package com.testrigor.seleniumextension.v3.application;

import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.testrigor.seleniumextension.v3.application.services.SeleniumExtensionService;

public interface TestrigorDriver extends WebDriver, JavascriptExecutor, FindsById, FindsByClassName, FindsByLinkText, FindsByName, FindsByCssSelector, FindsByTagName, FindsByXPath, HasInputDevices, HasCapabilities, Interactive, TakesScreenshot {

	SeleniumExtensionService getManager();

	<T extends RemoteWebDriver> T getDelegate();

	void setTestCaseName(String name);

	void setTestContext(String testId);

	void clearTestContext();

	void executePrompt(String prompt);
}
