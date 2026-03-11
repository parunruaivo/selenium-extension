package com.testrigor.seleniumextension.v4.application.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcNonRetryableTransportException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcRetryableTransportException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.SeleniumExtensionException;
import com.testrigor.seleniumextension.v4.application.grpc.GrpcConnection;
import com.typesafe.config.ConfigFactory;

import io.grpc.Status;

@SuppressWarnings("checkstyle:MagicNumber")
public class SeleniumExtensionServiceReconnectTest {

	@Test
	public void executeAction_reconnectsAndRetriesOnce_whenFailureIsRetryable() throws Exception {
		SeleniumExtensionService service = createService();
		FakeGrpcConnection grpcConnection = new FakeGrpcConnection(service.getDriver());
		setField(service, "grpcConnection", grpcConnection);

		grpcConnection.retryable = false;
		grpcConnection.enqueueActionResult(failedFuture(new GrpcRetryableTransportException(
			"retryable transport",
			Status.CANCELLED.withDescription("RST_STREAM closed stream").asRuntimeException(),
			"executeAction",
			"message-id",
			"CANCELLED",
			"RST_STREAM closed stream")));
		grpcConnection.enqueueActionResult(CompletableFuture.completedFuture(null));

		service.click("Login");

		assertThat(grpcConnection.executeActionCalls).isEqualTo(2);
		assertThat(grpcConnection.reconnectCalls).isEqualTo(1);
	}

	@Test
	public void executeAction_doesNotReconnect_whenFailureIsNotRetryable() throws Exception {
		SeleniumExtensionService service = createService();
		FakeGrpcConnection grpcConnection = new FakeGrpcConnection(service.getDriver());
		setField(service, "grpcConnection", grpcConnection);

		grpcConnection.retryable = true;
		grpcConnection.enqueueActionResult(failedFuture(new GrpcNonRetryableTransportException(
			"non-retryable transport",
			Status.INVALID_ARGUMENT.withDescription("Invalid action").asRuntimeException(),
			"executeAction",
			"message-id",
			"INVALID_ARGUMENT",
			"Invalid action")));

		assertThatThrownBy(() -> service.click("Login"))
			.isInstanceOf(SeleniumExtensionException.class);

		assertThat(grpcConnection.executeActionCalls).isEqualTo(1);
		assertThat(grpcConnection.reconnectCalls).isZero();
	}

	@Test
	public void validationAction_throwsAssertionError_afterSingleReconnectRetry() throws Exception {
		SeleniumExtensionService service = createService();
		FakeGrpcConnection grpcConnection = new FakeGrpcConnection(service.getDriver());
		setField(service, "grpcConnection", grpcConnection);

		grpcConnection.retryable = true;
		grpcConnection.enqueueActionResult(failedFuture(Status.CANCELLED.withDescription("client cancelled").asRuntimeException()));
		grpcConnection.enqueueActionResult(failedFuture(new SeleniumExtensionException("still failing")));

		assertThatThrownBy(() -> service.checkPageContains("Expected text"))
			.isInstanceOf(AssertionError.class)
			.hasCauseInstanceOf(SeleniumExtensionException.class);

		assertThat(grpcConnection.executeActionCalls).isEqualTo(2);
		assertThat(grpcConnection.reconnectCalls).isEqualTo(1);
	}

	private SeleniumExtensionService createService() throws Exception {
		RemoteWebDriver driver = new StubRemoteWebDriver();
		return new SeleniumExtensionService(driver, "token");
	}

	private static final class StubRemoteWebDriver extends RemoteWebDriver {
		StubRemoteWebDriver() {
			super();
		}
	}

	private static <T> CompletableFuture<T> failedFuture(Throwable throwable) {
		CompletableFuture<T> future = new CompletableFuture<>();
		future.completeExceptionally(throwable);
		return future;
	}

	private void setField(Object target, String fieldName, Object value) throws Exception {
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}

	private static final class FakeGrpcConnection extends GrpcConnection {
		private final Deque<CompletableFuture<Object>> actionResults = new ArrayDeque<>();
		private int reconnectCalls;
		private int executeActionCalls;
		private boolean retryable;

		FakeGrpcConnection(RemoteWebDriver driver) {
			super(
				driver,
				ConfigFactory.parseString("testrigor.grpc.uri=\"localhost\"\ntestrigor.grpc.port=9091"),
				"token");
		}

		void enqueueActionResult(CompletableFuture<Object> result) {
			actionResults.add(result);
		}

		@Override
		public CompletableFuture<Object> executeAction(String actionName, Map<String, Object> parameters) {
			executeActionCalls++;
			CompletableFuture<Object> result = actionResults.pollFirst();
			return result == null ? CompletableFuture.completedFuture(null) : result;
		}

		@Override
		public synchronized void reconnect() {
			reconnectCalls++;
		}

		@Override
		public boolean isRetryableTransportFailure(Throwable throwable) {
			return retryable;
		}
	}
}
