package com.testrigor.seleniumextension.v4.application;

import java.util.Map;

import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PrintsPage;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.virtualauthenticator.HasVirtualAuthenticator;

import com.testrigor.seleniumextension.v4.application.services.SeleniumExtensionService;

public interface TestrigorDriver extends WebDriver, JavascriptExecutor, HasCapabilities, HasVirtualAuthenticator,
	Interactive, PrintsPage, TakesScreenshot {

	SeleniumExtensionService getManager();

	<T extends RemoteWebDriver> T getDelegate();

	void setTestCaseName(String name);

	void setTestContext(String testId);

	void clearTestContext();

	void executePrompt(String prompt);

	Object executeAction(String actionName, Map<String, Object> parameters);

	void click(String elementDescription);

	void checkPageContains(String text);

	String grabValue(String elementDescription);
}
