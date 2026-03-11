package com.testrigor.seleniumextension.commons.application.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.mockito.Mockito;
import org.testng.annotations.Test;

import com.google.rpc.Code;
import com.google.rpc.Status;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcInternalException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcIssuesDetectedException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcRetryableTransportException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcUnsupportedActionException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.SeleniumExtensionException;
import com.testrigor.seleniumextension.grpc.lib.Result;
import com.testrigor.seleniumextension.grpc.lib.ServerMessage;
import com.typesafe.config.Config;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

@SuppressWarnings("checkstyle:MagicNumber")
public class TestRigorGrpcClientTest {

	private static void stubGrpcUriAndPort(Config config, String host, int port) {
		when(config.hasPath("testrigor.grpc.uri")).thenReturn(true);
		when(config.hasPath("testrigor.grpc.port")).thenReturn(true);
		when(config.getString("testrigor.grpc.uri")).thenReturn(host);
		when(config.getInt("testrigor.grpc.port")).thenReturn(port);
		when(config.hasPath("testrigor.grpc.use-tls")).thenReturn(false);
	}

	@Test
	public void constructor_acceptsGrpcEndpointConfigWithoutConfigMock() throws Exception {
		TestRigorGrpcClient client = new TestRigorGrpcClient(
			GrpcEndpointConfig.of("localhost", 9091, false),
			Mockito.mock(GrpcDriverAdapter.class),
			"token");
		try {
			assertThat(readPrivateStringField(client, "apiToken")).isEqualTo("token");
		} finally {
			client.close();
		}
	}

	@Test
	public void normalizeToken_trimsLeadingAndTrailingWhitespace() {
		assertThat(TestRigorGrpcClient.normalizeToken("  token-value  ")).isEqualTo("token-value");
	}

	@Test
	public void useTls_defaultsToTrueOnPort443WhenPropertyAbsent() {
		Config config = mock(Config.class);
		when(config.hasPath("testrigor.grpc.use-tls")).thenReturn(false);
		assertThat(TestRigorGrpcClient.useTls(config, 443)).isTrue();
	}

	@Test
	public void useTls_defaultsToFalseOnNon443WhenPropertyAbsent() {
		Config config = mock(Config.class);
		when(config.hasPath("testrigor.grpc.use-tls")).thenReturn(false);
		assertThat(TestRigorGrpcClient.useTls(config, 9091)).isFalse();
	}

	@Test
	public void useTls_explicitPropertyOverridesPort() {
		Config config = mock(Config.class);
		when(config.hasPath("testrigor.grpc.use-tls")).thenReturn(true);
		when(config.getBoolean("testrigor.grpc.use-tls")).thenReturn(false);
		assertThat(TestRigorGrpcClient.useTls(config, 443)).isFalse();
		when(config.getBoolean("testrigor.grpc.use-tls")).thenReturn(true);
		assertThat(TestRigorGrpcClient.useTls(config, 9091)).isTrue();
	}

	@Test
	public void constructor_storesTrimmedTokenForMetadataUsage() throws Exception {
		Config config = Mockito.mock(Config.class);
		stubGrpcUriAndPort(config, "localhost", 9091);

		TestRigorGrpcClient client = new TestRigorGrpcClient(
			config,
			Mockito.mock(GrpcDriverAdapter.class),
			"  api-token  ");
		try {
			assertThat(readPrivateStringField(client, "apiToken")).isEqualTo("api-token");
		} finally {
			client.close();
		}
	}

	@Test
	public void constructor_convertsBlankTokenToEmptySoHeaderIsNotAttached() throws Exception {
		Config config = Mockito.mock(Config.class);
		stubGrpcUriAndPort(config, "localhost", 9091);

		TestRigorGrpcClient client = new TestRigorGrpcClient(
			config,
			Mockito.mock(GrpcDriverAdapter.class),
			"   ");
		try {
			assertThat(readPrivateStringField(client, "apiToken")).isEmpty();
		} finally {
			client.close();
		}
	}

