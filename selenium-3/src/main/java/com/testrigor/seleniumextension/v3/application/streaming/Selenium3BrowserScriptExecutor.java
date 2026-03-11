package com.testrigor.seleniumextension.v3.application.streaming;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.testrigor.seleniumextension.commons.application.streaming.BrowserScriptExecutor;

public class Selenium3BrowserScriptExecutor implements BrowserScriptExecutor {

	private final RemoteWebDriver driver;

	public Selenium3BrowserScriptExecutor(RemoteWebDriver driver) {
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
