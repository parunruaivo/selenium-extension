package com.testrigor.seleniumextension.commons.application.grpc;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.typesafe.config.Config;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcActionExecutionException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcInternalException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcIssuesDetectedException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcInvalidArgumentException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcMissingActionPayloadException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcNonRetryableTransportException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcNotFoundException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcRetryableTransportException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcServerStatusException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcStreamClosedException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.SeleniumExtensionException;
import com.testrigor.seleniumextension.commons.application.context.TestRigorContext;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcUnsupportedActionException;
import com.testrigor.seleniumextension.grpc.lib.ActionRequest;
import com.testrigor.seleniumextension.grpc.lib.ClientMessage;
import com.testrigor.seleniumextension.grpc.lib.ClientMessagePayload;
import com.testrigor.seleniumextension.grpc.lib.Driver;
import com.testrigor.seleniumextension.grpc.lib.ServerMessage;
import com.testrigor.seleniumextension.grpc.lib.TestRigorServiceGrpc;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
@SuppressWarnings({ "PMD.AvoidCatchingGenericException" })
public class TestRigorGrpcClient implements AutoCloseable {

	private static final Metadata.Key<String> API_TOKEN_KEY =
		Metadata.Key.of("api-token", Metadata.ASCII_STRING_MARSHALLER);

	final ManagedChannel channel;
	final Map<String, CompletableFuture<?>> futures = new ConcurrentHashMap<>();
	final GrpcDriverAdapter adapter;
	final String apiToken;
	StreamObserver<ClientMessage> clientMessageStreamObserver;
	final AtomicBoolean clientStreamClosed = new AtomicBoolean(true);
	final AtomicBoolean firstFailureLogged = new AtomicBoolean(false);
	volatile String activeOperation = "unknown";
	volatile String activeMessageId = "";
	volatile DriverInfo activeDriverInfo;

	public TestRigorGrpcClient(Config config, GrpcDriverAdapter aAdapter, String apiToken) {
		this(GrpcEndpointConfig.fromConfig(config), aAdapter, apiToken);
	}

	public TestRigorGrpcClient(GrpcEndpointConfig endpoint, GrpcDriverAdapter aAdapter, String apiToken) {
		adapter = aAdapter;
		this.apiToken = normalizeToken(apiToken);
		channel = buildChannel(endpoint);
	}

	private static ManagedChannel buildChannel(GrpcEndpointConfig endpoint) {
		NettyChannelBuilder channelBuilder = NettyChannelBuilder.forAddress(endpoint.getHost(), endpoint.getPort());
		if (endpoint.useTransportSecurity()) {
			channelBuilder.useTransportSecurity();
		} else {
			channelBuilder.usePlaintext();
		}
		return channelBuilder.build();
	}

	/**
	 * Uses TLS for Cloudflare/public endpoints (port 443) unless {@code testrigor.grpc.use-tls} overrides.
	 */
	static boolean useTls(Config config, int port) {
		Boolean explicit = config.hasPath("testrigor.grpc.use-tls") ? config.getBoolean("testrigor.grpc.use-tls") : null;
		return GrpcEndpointConfig.resolveUseTls(port, explicit);
	}

	private TestRigorServiceGrpc.TestRigorServiceStub stubWithAuth() {
		TestRigorServiceGrpc.TestRigorServiceStub stub = TestRigorServiceGrpc.newStub(channel);
		if (apiToken != null && !apiToken.isEmpty()) {
			Metadata metadata = new Metadata();
			metadata.put(API_TOKEN_KEY, apiToken);
			ClientInterceptor interceptor = MetadataUtils.newAttachHeadersInterceptor(metadata);
			stub = stub.withInterceptors(interceptor);
		}
		return stub;
	}

	static String normalizeToken(String token) {
		if (token == null) {
			return "";
		}
		return token.trim();
	}

