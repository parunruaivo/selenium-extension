package com.testrigor.seleniumextension.v4;

import java.io.File;
import java.nio.file.Path;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.testrigor.seleniumextension.v4.domain.model.ByUserDescription;
import com.testrigor.seleniumextension.v4.application.TestrigorDriver;
import com.testrigor.seleniumextension.v4.application.commands.TestRigorActions;
import com.testrigor.seleniumextension.v4.application.commands.TestRigorQueries;
import com.testrigor.seleniumextension.v4.application.commands.TestRigorValidations;
import com.testrigor.seleniumextension.v4.application.proxy.ProxyFactory;
import com.testrigor.seleniumextension.v4.application.proxy.SeleniumExtensionProxyInvocationHandler;
import com.testrigor.seleniumextension.v4.application.services.SeleniumExtensionService;
import com.testrigor.seleniumextension.v4.application.streaming.StreamingExtensionSetup;
import com.testrigor.seleniumextension.commons.application.grpc.GrpcEndpointConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestRigor {
	private static final String START_MAXIMIZED_ARG = "--start-maximized";

	public static TestrigorDriver extendDriver(RemoteWebDriver originalDriver, String apiToken) {
		return create(originalDriver, apiToken);
	}

	/**
	 * Wraps a driver with the extension, using the given gRPC endpoint (streaming/recorders still use default application config).
	 */
	public static TestrigorDriver extendDriver(RemoteWebDriver originalDriver, String apiToken,
			GrpcEndpointConfig grpcEndpoint) {
		return create(originalDriver, apiToken, grpcEndpoint);
	}

	public static TestrigorDriver testrigor(RemoteWebDriver originalDriver, String apiToken) {
		return create(originalDriver, apiToken);
	}

	static TestrigorDriver create(RemoteWebDriver delegate, String apiToken) {
		SeleniumExtensionService extensionService = new SeleniumExtensionService(delegate, apiToken);
		return create(extensionService);
	}

	static TestrigorDriver create(RemoteWebDriver delegate, String apiToken, GrpcEndpointConfig grpcEndpoint) {
		SeleniumExtensionService extensionService = new SeleniumExtensionService(delegate, apiToken, grpcEndpoint);
		return create(extensionService);
	}

	public static TestrigorDriver create(SeleniumExtensionService manager) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Class<? extends WebDriver> driverClass = manager.getDriver().getClass();
		SeleniumExtensionProxyInvocationHandler handler = new SeleniumExtensionProxyInvocationHandler(manager);
		return ProxyFactory.createDriverProxy(classLoader, handler, driverClass);
	}

	public static TestrigorDriver createTestrigorDriver(SeleniumExtensionService manager) {
		return create(manager);
	}

	public static By byUserDescription(String description) {
		return new ByUserDescription(description);
	}

	/** Entry point for execute-only fluent actions. */
	public static TestRigorActions actions(TestrigorDriver driver) {
		return TestRigorActions.actions(driver);
	}

	/** Entry point for fluent validations/checks. */
	public static TestRigorValidations validations(TestrigorDriver driver) {
		return TestRigorValidations.validations(driver);
	}

	/** Entry point for return-value query commands. */
	public static TestRigorQueries queries(TestrigorDriver driver) {
		return TestRigorQueries.queries(driver);
	}

	/** Configure ChromeOptions with packaged streaming extension before driver creation. */
	public static void configureStreamingExtension(ChromeOptions options, Config config) {
		StreamingExtensionSetup.configureChromeOptions(options, config);
	}

	/** Configure EdgeOptions with packaged streaming extension before driver creation. */
	public static void configureStreamingExtension(EdgeOptions options, Config config) {
		StreamingExtensionSetup.configureEdgeOptions(options, config);
	}

	/** Creates ChromeDriver with auto-installed streaming extension, then wraps it with self-heal. */
	public static TestrigorDriver createChromeDriver(Config config, String apiToken) {
		return createChromeDriver(config, apiToken, GrpcEndpointConfig.fromConfig(config));
	}

	/**
	 * Like {@link #createChromeDriver(Config, String)} but uses {@code grpcEndpoint} for the gRPC connection
	 * (e.g. code-first {@link GrpcEndpointConfig#of} or {@link GrpcEndpointConfig#fromConfig(Config)} on the same {@code config}).
	 */
	public static TestrigorDriver createChromeDriver(Config config, String apiToken,
			GrpcEndpointConfig grpcEndpoint) {
		ChromeOptions options = new ChromeOptions();
		applyCommonBrowserConfig(config, options);
		configureStreamingExtension(options, config);
		ChromeDriverService service = buildChromeDriverService(config);
		return extendDriver(new ChromeDriver(service, options), apiToken, grpcEndpoint);
	}

	/** Creates EdgeDriver with auto-installed streaming extension, then wraps it with self-heal. */
	public static TestrigorDriver createEdgeDriver(Config config, String apiToken) {
		return createEdgeDriver(config, apiToken, GrpcEndpointConfig.fromConfig(config));
	}

	/**
	 * Like {@link #createEdgeDriver(Config, String)} but uses {@code grpcEndpoint} for the gRPC connection.
	 */
	public static TestrigorDriver createEdgeDriver(Config config, String apiToken,
			GrpcEndpointConfig grpcEndpoint) {
		EdgeOptions options = new EdgeOptions();
		applyCommonBrowserConfig(config, options);
		configureStreamingExtension(options, config);
		return extendDriver(new EdgeDriver(options), apiToken, grpcEndpoint);
	}

	/** Convenience overload using default application.properties lookup for Chrome. */
	public static TestrigorDriver createChromeDriver(String apiToken) {
		Config config = ConfigFactory.systemProperties()
				.withFallback(ConfigFactory.load("application.properties").withFallback(ConfigFactory.load()));
		return createChromeDriver(config, apiToken);
	}

	/** Convenience overload using default application.properties lookup for Edge. */
	public static TestrigorDriver createEdgeDriver(String apiToken) {
		Config config = ConfigFactory.systemProperties()
				.withFallback(ConfigFactory.load("application.properties").withFallback(ConfigFactory.load()));
		return createEdgeDriver(config, apiToken);
	}

	private static void applyCommonBrowserConfig(Config config, ChromeOptions options) {
		boolean hasStartMaximized = false;
		if (config.hasPath("chrome.binary")) {
			String binary = config.getString("chrome.binary");
			if (binary != null && !binary.trim().isEmpty()) {
				options.setBinary(binary.trim());
			}
		}
		if (config.hasPath("chrome.args")) {
			String args = config.getString("chrome.args");
			if (args != null && !args.trim().isEmpty()) {
				for (String arg : args.split(",")) {
					String trimmed = arg.trim();
					if (!trimmed.isEmpty()) {
						options.addArguments(trimmed);
						if (START_MAXIMIZED_ARG.equalsIgnoreCase(trimmed)) {
							hasStartMaximized = true;
						}
					}
				}
			}
		}
		if (!hasStartMaximized) {
			options.addArguments(START_MAXIMIZED_ARG);
		}
	}

	private static ChromeDriverService buildChromeDriverService(Config config) {
		ChromeDriverService.Builder serviceBuilder = new ChromeDriverService.Builder().usingAnyFreePort();

		String driverPath = resolveChromeDriverPath(config);
		if (isNotBlank(driverPath)) {
			serviceBuilder = serviceBuilder.usingDriverExecutable(new File(driverPath));
		}

		if (config.hasPath("chromedriver.verbose") && config.getBoolean("chromedriver.verbose")) {
			serviceBuilder = serviceBuilder.withVerbose(true);
		}

		String logPath = resolveOptionalConfig(config, "chromedriver.log.path");
		if (isNotBlank(logPath)) {
			serviceBuilder = serviceBuilder.withLogFile(Path.of(logPath).toFile());
		}

		return serviceBuilder.build();
	}

	private static String resolveChromeDriverPath(Config config) {
		String fromConfig = resolveOptionalConfig(config, "webdriver.chrome.driver");
		if (isNotBlank(fromConfig)) {
			return fromConfig;
		}

		String fromSystemProperty = System.getProperty("webdriver.chrome.driver");
		if (isNotBlank(fromSystemProperty)) {
			return fromSystemProperty.trim();
		}

		return null;
	}

	private static String resolveOptionalConfig(Config config, String key) {
		if (!config.hasPath(key)) {
			return null;
		}
		String value = config.getString(key);
		return value == null ? null : value.trim();
	}

	private static boolean isNotBlank(String value) {
		return value != null && !value.trim().isEmpty();
	}

	private static void applyCommonBrowserConfig(Config config, EdgeOptions options) {
		boolean hasStartMaximized = false;
		if (config.hasPath("edge.args")) {
			String args = config.getString("edge.args");
			if (args != null && !args.trim().isEmpty()) {
				for (String arg : args.split(",")) {
					String trimmed = arg.trim();
					if (!trimmed.isEmpty()) {
						options.addArguments(trimmed);
						if (START_MAXIMIZED_ARG.equalsIgnoreCase(trimmed)) {
							hasStartMaximized = true;
						}
					}
				}
			}
		} else if (config.hasPath("chrome.args")) {
			String args = config.getString("chrome.args");
			if (args != null && !args.trim().isEmpty()) {
				for (String arg : args.split(",")) {
					String trimmed = arg.trim();
					if (!trimmed.isEmpty()) {
						options.addArguments(trimmed);
						if (START_MAXIMIZED_ARG.equalsIgnoreCase(trimmed)) {
							hasStartMaximized = true;
						}
					}
				}
			}
		}
		if (!hasStartMaximized) {
			options.addArguments(START_MAXIMIZED_ARG);
		}
	}
}
