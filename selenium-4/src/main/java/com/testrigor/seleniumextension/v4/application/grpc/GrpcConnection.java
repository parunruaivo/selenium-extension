package com.testrigor.seleniumextension.v4.application.grpc;

import static org.openqa.selenium.json.Json.MAP_TYPE;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandPayload;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;

import com.typesafe.config.Config;
import com.testrigor.seleniumextension.commons.application.context.TestRigorContext;
import com.testrigor.seleniumextension.commons.application.grpc.DriverCommandValueCodec;
import com.testrigor.seleniumextension.commons.application.grpc.DriverInfo;
import com.testrigor.seleniumextension.commons.application.grpc.GrpcDriverAdapter;
import com.testrigor.seleniumextension.commons.application.grpc.GrpcEndpointConfig;
import com.testrigor.seleniumextension.commons.application.grpc.TestRigorGrpcClient;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.SeleniumExtensionException;
import com.testrigor.seleniumextension.grpc.lib.ClientMessage;
import com.testrigor.seleniumextension.grpc.lib.ClientMessagePayload;
import com.testrigor.seleniumextension.grpc.lib.DriverCommandResponse;

import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
@SuppressWarnings({ "PMD.AvoidCatchingGenericException" })
public class GrpcConnection implements GrpcDriverAdapter, AutoCloseable {

	private static final Json JSON = new Json();

	final RemoteWebDriver remoteWebDriver;
	final GrpcEndpointConfig grpcEndpoint;
	final String apiToken;
	volatile TestRigorGrpcClient client;
	volatile boolean closed;

	public GrpcConnection(RemoteWebDriver aRemoteWebDriver, Config aConfig, String apiToken) {
		this(aRemoteWebDriver, GrpcEndpointConfig.fromConfig(aConfig), apiToken);
	}

	public GrpcConnection(RemoteWebDriver aRemoteWebDriver, GrpcEndpointConfig aGrpcEndpoint, String apiToken) {
		remoteWebDriver = aRemoteWebDriver;
		grpcEndpoint = aGrpcEndpoint;
		this.apiToken = apiToken;
		client = new TestRigorGrpcClient(grpcEndpoint, this, apiToken);
	}

	@Override
	public DriverInfo getDriverInfo(String testIdOverride) {
		String sessionId = remoteWebDriver.getSessionId().toString();
		Map<String, Object> capabilityMap = new HashMap<>(remoteWebDriver.getCapabilities().asMap());
		String browserName = remoteWebDriver.getCapabilities().getBrowserName();
		if (isNotBlank(browserName) && !capabilityMap.containsKey("browserName")) {
			capabilityMap.put("browserName", browserName);
		}
		String capabilitiesJson = JSON.toJson(capabilityMap);
		String testId = (testIdOverride != null && !testIdOverride.isBlank())
			? testIdOverride
			: TestRigorContext.getTestId();
		return new DriverInfo(sessionId, capabilitiesJson, testId);
	}

	@Override
	@SuppressWarnings({ "PMD.UseObjectForClearerAPI", "PMD.AvoidCatchingGenericException" })
	public void executeCommand(String messageId, String sessionId, String commandName, String parametersJson,
			StreamObserver<ClientMessage> responseObserver) {
		Map<String, Object> parameters = JSON.toType(parametersJson, MAP_TYPE);
		Command command = new Command(
			new SessionId(sessionId),
			new CommandPayload(commandName, parameters));
		try {
			Response response = remoteWebDriver.getCommandExecutor().execute(command);
			String valueJson = response.getValue() == null ? "" : JSON.toJson(response.getValue());
			String responseSessionId = response.getSessionId() == null ? "" : response.getSessionId().toString();
			Integer responseStatus = response.getStatus() == null ? 0 : response.getStatus();
			String responseState = response.getState() == null ? "" : response.getState();
			DriverCommandResponse.Builder responseBuilder = DriverCommandResponse.newBuilder()
				.setSessionId(responseSessionId)
				.setStatus(responseStatus)
				.setState(responseState);
			DriverCommandValueCodec.setEncodedValue(responseBuilder, valueJson);
			DriverCommandResponse driverResponse = responseBuilder.build();
			responseObserver.onNext(ClientMessage.newBuilder()
				.setId(messageId)
				.setPayload(ClientMessagePayload.newBuilder().setResponse(driverResponse).build())
				.build());
		} catch (RuntimeException | IOException e) {
			log.error(e.getMessage());
			responseObserver.onError(e);
		}
	}

	@Override
	public Object resolveElementFromXpath(String xpath) {
		return remoteWebDriver.findElement(By.xpath(xpath));
	}

	public CompletableFuture<WebElement> findByUserDescription(String description) {
		return client.findByUserDescription(description)
			.thenApply(obj -> (WebElement) obj);
	}

	public CompletableFuture<Void> executePrompt(String prompt) {
		return client.executePrompt(prompt);
	}

	public CompletableFuture<Object> executeAction(String actionName, Map<String, Object> parameters) {
		String parametersJson = parameters == null ? "" : JSON.toJson(parameters);
		return client.executeAction(actionName, parametersJson);
	}

	public synchronized void reconnect() {
		if (closed) {
			throw new SeleniumExtensionException("Driver connection already closed");
		}
		log.warn("Reconnecting gRPC client for session {}", remoteWebDriver.getSessionId());
		safelyCloseClient(client);
		client = new TestRigorGrpcClient(grpcEndpoint, this, apiToken);
	}

	public boolean isRetryableTransportFailure(Throwable throwable) {
		return TestRigorGrpcClient.isRetryableTransportFailure(throwable);
	}

	@Override
	public synchronized void close() throws Exception {
		if (closed) {
			return;
		}
		closed = true;
		safelyCloseClient(client);
		client = null;
	}

	private void safelyCloseClient(TestRigorGrpcClient clientToClose) {
		if (clientToClose == null) {
			return;
		}
		try {
			clientToClose.close();
		} catch (Exception e) {
			log.warn("Error closing gRPC client: {}", e.getMessage());
		}
	}

	private static boolean isNotBlank(String value) {
		return value != null && !value.trim().isEmpty();
	}
}