	@SuppressWarnings("unchecked")
	public void processServerMessage(ServerMessage serverMessage) {
		log.trace("Received message from server: {}", serverMessage);

		switch (serverMessage.getMessageCase()) {
			case COMMAND:
				adapter.executeCommand(
					serverMessage.getId(),
					serverMessage.getCommand().getSessionId(),
					serverMessage.getCommand().getPayload().getName(),
					serverMessage.getCommand().getPayload().getParametersJson(),
					clientMessageStreamObserver);
				break;
			case RESULT:
				processResult(serverMessage);
				break;
			case STATUS:
				processStatus(serverMessage);
				break;
			default:
				log.error("Unknown message type " + serverMessage.getMessageCase());
				break;
		}
	}

	@SuppressWarnings("unchecked")
	private void processStatus(ServerMessage serverMessage) {
		com.google.rpc.Status status = serverMessage.getStatus();
		log.trace("Status code:" + Code.forNumber(status.getCode()));
		log.trace("Status message:" + status.getMessage());
		String errorMessage = status.getMessage();
		String reason = "";
		for (Any any : status.getDetailsList()) {
			// Avoid Any.is(...) reflection path to stay resilient to protobuf runtime skew.
			if (!any.getTypeUrl().endsWith("/google.rpc.ErrorInfo")) {
				continue;
			}
			try {
				ErrorInfo errorInfo = any.unpack(ErrorInfo.class);
				reason = errorInfo.getReason();
				if (errorMessage == null || errorMessage.isBlank()) {
					errorMessage = errorInfo.getReason();
				}
			} catch (InvalidProtocolBufferException e) {
				log.error(e.getMessage());
			} catch (LinkageError | RuntimeException e) {
				// Handles NoClassDefFoundError/ClassNotFoundException caused by mixed protobuf jars at runtime.
				log.warn("Unable to unpack status detail ErrorInfo due to runtime mismatch: {}", e.getMessage());
			}
		}
		CompletableFuture<?> future = futures.remove(serverMessage.getId());
		if (future != null) {
			future.completeExceptionally(toServerStatusException(
				status,
				errorMessage,
				reason,
				serverMessage.getId(),
				activeOperation
			));
		}
	}

	@SuppressWarnings("unchecked")
	private void processResult(ServerMessage serverMessage) {
		CompletableFuture<?> future = futures.remove(serverMessage.getId());
		if (future == null) {
			return;
		}
		var result = serverMessage.getResult();
		switch (result.getTypedValueCase()) {
			case ELEMENTXPATH:
				try {
					Object element = adapter.resolveElementFromXpath(result.getElementXpath());
					((CompletableFuture<Object>) future).complete(element);
				} catch (Exception e) {
					((CompletableFuture<Object>) future).completeExceptionally(e);
				}
				break;
			case STRINGVALUE:
				((CompletableFuture<Object>) future).complete(result.getStringValue());
				break;
			case BOOLVALUE:
				((CompletableFuture<Object>) future).complete(result.getBoolValue());
				break;
			case JSONVALUE:
				((CompletableFuture<Object>) future).complete(result.getJsonValue());
				break;
			case TYPEDVALUE_NOT_SET:
			default:
				if (result.hasValue()) {
					try {
						Object element = adapter.resolveElementFromXpath(result.getValue());
						((CompletableFuture<Object>) future).complete(element);
					} catch (Exception e) {
						((CompletableFuture<Object>) future).completeExceptionally(e);
					}
				} else {
					((CompletableFuture<Void>) future).complete(null);
				}
				break;
		}
	}

	public CompletableFuture<Object> findByUserDescription(String description) {
		String messageId = UUID.randomUUID().toString();
		CompletableFuture<Object> completableFuture = new CompletableFuture<>();
		futures.put(messageId, completableFuture);
		activeOperation = "findElement";
		activeMessageId = messageId;
		activeDriverInfo = null;
		firstFailureLogged.set(false);
		clientMessageStreamObserver = stubWithAuth().findElement(serverStreamObserver());
		clientStreamClosed.set(false);

		final String capturedTestId = TestRigorContext.getTestId();
		CompletableFuture.runAsync(() -> {
			try {
				DriverInfo info = adapter.getDriverInfo(capturedTestId);
				activeDriverInfo = info;
				logSendStart(messageId, info);
				Driver driver = buildDriver(info);
				ClientMessage clientMessage = ClientMessage.newBuilder()
					.setId(messageId)
					.setPayload(ClientMessagePayload.newBuilder()
						.setDriver(driver)
						.setMessage(description)
						.build())
					.build();
				clientMessageStreamObserver.onNext(clientMessage);
			} catch (RuntimeException e) {
				log.error(e.getMessage(), e);
				abortClientStream(e);
			}
		});

		return completableFuture;
	}

