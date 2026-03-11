package com.testrigor.seleniumextension.v3.application.services;

import static lombok.AccessLevel.PRIVATE;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.testrigor.seleniumextension.commons.application.context.TestRigorContext;
import com.testrigor.seleniumextension.commons.application.grpc.GrpcEndpointConfig;
import com.testrigor.seleniumextension.commons.application.grpc.SeleniumExtensionGrpcActions;
import com.testrigor.seleniumextension.commons.application.streaming.BrowserScriptExecutor;
import com.testrigor.seleniumextension.commons.application.streaming.StreamingCoordinator;

import com.testrigor.seleniumextension.v3.application.streaming.Selenium3BrowserScriptExecutor;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.testrigor.seleniumextension.commons.domain.model.Locator;
import com.testrigor.seleniumextension.commons.infrastructure.exceptions.SeleniumExtensionException;
import com.testrigor.seleniumextension.v3.application.grpc.GrpcConnection;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = PRIVATE)
@SuppressWarnings("PMD.AvoidCatchingGenericException") // closeConnection() close() throws Exception
public class SeleniumExtensionService {

	private static final Logger log = LogManager.getLogger(SeleniumExtensionService.class);

	private static final Config DEFAULT_CONFIG = ConfigFactory.systemProperties().withFallback(
		ConfigFactory.load("application.properties").withFallback(ConfigFactory.load()));

	@Getter
	final RemoteWebDriver driver;
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
		this.browserScriptExecutor = new Selenium3BrowserScriptExecutor(delegate);
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

	public void recordHealedFind(Locator original, Locator healed) {
		Map<String, Object> params = new HashMap<>();
		params.put(SeleniumExtensionGrpcActions.KEY_ORIGINAL_LOCATOR_TYPE, original.getType().getValue());
		params.put(SeleniumExtensionGrpcActions.KEY_ORIGINAL_LOCATOR_VALUE, original.getValue());
		params.put(SeleniumExtensionGrpcActions.KEY_HEALED_LOCATOR_TYPE, healed.getType().getValue());
		params.put(SeleniumExtensionGrpcActions.KEY_HEALED_LOCATOR_VALUE, healed.getValue());
		try {
			synchronized (connectionLock) {
				getOrCreateConnection().executeAction(SeleniumExtensionGrpcActions.RECORD_SELENIUM_LOCATOR_MAPPING, params).get();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.debug("recordSeleniumLocatorMapping interrupted");
		} catch (ExecutionException e) {
			log.debug("recordSeleniumLocatorMapping failed: {}", e.getCause() == null ? e : e.getCause().getMessage());
		} catch (Exception e) {
			log.debug("recordSeleniumLocatorMapping failed: {}", e.getMessage());
		}
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
					// best-effort close; do not throw
				}
				grpcConnection = null;
			}
			connectionClosed = true;
		}
	}

	public WebElement findByUserDescription(String description) {
		try {
			synchronized (connectionLock) {
				return getOrCreateConnection().findByUserDescription(description).get();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new SeleniumExtensionException("Interrupted while finding element by description", e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof SeleniumExtensionException) {
				throw (SeleniumExtensionException) cause;
			}
			throw new SeleniumExtensionException(cause == null ? e : cause);
		} catch (SeleniumExtensionException e) {
			throw e;
		} catch (Exception e) {
			throw new SeleniumExtensionException("Failed to find element by description", e);
		}
	}

	public void executePrompt(String prompt) {
		try {
			synchronized (connectionLock) {
				getOrCreateConnection().executePrompt(prompt).get();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new SeleniumExtensionException("Interrupted while executing prompt", e);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof SeleniumExtensionException) {
				throw (SeleniumExtensionException) cause;
			}
			throw new SeleniumExtensionException(cause == null ? e : cause);
		} catch (SeleniumExtensionException e) {
			throw e;
		} catch (Exception e) {
			throw new SeleniumExtensionException("Failed to execute prompt", e);
		}
	}
}
