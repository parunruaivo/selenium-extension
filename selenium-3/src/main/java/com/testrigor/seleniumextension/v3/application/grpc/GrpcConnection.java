package com.testrigor.seleniumextension.v3.application.grpc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.remote.Command;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testrigor.seleniumextension.commons.application.context.TestRigorContext;
import com.testrigor.seleniumextension.commons.application.grpc.DriverCommandValueCodec;
import com.testrigor.seleniumextension.commons.application.grpc.DriverInfo;
import com.testrigor.seleniumextension.commons.application.grpc.GrpcDriverAdapter;
import com.testrigor.seleniumextension.commons.application.grpc.GrpcEndpointConfig;
import com.testrigor.seleniumextension.commons.application.grpc.TestRigorGrpcClient;
import com.testrigor.seleniumextension.commons.application.utils.JsonHelpers;
import com.testrigor.seleniumextension.grpc.lib.ClientMessage;
import com.testrigor.seleniumextension.grpc.lib.ClientMessagePayload;
import com.testrigor.seleniumextension.grpc.lib.DriverCommandResponse;

import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
public class GrpcConnection implements GrpcDriverAdapter, AutoCloseable {

	final RemoteWebDriver remoteWebDriver;
	final TestRigorGrpcClient client;

	public GrpcConnection(RemoteWebDriver aRemoteWebDriver, com.typesafe.config.Config config, String apiToken) {
		this(aRemoteWebDriver, GrpcEndpointConfig.fromConfig(config), apiToken);
	}

	public GrpcConnection(RemoteWebDriver aRemoteWebDriver, GrpcEndpointConfig aGrpcEndpoint, String apiToken) {
		remoteWebDriver = aRemoteWebDriver;
		client = new TestRigorGrpcClient(aGrpcEndpoint, this, apiToken);
	}

	@Override
	public DriverInfo getDriverInfo(String testIdOverride) {
		String sessionId = remoteWebDriver.getSessionId().toString();
		Map<String, Object> capabilityMap = new HashMap<>(remoteWebDriver.getCapabilities().asMap());
		String browserName = remoteWebDriver.getCapabilities().getBrowserName();
		if (isNotBlank(browserName) && !capabilityMap.containsKey("browserName")) {
			capabilityMap.put("browserName", browserName);
		}
		String capabilitiesJson = JsonHelpers.serializeJson(capabilityMap);
		String testId = (testIdOverride != null && !testIdOverride.isBlank())
			? testIdOverride
			: TestRigorContext.getTestId();
		return new DriverInfo(sessionId, capabilitiesJson, testId);
	}

	@Override
	@SuppressWarnings({ "PMD.UseObjectForClearerAPI", "PMD.AvoidCatchingGenericException" })
	public void executeCommand(String messageId, String sessionId, String commandName, String parametersJson,
			StreamObserver<ClientMessage> responseObserver) {
		Map<String, Object> parameters = JsonHelpers.deserializeJson(parametersJson, new TypeReference<>() { });
		Command command = new Command(new SessionId(sessionId), commandName, parameters);
		try {
			Response response = remoteWebDriver.getCommandExecutor().execute(command);
			String valueJson = response.getValue() == null ? "" : JsonHelpers.serializeJson(response.getValue());
			String responseSessionId = response.getSessionId() == null ? "" : response.getSessionId();
			int responseStatus = response.getStatus() == null ? 0 : response.getStatus();
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
		String parametersJson = parameters == null ? "" : JsonHelpers.serializeJson(parameters);
		return client.executeAction(actionName, parametersJson);
	}

	@Override
	public void close() throws Exception {
		client.close();
	}

	private static boolean isNotBlank(String value) {
		return value != null && !value.trim().isEmpty();
	}
}
