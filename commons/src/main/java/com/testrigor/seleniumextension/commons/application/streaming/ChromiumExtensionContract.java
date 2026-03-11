package com.testrigor.seleniumextension.commons.application.streaming;

/**
 * Contract used between page-side Selenium JS bridge and Chromium extension runtime.
 */
public final class ChromiumExtensionContract {

	public static final String ACTION_START = "startStreaming";
	public static final String ACTION_STOP = "stopStreaming";

	public static final String STATE_STARTED = "started";
	public static final String STATE_STOPPED = "stopped";
	public static final String STATE_STARTING = "starting";

	private ChromiumExtensionContract() {
	}
}
