package com.testrigor.seleniumextension.v4.application.streaming;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.testrigor.seleniumextension.commons.application.streaming.BrowserScriptExecutor;

public class Selenium4BrowserScriptExecutor implements BrowserScriptExecutor {

	private final RemoteWebDriver driver;

	public Selenium4BrowserScriptExecutor(RemoteWebDriver driver) {
		this.driver = driver;
	}

	@Override
	public String getBrowserName() {
		Capabilities capabilities = driver.getCapabilities();
		return capabilities == null ? "unknown" : capabilities.getBrowserName();
	}

	@Override
	public Object executeScript(String script, Object... args) {
		return ((JavascriptExecutor) driver).executeScript(script, args);
	}
}
