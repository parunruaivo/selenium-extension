package com.testrigor.seleniumextension.v4.application.services;

import static lombok.AccessLevel.PRIVATE;
import static org.openqa.selenium.json.Json.MAP_TYPE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.testrigor.seleniumextension.commons.application.context.TestRigorContext;
import com.testrigor.seleniumextension.commons.application.grpc.GrpcEndpointConfig;
import com.testrigor.seleniumextension.commons.application.grpc.SeleniumExtensionGrpcActions;
import com.testrigor.seleniumextension.commons.application.streaming.BrowserScriptExecutor;
import com.testrigor.seleniumextension.commons.application.streaming.StreamingCoordinator;
import com.testrigor.seleniumextension.commons.domain.model.Action;
import com.testrigor.seleniumextension.commons.domain.model.Locator;
import com.testrigor.seleniumextension.commons.domain.model.LocatorType;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcNotFoundException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.GrpcTransportException;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.SeleniumExtensionException;
import com.testrigor.seleniumextension.v4.application.grpc.GrpcConnection;
import com.testrigor.seleniumextension.v4.application.streaming.Selenium4BrowserScriptExecutor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

@FieldDefaults(level = PRIVATE)
@Log4j2
@SuppressWarnings("PMD.AvoidCatchingGenericException")
public class SeleniumExtensionService {

	private static final Json JSON = new Json();

	private static final Config DEFAULT_CONFIG = ConfigFactory.systemProperties().withFallback(
		ConfigFactory.load("application.properties").withFallback(ConfigFactory.load()));

	@Getter final RemoteWebDriver driver;
	final String apiToken;
	final GrpcEndpointConfig grpcEndpoint;

	/** Single gRPC connection per driver; created lazily, closed on quit. */
	volatile GrpcConnection grpcConnection;
	/** Ensures one connection and serializes gRPC calls. */
	private final Object connectionLock = new Object();
	/** True after closeConnection(); prevents reuse after quit. */
	private volatile boolean connectionClosed;
	/** Browser stream lifecycle tied to this driver session. */
	private final StreamingCoordinator streamingCoordinator;
	private final BrowserScriptExecutor browserScriptExecutor;
	private volatile boolean streamingStarted;

	public SeleniumExtensionService(RemoteWebDriver delegate, String apiToken) {
		this(delegate, apiToken, GrpcEndpointConfig.fromConfig(DEFAULT_CONFIG));
	}

	/**
	 * @param grpcEndpoint explicit gRPC target (overrides {@code testrigor.grpc.*} from default application config for the connection only)
	 */
	public SeleniumExtensionService(RemoteWebDriver delegate, String apiToken, GrpcEndpointConfig grpcEndpoint) {
		this.driver = delegate;
		this.apiToken = apiToken;
		this.grpcEndpoint = grpcEndpoint;
		this.browserScriptExecutor = new Selenium4BrowserScriptExecutor(delegate);
		this.streamingCoordinator = StreamingCoordinator.fromConfig(DEFAULT_CONFIG);
	}

	/** Starts browser streaming once per driver session; safe to call repeatedly. */
	public void startStreamingIfNeeded() {
		if (streamingStarted || connectionClosed) {
			return;
		}
		String currentUrl = safeCurrentUrl();
		if (!isCapturableUrl(currentUrl)) {
			return;
		}
		try {
			String sessionId = driver.getSessionId() == null ? "unknown" : driver.getSessionId().toString();
			streamingStarted = streamingCoordinator.start(browserScriptExecutor, sessionId);
		} catch (Exception e) {
			log.warn("Error starting browser stream: {}", e.getMessage());
		}
	}

	private String safeCurrentUrl() {
		try {
			return driver.getCurrentUrl();
		} catch (Exception ignored) {
			return "";
		}
	}

	private boolean isCapturableUrl(String url) {
		if (url == null) {
			return false;
		}
		String normalized = url.trim().toLowerCase();
		return !(normalized.startsWith("chrome://")
				|| normalized.startsWith("chrome-error://")
				|| normalized.startsWith("edge://")
				|| normalized.startsWith("about:"));
	}

	public void setTestCaseName(String name) {
		TestRigorContext.setTestContext(name);
	}

	public void setTestContext(String testId) {
		TestRigorContext.setTestContext(testId);
	}

	public void clearTestContext() {
		TestRigorContext.clearTestContext();
	}