	public CompletableFuture<Void> executePrompt(String prompt) {
		String messageId = UUID.randomUUID().toString();
		CompletableFuture<Void> completableFuture = new CompletableFuture<>();
		futures.put(messageId, completableFuture);
		activeOperation = "executePrompt";
		activeMessageId = messageId;
		activeDriverInfo = null;
		firstFailureLogged.set(false);
		clientMessageStreamObserver = stubWithAuth().executePrompt(serverStreamObserver());
		clientStreamClosed.set(false);

		final String capturedTestId = TestRigorContext.getTestId();
		CompletableFuture.runAsync(() -> {
			try {
				DriverInfo info = adapter.getDriverInfo(capturedTestId);
				activeDriverInfo = info;
				logSendStart(messageId, info);
				Driver driver = buildDriver(info);
				ClientMessage clientMessage = ClientMessage.newBuilder()
					.setId(messageId)
					.setPayload(ClientMessagePayload.newBuilder()
						.setDriver(driver)
						.setMessage(prompt)
						.build())
					.build();
				clientMessageStreamObserver.onNext(clientMessage);
			} catch (RuntimeException e) {
				log.error(e.getMessage(), e);
				abortClientStream(e);
			}
		});

		return completableFuture;
	}

	public CompletableFuture<Object> executeAction(String actionName, String parametersJson) {
		String messageId = UUID.randomUUID().toString();
		CompletableFuture<Object> completableFuture = new CompletableFuture<>();
		futures.put(messageId, completableFuture);
		activeOperation = "executeAction";
		activeMessageId = messageId;
		activeDriverInfo = null;
		firstFailureLogged.set(false);
		clientMessageStreamObserver = stubWithAuth().executeAction(serverStreamObserver());
		clientStreamClosed.set(false);

		final String capturedTestId = TestRigorContext.getTestId();
		CompletableFuture.runAsync(() -> {
			try {
				DriverInfo info = adapter.getDriverInfo(capturedTestId);
				activeDriverInfo = info;
				logSendStart(messageId, info);
				Driver driver = buildDriver(info);
				ClientMessage clientMessage = ClientMessage.newBuilder()
					.setId(messageId)
					.setPayload(ClientMessagePayload.newBuilder()
						.setDriver(driver)
						.setAction(ActionRequest.newBuilder()
							.setName(actionName)
							.setParametersJson(parametersJson == null ? "" : parametersJson)
							.build())
						.build())
					.build();
				clientMessageStreamObserver.onNext(clientMessage);
			} catch (RuntimeException e) {
				log.error(e.getMessage(), e);
				abortClientStream(e);
			}
		});

		return completableFuture;
	}

