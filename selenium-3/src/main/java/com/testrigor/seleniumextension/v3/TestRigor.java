package com.testrigor.seleniumextension.v3;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.testrigor.seleniumextension.v3.application.TestrigorDriver;
import com.testrigor.seleniumextension.v3.application.commands.TestRigorCommands;
import com.testrigor.seleniumextension.v3.application.proxy.SeleniumExtensionProxyInvocationHandler;
import com.testrigor.seleniumextension.v3.application.services.SeleniumExtensionService;
import com.testrigor.seleniumextension.v3.application.proxy.ProxyFactory;
import com.testrigor.seleniumextension.v3.application.streaming.StreamingExtensionSetup;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TestRigor {
	private static final String START_MAXIMIZED_ARG = "--start-maximized";

	public static TestrigorDriver extendDriver(RemoteWebDriver originalDriver, String apiToken) {
		return create(originalDriver, apiToken);
	}

	public static By byUserDescription(String description) {
		return new com.testrigor.seleniumextension.v3.domain.model.ByUserDescription(description);
	}

	static TestrigorDriver create(RemoteWebDriver delegate, String apiToken) {
		SeleniumExtensionService extensionService = new SeleniumExtensionService(delegate, apiToken);
		return create(extensionService);
	}

	public static TestrigorDriver create(SeleniumExtensionService manager) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Class<? extends WebDriver> driverClass = manager.getDriver().getClass();
		SeleniumExtensionProxyInvocationHandler handler = new SeleniumExtensionProxyInvocationHandler(manager);
		return ProxyFactory.createDriverProxy(classLoader, handler, driverClass);
	}

	/** Entry point for the fluent testRigor command DSL; chain steps then {@link TestRigorCommands#execute() execute()} or {@link TestRigorCommands#buildPrompt() buildPrompt()}. */
	public static TestRigorCommands commands(TestrigorDriver driver) {
		return TestRigorCommands.with(driver);
	}

	/** Configure ChromeOptions with packaged streaming extension before driver creation. */
	public static void configureStreamingExtension(ChromeOptions options, Config config) {
		StreamingExtensionSetup.configureChromeOptions(options, config);
	}

	/** Creates ChromeDriver with auto-installed streaming extension, then wraps it with self-heal. */
	public static TestrigorDriver createChromeDriver(Config config, String apiToken) {
		ChromeOptions options = new ChromeOptions();
		applyChromeConfig(config, options);
		configureStreamingExtension(options, config);
		return extendDriver(new ChromeDriver(options), apiToken);
	}

	/** Convenience overload using default application.properties lookup. */
	public static TestrigorDriver createChromeDriver(String apiToken) {
		Config config = ConfigFactory.systemProperties()
				.withFallback(ConfigFactory.load("application.properties").withFallback(ConfigFactory.load()));
		return createChromeDriver(config, apiToken);
	}

	private static void applyChromeConfig(Config config, ChromeOptions options) {
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
}