	public void saveAction(Action action) {
		requireTestIdForLocatorHealing();
		Locator loc = action.getLocator();
		Map<String, Object> params = locatorParams(loc);
		try {
			executeWithReconnectRetry(
				"executeAction:" + SeleniumExtensionGrpcActions.SAVE_SELENIUM_LOCATOR,
				() -> {
					getOrCreateConnection().executeAction(SeleniumExtensionGrpcActions.SAVE_SELENIUM_LOCATOR, params).join();
					return null;
				});
		} catch (Exception e) {
			log.warn("saveSeleniumLocator failed: {}", rootCauseMessage(e));
		}
	}

	public Optional<By> getHealedLocator(Locator locator) {
		requireTestIdForLocatorHealing();
		Map<String, Object> params = locatorParams(locator);
		try {
			Object result = executeWithReconnectRetry(
				"executeAction:" + SeleniumExtensionGrpcActions.GET_HEALED_SELENIUM_LOCATOR,
				() -> getOrCreateConnection().executeAction(SeleniumExtensionGrpcActions.GET_HEALED_SELENIUM_LOCATOR, params).join());
			if (!(result instanceof String)) {
				return Optional.empty();
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> map = JSON.toType((String) result, MAP_TYPE);
			Locator healed = new Locator(
				LocatorType.fromName(String.valueOf(map.get(SeleniumExtensionGrpcActions.KEY_LOCATOR_TYPE))),
				String.valueOf(map.get(SeleniumExtensionGrpcActions.KEY_LOCATOR_VALUE)));
			return Optional.of(toBy(healed));
		} catch (SeleniumExtensionException e) {
			if (containsGrpcNotFound(e)) {
				return Optional.empty();
			}
			throw e;
		}
	}

	public void recordHealedFind(Locator original, Locator healed) {
		requireTestIdForLocatorHealing();
		Map<String, Object> params = new HashMap<>();
		params.put(SeleniumExtensionGrpcActions.KEY_ORIGINAL_LOCATOR_TYPE, original.getType().getValue());
		params.put(SeleniumExtensionGrpcActions.KEY_ORIGINAL_LOCATOR_VALUE, original.getValue());
		params.put(SeleniumExtensionGrpcActions.KEY_HEALED_LOCATOR_TYPE, healed.getType().getValue());
		params.put(SeleniumExtensionGrpcActions.KEY_HEALED_LOCATOR_VALUE, healed.getValue());
		try {
			executeWithReconnectRetry(
				"executeAction:" + SeleniumExtensionGrpcActions.RECORD_SELENIUM_LOCATOR_MAPPING,
				() -> {
					getOrCreateConnection().executeAction(SeleniumExtensionGrpcActions.RECORD_SELENIUM_LOCATOR_MAPPING, params).join();
					return null;
				});
		} catch (Exception e) {
			log.debug("recordSeleniumLocatorMapping failed: {}", rootCauseMessage(e));
		}
	}

	private static void requireTestIdForLocatorHealing() {
		String testId = TestRigorContext.getTestId();
		if ((testId == null) || testId.isBlank()) {
			throw new IllegalStateException(
				"testId is required for locator healing; set a non-blank test context before saveAction, getHealedLocator, or recordHealedFind.");
		}
	}

	private static Map<String, Object> locatorParams(Locator loc) {
		Map<String, Object> params = new HashMap<>();
		params.put(SeleniumExtensionGrpcActions.KEY_LOCATOR_TYPE, loc.getType().getValue());
		params.put(SeleniumExtensionGrpcActions.KEY_LOCATOR_VALUE, loc.getValue());
		return params;
	}

	private static By toBy(Locator healedLocator) {
		String value = healedLocator.getValue();
		switch (healedLocator.getType()) {
			case ID:
				return By.id(value);
			case XPATH:
				return By.xpath(value);
			case CSS_SELECTOR:
				return By.cssSelector(value);
			case TAG_NAME:
				return By.tagName(value);
			case LINK_TEXT:
				return By.linkText(value);
			case NAME:
				return By.name(value);
			case CLASS:
				return By.className(value);
			case PARTIAL_LINK_TEXT:
				return By.partialLinkText(value);
			case USER_DESCRIPTION:
			default:
				throw new SeleniumExtensionException("Unsupported healed locator type: " + healedLocator.getType());
		}
	}

	private static boolean containsGrpcNotFound(Throwable e) {
		for (Throwable t = e; t != null; t = t.getCause()) {
			if (t instanceof GrpcNotFoundException) {
				return true;
			}
		}
		return false;
	}

	private static String rootCauseMessage(Throwable e) {
		Throwable t = e;
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t.getMessage() == null ? t.getClass().getSimpleName() : t.getMessage();
	}

	/**
	 * Returns the shared gRPC connection, creating it on first use.
	 * @throws SeleniumExtensionException if the connection was already closed (e.g. after quit)
	 */
	private GrpcConnection getOrCreateConnection() {
		if (connectionClosed) {
			throw new SeleniumExtensionException("Driver connection already closed");
		}
		if (grpcConnection == null) {
			synchronized (connectionLock) {
				if (connectionClosed) {
					throw new SeleniumExtensionException("Driver connection already closed");
				}
				if (grpcConnection == null) {
					grpcConnection = new GrpcConnection(driver, grpcEndpoint, apiToken);
				}
			}
		}
		return grpcConnection;
	}

	/** Closes the gRPC connection if open; idempotent. Called when the driver is quit. */
	public void closeConnection() {
		synchronized (connectionLock) {
			try {
				streamingCoordinator.stop(browserScriptExecutor);
			} catch (Exception e) {
				log.warn("Error stopping browser stream: {}", e.getMessage());
			}
			streamingStarted = false;
			if (grpcConnection != null) {
				try {
					grpcConnection.close();
				} catch (Exception e) {
					log.warn("Error closing gRPC connection: {}", e.getMessage());
				}
				grpcConnection = null;
			}
			connectionClosed = true;
		}
	}

	public WebElement findByUserDescription(String description) {
		return executeWithReconnectRetry(
			"findByUserDescription",
			() -> getOrCreateConnection().findByUserDescription(description).join());
	}

	public void executePrompt(String prompt) {
		executeWithReconnectRetry(
			"executePrompt",
			() -> {
				getOrCreateConnection().executePrompt(prompt).join();
				return null;
			}
		);
	}

	public Object executeAction(String actionName, Map<String, Object> parameters) {
		return executeAction(actionName, parameters, false);
	}

	private Object executeAction(String actionName, Map<String, Object> parameters, boolean validationCommand) {
		try {
			return executeWithReconnectRetry(
				"executeAction:" + actionName,
				() -> getOrCreateConnection().executeAction(actionName, parameters).join());
		} catch (SeleniumExtensionException e) {
			if (validationCommand) {
				throw new AssertionError(e.getMessage(), e);
			}
			throw e;
		} catch (Exception e) {
			throw new SeleniumExtensionException("Failed to execute action " + actionName, e);
		}
	}

	public void click(String elementDescription) {
		executeAction("click", singleParameter("elementDescription", elementDescription));
	}

	public void checkPageContains(String text) {
		executeAction("checkPageContains", singleParameter("text", text), true);
	}

	public String grabValue(String elementDescription) {
		Object result = executeAction("grabValue", singleParameter("elementDescription", elementDescription));
		return result == null ? "" : String.valueOf(result);
	}

	private Map<String, Object> singleParameter(String key, String value) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(key, value);
		return parameters;
	}