	@Test
	public void processStatus_keepsServerMessageAsPrimaryError() throws Exception {
		Config config = Mockito.mock(Config.class);
		stubGrpcUriAndPort(config, "localhost", 9091);
		TestRigorGrpcClient client = new TestRigorGrpcClient(config, Mockito.mock(GrpcDriverAdapter.class), "token");
		try {
			CompletableFuture<Object> future = new CompletableFuture<>();
			futures(client).put("status-id", future);

			ServerMessage message = ServerMessage.newBuilder()
				.setId("status-id")
				.setStatus(Status.newBuilder()
					.setCode(Code.INTERNAL.getNumber())
					.setMessage("Page does not contain expected value")
					.build())
				.build();

			client.processServerMessage(message);

			assertThat(future).isCompletedExceptionally();
			assertThatThrownBy(future::join)
				.hasCauseInstanceOf(GrpcInternalException.class)
				.satisfies(throwable -> {
					GrpcInternalException cause = (GrpcInternalException) throwable.getCause();
					assertThat(cause.getMessage()).isEqualTo("Page does not contain expected value");
					assertThat(cause.getOperation()).isEqualTo("unknown");
					assertThat(cause.getMessageId()).isEqualTo("status-id");
				});
		} finally {
			client.close();
		}
	}

	@Test
	public void processStatus_mapsUnsupportedActionToSpecificException() throws Exception {
		Config config = Mockito.mock(Config.class);
		stubGrpcUriAndPort(config, "localhost", 9091);
		TestRigorGrpcClient client = new TestRigorGrpcClient(config, Mockito.mock(GrpcDriverAdapter.class), "token");
		try {
			Status status = Status.newBuilder()
				.setCode(Code.INVALID_ARGUMENT.getNumber())
				.setMessage("Unsupported action: unknownAction")
				.build();
			SeleniumExtensionException mapped = (SeleniumExtensionException) invokePrivate(
				client,
				"toServerStatusException",
				new Class<?>[] { Status.class, String.class, String.class, String.class, String.class },
				new Object[] { status, status.getMessage(), "Unsupported action", "unsupported-id", "executeAction" }
			);
			assertThat(mapped).isInstanceOf(GrpcUnsupportedActionException.class);
		} finally {
			client.close();
		}
	}

	@Test
	public void processStatus_mapsIssuesDetectedToSpecificException() throws Exception {
		Config config = Mockito.mock(Config.class);
		stubGrpcUriAndPort(config, "localhost", 9091);
		TestRigorGrpcClient client = new TestRigorGrpcClient(config, Mockito.mock(GrpcDriverAdapter.class), "token");
		try {
			Status status = Status.newBuilder()
				.setCode(Code.INTERNAL.getNumber())
				.setMessage("Validation issues")
				.build();
			SeleniumExtensionException mapped = (SeleniumExtensionException) invokePrivate(
				client,
				"toServerStatusException",
				new Class<?>[] { Status.class, String.class, String.class, String.class, String.class },
				new Object[] { status, status.getMessage(), "Issues detected", "issues-id", "executePrompt" }
			);
			assertThat(mapped).isInstanceOf(GrpcIssuesDetectedException.class);
		} finally {
			client.close();
		}
	}

	@Test
	public void processResult_resolvesTypedElementXpath() throws Exception {
		Config config = Mockito.mock(Config.class);
		GrpcDriverAdapter adapter = Mockito.mock(GrpcDriverAdapter.class);
		stubGrpcUriAndPort(config, "localhost", 9091);
		when(adapter.resolveElementFromXpath("//button")).thenReturn("element-proxy");
		TestRigorGrpcClient client = new TestRigorGrpcClient(config, adapter, "token");
		try {
			CompletableFuture<Object> future = new CompletableFuture<>();
			futures(client).put("result-id", future);
			ServerMessage message = ServerMessage.newBuilder()
				.setId("result-id")
				.setResult(Result.newBuilder().setElementXpath("//button").build())
				.build();

			client.processServerMessage(message);
			assertThat(future.join()).isEqualTo("element-proxy");
		} finally {
			client.close();
		}
	}