	@Override
	public void close() throws Exception {
		completeClientStream();
		channel.shutdown();
		try {
			if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
				channel.shutdownNow();
				channel.awaitTermination(5, TimeUnit.SECONDS);
			}
		} catch (InterruptedException e) {
			channel.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	private static boolean isChannelShutdownError(Throwable throwable) {
		if (!(throwable instanceof StatusRuntimeException)) {
			return false;
		}
		Status status = ((StatusRuntimeException) throwable).getStatus();
		if (status.getCode() != Status.Code.UNAVAILABLE) {
			return false;
		}
		String message = status.getDescription();
		return message != null && message.contains("Channel shutdownNow invoked");
	}

	private StreamObserver<ServerMessage> serverStreamObserver() {
		return new StreamObserver<>() {
			@Override
			public void onNext(ServerMessage serverMessage) {
				processServerMessage(serverMessage);
			}

			@Override
			public void onError(Throwable throwable) {
				handleStreamFailure(throwable);
			}

			@Override
			public void onCompleted() {
				clientStreamClosed.set(true);
				GrpcStreamClosedException failure = new GrpcStreamClosedException(
					"gRPC stream completed before all responses were received",
					activeOperation,
					activeMessageId
				);
				failAllPendingFutures(failure);
				log.debug("gRPC stream completed. operation={}, messageId={}", activeOperation, activeMessageId);
			}
		};
	}

	private void handleStreamFailure(Throwable throwable) {
		clientStreamClosed.set(true);
		Throwable root = throwable == null ? new IllegalStateException("Unknown gRPC stream failure") : throwable;
		boolean retryable = isRetryableTransportFailure(root);
		logStreamFailure(root, retryable);
		failAllPendingFutures(toTransportException(root, retryable));
	}

	private void failAllPendingFutures(Throwable throwable) {
		if (futures.isEmpty()) {
			return;
		}
		for (CompletableFuture<?> future : futures.values()) {
			future.completeExceptionally(throwable);
		}
		futures.clear();
	}

	private void logStreamFailure(Throwable throwable, boolean retryable) {
		String statusCode = grpcStatusCode(throwable);
		String description = grpcStatusDescription(throwable);
		String context = streamContext();
		if (firstFailureLogged.compareAndSet(false, true)) {
			log.error(
				"gRPC stream failure. operation={}, messageId={}, status={}, retryable={}, description='{}'{}",
				activeOperation,
				activeMessageId,
				statusCode,
				retryable,
				description == null ? "" : description,
				context,
				throwable
			);
			return;
		}
		log.debug(
			"gRPC stream failure (suppressed duplicate). operation={}, messageId={}, status={}, retryable={}, description='{}'{}",
			activeOperation,
			activeMessageId,
			statusCode,
			retryable,
			description == null ? "" : description,
			context
		);
	}

	private void logSendStart(String messageId, DriverInfo info) {
		log.debug(
			"Sending gRPC request. operation={}, messageId={}, sessionId={}, testId={}",
			activeOperation,
			messageId,
			info == null ? "" : safe(info.getSessionId()),
			info == null ? "" : safe(info.getTestId())
		);
	}

	private String buildFailureMessage(Throwable throwable, boolean retryable) {
		return String.format(
			"gRPC stream failed. operation=%s, messageId=%s, status=%s, retryable=%s, description=%s",
			activeOperation,
			activeMessageId,
			grpcStatusCode(throwable),
			retryable,
			grpcStatusDescription(throwable)
		);
	}

	private SeleniumExtensionException toTransportException(Throwable throwable, boolean retryable) {
		String statusCode = grpcStatusCode(throwable);
		String statusDescription = grpcStatusDescription(throwable);
		String message = buildFailureMessage(throwable, retryable);
		if (retryable) {
			return new GrpcRetryableTransportException(
				message,
				throwable,
				activeOperation,
				activeMessageId,
				statusCode,
				statusDescription
			);
		}
		return new GrpcNonRetryableTransportException(
			message,
			throwable,
			activeOperation,
			activeMessageId,
			statusCode,
			statusDescription
		);
	}

	private SeleniumExtensionException toServerStatusException(com.google.rpc.Status status, String errorMessage,
			String reason, String messageId, String operation) {
		Code code = Code.forNumber(status.getCode());
		if (code == null) {
			code = Code.UNKNOWN;
		}
		String safeMessage = errorMessage == null ? "" : errorMessage;
		String safeReason = reason == null ? "" : reason;
		switch (code) {
			case INVALID_ARGUMENT:
				if (matchesReason(safeReason, "Unsupported action")) {
					return new GrpcUnsupportedActionException(safeMessage, safeReason, operation, messageId);
				}
				if (matchesReason(safeReason, "Missing action payload")) {
					return new GrpcMissingActionPayloadException(safeMessage, safeReason, operation, messageId);
				}
				return new GrpcInvalidArgumentException(safeMessage, safeReason, operation, messageId);
			case NOT_FOUND:
				return new GrpcNotFoundException(safeMessage, safeReason, operation, messageId);
			case INTERNAL:
				if (matchesReason(safeReason, "Issues detected")) {
					return new GrpcIssuesDetectedException(safeMessage, safeReason, operation, messageId);
				}
				if (matchesReason(safeReason, "Error when processing action")) {
					return new GrpcActionExecutionException(safeMessage, safeReason, operation, messageId);
				}
				return new GrpcInternalException(
					safeMessage,
					safeReason,
					operation,
					messageId
				);
			default:
				return new GrpcServerStatusException(safeMessage, code, safeReason, operation, messageId);
		}
	}

	private static boolean matchesReason(String actualReason, String expectedReason) {
		return actualReason != null && actualReason.equalsIgnoreCase(expectedReason);
	}

	private String streamContext() {
		DriverInfo info = activeDriverInfo;
		if (info == null) {
			return "";
		}
		return String.format(", sessionId=%s, testId=%s",
			safe(info.getSessionId()),
			safe(info.getTestId()));
	}

	private static String safe(String value) {
		return value == null ? "" : value;
	}

	static String grpcStatusCode(Throwable throwable) {
		StatusRuntimeException statusRuntimeException = findCause(throwable, StatusRuntimeException.class);
		if (statusRuntimeException == null) {
			return "UNKNOWN";
		}
		return statusRuntimeException.getStatus().getCode().name();
	}

	private static String grpcStatusDescription(Throwable throwable) {
		StatusRuntimeException statusRuntimeException = findCause(throwable, StatusRuntimeException.class);
		if (statusRuntimeException == null || statusRuntimeException.getStatus() == null) {
			return throwable == null ? "" : String.valueOf(throwable.getMessage());
		}
		String description = statusRuntimeException.getStatus().getDescription();
		return description == null ? "" : description;
	}

	public static boolean isRetryableTransportFailure(Throwable throwable) {
		StatusRuntimeException statusRuntimeException = findCause(throwable, StatusRuntimeException.class);
		if (statusRuntimeException == null || statusRuntimeException.getStatus() == null) {
			return false;
		}

		Status.Code statusCode = statusRuntimeException.getStatus().getCode();
		if ((statusCode == Status.Code.CANCELLED) || (statusCode == Status.Code.UNAVAILABLE)) {
			return true;
		}
		if (statusCode != Status.Code.INTERNAL) {
			return false;
		}

		String description = statusRuntimeException.getStatus().getDescription();
		if (description == null) {
			return false;
		}
		String normalized = description.toLowerCase();
		return normalized.contains("rst_stream")
			|| normalized.contains("http/2 error code: cancel")
			|| normalized.contains("stream closed")
			|| normalized.contains("channel shutdown")
			|| normalized.contains("call already cancelled")
			|| normalized.contains("client cancelled");
	}

	private static <T extends Throwable> T findCause(Throwable throwable, Class<T> type) {
		Throwable current = throwable;
		while (current != null) {
			if (type.isInstance(current)) {
				return type.cast(current);
			}
			current = current.getCause();
		}
		return null;
	}

	private void completeClientStream() {
		StreamObserver<ClientMessage> observer = clientMessageStreamObserver;
		if (observer == null || !clientStreamClosed.compareAndSet(false, true)) {
			return;
		}
		try {
			observer.onCompleted();
		} catch (IllegalStateException e) {
			log.trace("Client stream already half-closed: {}", e.getMessage());
		}
	}

	private void abortClientStream(Throwable throwable) {
		StreamObserver<ClientMessage> observer = clientMessageStreamObserver;
		if (observer == null || !clientStreamClosed.compareAndSet(false, true)) {
			return;
		}
		try {
			observer.onError(throwable);
		} catch (IllegalStateException e) {
			log.trace("Client stream already closed while aborting: {}", e.getMessage());
		}
	}

	private Driver buildDriver(DriverInfo info) {
		Driver.Builder builder = Driver.newBuilder()
			.setSessionId(info.getSessionId())
			.setCapabilitiesJson(info.getCapabilitiesJson());
		if (info.getTestId() != null && !info.getTestId().isBlank()) {
			builder.setTestId(info.getTestId());
		}
		return builder.build();
	}
}
