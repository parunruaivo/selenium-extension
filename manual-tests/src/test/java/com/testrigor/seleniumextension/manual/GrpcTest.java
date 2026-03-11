package com.testrigor.seleniumextension.manual;

import static com.testrigor.seleniumextension.v4.TestRigor.actions;
import static com.testrigor.seleniumextension.v4.TestRigor.queries;
import static com.testrigor.seleniumextension.v4.TestRigor.validations;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.testrigor.seleniumextension.v4.application.TestrigorDriver;
import com.testrigor.seleniumextension.v4.domain.model.ByUserDescription;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.testrigor.seleniumextension.commons.application.grpc.GrpcEndpointConfig;
import com.testrigor.seleniumextension.v4.TestRigor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class GrpcTest {

	private static final String DEFAULT_TEST_PAGE =
		"https://www.lanacion.com.ar/";

	TestrigorDriver driver;
	Config config;
	GrpcEndpointConfig grpcEndpoint;
	String apiToken;

	@BeforeMethod
	public void setup() {
		config = loadConfig();
		grpcEndpoint = GrpcEndpointConfig.fromConfig(config);
		apiToken = getRequiredValue("testrigor.apiToken", "TESTRIGOR_API_TOKEN");
		System.setProperty("webdriver.chrome.driver", getRequiredValue("webdriver.chrome.driver", "WEBDRIVER_CHROME_DRIVER"));
		String defaultArgs = String.join(",", resolveChromeArgs());
		if (!defaultArgs.isEmpty() && (resolveOptional("chrome.args", "CHROME_ARGS") == null
			|| resolveOptional("chrome.args", "CHROME_ARGS").isEmpty())) {
			System.setProperty("chrome.args", defaultArgs);
		}
		driver = TestRigor.createChromeDriver(config, apiToken, grpcEndpoint);
	}

	@Test
	public void test_find_by_user_description() {
		driver.setTestContext("test_find_by_user_description");
		driver.get(resolveOptional("test.page.url", "TEST_PAGE_URL", DEFAULT_TEST_PAGE));

		WebElement button = driver.findElement(TestRigor.byUserDescription("Login"));
		button.click();
		assertThat(button).isNotNull();
	}

	@Test
	public void test_execute_prompt() {
		driver.setTestContext("test_execute_prompt");
		driver.get(resolveOptional("test.page.url", "TEST_PAGE_URL", DEFAULT_TEST_PAGE));

		actions(driver)
			.click("secciones")
			.and()
			.click("economía")
			.execute();


		driver.checkPageContains("Comercio Exterior2");
	}

	@Test
	public void test_grab_value_prompt() {
		driver.setTestContext("test_grab_value_prompt");
		driver.get("https://www.lanacion.com.ar");


		actions(driver)
			.waitUntilPageContains("Iniciar sesión")
			.execute();

		WebElement element = driver.findElement(new ByUserDescription("Iniciar sesión"));
		WebElement byId = driver.findElement(By.id("btningresar"));

		validations(driver)
			.checkPageContains("Dólar MEP")
			.execute();


		final String dolarMepString = queries(driver)
			.grabValueByRegex("\\$[0-9].+", "Dólar MEP");

		actions(driver)
			.openNewTab()
			.openUrl("https://es.investing.com/indices/merv")
			.waitUntilPageContains("S&P Merval (MERV)")
			.execute();

		final String cotizacionString = queries(driver)
			.grabValue()
			.below("S&P Merval (MERV)")
			.byRegex("\\d{1,3}(?:\\.\\d{3})*,\\d{2}");

		final BigDecimal dolarMep = parsePrice(dolarMepString);
		final BigDecimal cotizacionMerval = parsePrice(cotizacionString);

		final String usd = cotizacionMerval.divide(dolarMep, 2, RoundingMode.HALF_UP).toString();

		actions(driver)
			.openNewTab()
			.openUrl("https://wise.com/gb/currency-converter/")
			.waitUntilPageContains("Currency Converter")
			.click("GBP")
			.type("USD").typeEnter()
			.enter(usd).into("Amount")
			.execute();

		final String eur = queries(driver)
			.grabValue("input", "Converted to");


		assertThat(eur).isNotBlank();

	}

	@AfterMethod(alwaysRun = true)
	public void close() {
		if (driver != null) {
			try {
				driver.quit();
			} catch (Exception ignored) {
				// fall through to direct delegate quit
			}
		}
		driver = null;
	}

	private Config loadConfig() {
		String configFilePath = System.getProperty("config.file");
		if (configFilePath != null && !configFilePath.isEmpty()) {
			File configFile = new File(configFilePath);
			return ConfigFactory.systemProperties()
				.withFallback(ConfigFactory.parseFile(configFile))
				.withFallback(ConfigFactory.load("application"));
		}
		return ConfigFactory.systemProperties().withFallback(ConfigFactory.load("application"));
	}

	private String getRequiredValue(String key, String envKey) {
		String value = resolveOptional(key, envKey);
		if (value == null || value.isEmpty()) {
			throw new IllegalStateException("Missing required configuration: " + key
				+ " (system property / application.properties / env " + envKey + ")");
		}
		return value;
	}

	private String resolveOptional(String key, String envKey) {
		if (config.hasPath(key)) {
			return config.getString(key);
		}
		String envValue = System.getenv(envKey);
		return envValue == null ? null : envValue.trim();
	}

	private String resolveOptional(String key, String envKey, String defaultValue) {
		String value = resolveOptional(key, envKey);
		return value == null || value.isEmpty() ? defaultValue : value;
	}

	private List<String> resolveChromeArgs() {
		List<String> args = new ArrayList<>();
		String argsValue = resolveOptional("chrome.args", "CHROME_ARGS");
		if (argsValue == null || argsValue.isEmpty()) {
			args.add("--remote-allow-origins=*");
			return args;
		}
		for (String arg : argsValue.split(",")) {
			String trimmed = arg.trim();
			if (!trimmed.isEmpty()) {
				args.add(trimmed);
			}
		}
		return args;
	}

	public static BigDecimal parsePrice(String input) {
		if (input == null || input.isBlank()) {
			return BigDecimal.ZERO;
		}

		// 1. Remove currency symbols, spaces, etc.
		String cleaned = input.replaceAll("[^0-9,\\.\\-]", "");

		// 2. Remove thousand separators (.)
		cleaned = cleaned.replace(".", "");

		// 3. Replace decimal comma with dot
		cleaned = cleaned.replace(",", ".");

		// 4. Parse safely
		return new BigDecimal(cleaned);
	}

}