	@Test
	public void processResult_returnsTypedStringValue() throws Exception {
		Config config = Mockito.mock(Config.class);
		stubGrpcUriAndPort(config, "localhost", 9091);
		TestRigorGrpcClient client = new TestRigorGrpcClient(config, Mockito.mock(GrpcDriverAdapter.class), "token");
		try {
			CompletableFuture<Object> future = new CompletableFuture<>();
			futures(client).put("string-id", future);
			ServerMessage message = ServerMessage.newBuilder()
				.setId("string-id")
				.setResult(Result.newBuilder().setStringValue("expected").build())
				.build();

			client.processServerMessage(message);
			assertThat(future.join()).isEqualTo("expected");
		} finally {
			client.close();
		}
	}

	@Test
	public void retryableTransportFailure_returnsTrue_forCancelledStreamReset() {
		StatusRuntimeException cancelled = io.grpc.Status.CANCELLED
			.withDescription("RST_STREAM closed stream. HTTP/2 error code: CANCEL")
			.asRuntimeException();
		assertThat(TestRigorGrpcClient.isRetryableTransportFailure(cancelled)).isTrue();
	}

	@Test
	public void retryableTransportFailure_returnsFalse_forUnknownApplicationError() {
		StatusRuntimeException invalidArgument = io.grpc.Status.INVALID_ARGUMENT
			.withDescription("Unsupported action")
			.asRuntimeException();
		assertThat(TestRigorGrpcClient.isRetryableTransportFailure(invalidArgument)).isFalse();
	}

	@Test
	public void streamOnError_completesPendingFutureExceptionally_withRetryableTransportException() throws Exception {
		Config config = Mockito.mock(Config.class);
		stubGrpcUriAndPort(config, "localhost", 9091);
		TestRigorGrpcClient client = new TestRigorGrpcClient(config, Mockito.mock(GrpcDriverAdapter.class), "token");
		try {
			CompletableFuture<Object> future = new CompletableFuture<>();
			futures(client).put("pending-id", future);

			@SuppressWarnings("unchecked")
			StreamObserver<ServerMessage> observer = (StreamObserver<ServerMessage>) invokePrivateNoArgs(client, "serverStreamObserver");
			observer.onError(io.grpc.Status.CANCELLED.withDescription("client cancelled").asRuntimeException());

			assertThat(future).isCompletedExceptionally();
			assertThatThrownBy(future::join)
				.hasCauseInstanceOf(GrpcRetryableTransportException.class)
				.satisfies(throwable -> {
					GrpcRetryableTransportException cause = (GrpcRetryableTransportException) throwable.getCause();
					assertThat(cause.isRetryable()).isTrue();
					assertThat(cause.getMessage()).contains("retryable=true");
				});
		} finally {
			client.close();
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, CompletableFuture<?>> futures(TestRigorGrpcClient client) throws ReflectiveOperationException {
		var field = client.getClass().getDeclaredField("futures");
		field.setAccessible(true);
		return (Map<String, CompletableFuture<?>>) field.get(client);
	}

	private String readPrivateStringField(Object target, String fieldName) throws ReflectiveOperationException {
		var field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return (String) field.get(target);
	}

	private Object invokePrivateNoArgs(Object target, String methodName) throws ReflectiveOperationException {
		var method = target.getClass().getDeclaredMethod(methodName);
		method.setAccessible(true);
		return method.invoke(target);
	}

	private Object invokePrivate(Object target, String methodName, Class<?>[] parameterTypes, Object[] args)
			throws ReflectiveOperationException {
		var method = target.getClass().getDeclaredMethod(methodName, parameterTypes);
		method.setAccessible(true);
		return method.invoke(target, args);
	}
}
