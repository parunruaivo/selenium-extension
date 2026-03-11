package com.testrigor.seleniumextension.v4.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.testrigor.seleniumextension.commons.application.context.TestRigorContext;
import com.testrigor.seleniumextension.commons.application.grpc.SeleniumExtensionGrpcActions;
import com.testrigor.seleniumextension.commons.domain.model.Locator;
import com.testrigor.seleniumextension.commons.domain.model.LocatorType;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcNotFoundException;
import com.testrigor.seleniumextension.v4.application.grpc.GrpcConnection;
import com.typesafe.config.ConfigFactory;

@SuppressWarnings("checkstyle:MagicNumber")
public class SeleniumExtensionServiceHealingTest {

	@BeforeMethod(alwaysRun = true)
	public void setTestId() {
		TestRigorContext.setTestContext("healing-test-default");
	}

	@AfterMethod(alwaysRun = true)
	public void clearTestId() {
		TestRigorContext.clearTestContext();
	}

	@Test
	public void getHealedLocator_mapsJsonToBy() throws Exception {
		SeleniumExtensionService service = newService();
		FakeGrpcConnection grpc = injectFakeConnection(service);
		grpc.enqueueActionResult(CompletableFuture.completedFuture(
			"{\"locatorType\":\"xpath\",\"locatorValue\":\"//div[@id='a']\"}"));

		Optional<By> healed = service.getHealedLocator(new Locator(LocatorType.ID, "a"));

		assertThat(healed).isPresent();
		assertThat(healed.get()).isInstanceOf(By.ByXPath.class);
		assertThat(grpc.lastActionName).isEqualTo(SeleniumExtensionGrpcActions.GET_HEALED_SELENIUM_LOCATOR);
		assertThat(grpc.lastParameters).containsEntry(SeleniumExtensionGrpcActions.KEY_LOCATOR_TYPE, "id");
		assertThat(grpc.lastParameters).containsEntry(SeleniumExtensionGrpcActions.KEY_LOCATOR_VALUE, "a");
	}

	@Test
	public void getHealedLocator_returnsEmpty_whenServerNotFound() throws Exception {
		SeleniumExtensionService service = newService();
		FakeGrpcConnection grpc = injectFakeConnection(service);
		grpc.enqueueActionResult(failedFuture(new GrpcNotFoundException(
			"missing",
			"Locator heal failed",
			"executeAction",
			"id-1")));

		Optional<By> healed = service.getHealedLocator(new Locator(LocatorType.CSS_SELECTOR, ".x"));

		assertThat(healed).isEmpty();
	}

	@Test
	public void getHealedLocator_throwsWhenTestIdMissing() {
		TestRigorContext.clearTestContext();
		try {
			SeleniumExtensionService service = newService();
			assertThatThrownBy(() -> service.getHealedLocator(new Locator(LocatorType.ID, "a")))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("testId is required");
		} finally {
			TestRigorContext.setTestContext("healing-test-default");
		}
	}

	private static SeleniumExtensionService newService() {
		return new SeleniumExtensionService(new StubRemoteWebDriver(), "token");
	}

	private static FakeGrpcConnection injectFakeConnection(SeleniumExtensionService service) throws Exception {
		FakeGrpcConnection grpc = new FakeGrpcConnection(service.getDriver());
		setField(service, "grpcConnection", grpc);
		return grpc;
	}

	private static void setField(Object target, String fieldName, Object value) throws Exception {
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}

	private static <T> CompletableFuture<T> failedFuture(Throwable throwable) {
		CompletableFuture<T> future = new CompletableFuture<>();
		future.completeExceptionally(throwable);
		return future;
	}

	private static final class StubRemoteWebDriver extends RemoteWebDriver {
		StubRemoteWebDriver() {
			super();
		}
	}

	private static final class FakeGrpcConnection extends GrpcConnection {
		private CompletableFuture<Object> nextResult = CompletableFuture.completedFuture(null);
		String lastActionName;
		Map<String, Object> lastParameters;

		FakeGrpcConnection(RemoteWebDriver driver) {
			super(
				driver,
				ConfigFactory.parseString("testrigor.grpc.uri=\"localhost\"\ntestrigor.grpc.port=9091"),
				"token");
		}

		void enqueueActionResult(CompletableFuture<Object> result) {
			nextResult = result;
		}

		@Override
		public CompletableFuture<Object> executeAction(String actionName, Map<String, Object> parameters) {
			lastActionName = actionName;
			lastParameters = parameters;
			CompletableFuture<Object> result = nextResult;
			nextResult = CompletableFuture.completedFuture(null);
			return result;
		}
	}
}
