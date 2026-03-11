package com.testrigor.seleniumextension.commons.application.streaming;

public interface BrowserStreamingStrategy {

	String name();

	boolean supports(String browserName);

	StreamStartResult start(BrowserScriptExecutor executor, StreamingSettings settings, String sessionId);

	default void stop(BrowserScriptExecutor executor) {
		// best effort optional cleanup
	}
}
