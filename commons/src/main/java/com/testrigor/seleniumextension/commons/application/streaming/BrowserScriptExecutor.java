package com.testrigor.seleniumextension.commons.application.streaming;

/**
 * Browser script bridge implemented by Selenium 3/4 modules.
 */
public interface BrowserScriptExecutor {

	String getBrowserName();

	Object executeScript(String script, Object... args);
}