	private <T> T executeWithReconnectRetry(String operation, Supplier<T> operationSupplier) {
		int attempt = 1;
		while (true) {
			try {
				synchronized (connectionLock) {
					return operationSupplier.get();
				}
			} catch (Exception e) {
				Throwable cause = unwrapCompletionException(e);
				if ((attempt == 1) && shouldReconnectAndRetry(cause)) {
					log.warn(
						"Retrying operation after gRPC reconnect. operation={}, retryableCause={}",
						operation,
						cause == null ? "unknown" : cause.getClass().getSimpleName(),
						cause
					);
					reconnectGrpcClient(cause);
					attempt++;
					continue;
				}
				throw toSeleniumExtensionException(cause == null ? e : cause, operation);
			}
		}
	}

	private void reconnectGrpcClient(Throwable cause) {
		synchronized (connectionLock) {
			if (connectionClosed) {
				throw new SeleniumExtensionException("Driver connection already closed", cause);
			}
			getOrCreateConnection().reconnect();
		}
	}

	private boolean shouldReconnectAndRetry(Throwable cause) {
		if (cause == null || connectionClosed) {
			return false;
		}
		if (cause instanceof GrpcTransportException) {
			return ((GrpcTransportException) cause).isRetryable();
		}
		GrpcConnection connection = grpcConnection;
		if (connection == null) {
			return false;
		}
		return connection.isRetryableTransportFailure(cause);
	}

	private Throwable unwrapCompletionException(Throwable throwable) {
		if ((throwable instanceof CompletionException) && (throwable.getCause() != null)) {
			return throwable.getCause();
		}
		return throwable;
	}

	private SeleniumExtensionException toSeleniumExtensionException(Throwable cause, String operation) {
		if (cause instanceof SeleniumExtensionException) {
			return (SeleniumExtensionException) cause;
		}
		return new SeleniumExtensionException("Failed to execute " + operation, cause);
	}
}
