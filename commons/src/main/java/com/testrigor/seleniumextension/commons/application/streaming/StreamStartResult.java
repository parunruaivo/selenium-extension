package com.testrigor.seleniumextension.commons.application.streaming;

public class StreamStartResult {

	private final boolean success;
	private final String strategy;
	private final String message;

	private StreamStartResult(boolean success, String strategy, String message) {
		this.success = success;
		this.strategy = strategy;
		this.message = message;
	}

	public static StreamStartResult success(String strategy) {
		return new StreamStartResult(true, strategy, "ok");
	}

	public static StreamStartResult failure(String strategy, String message) {
		return new StreamStartResult(false, strategy, message);
	}

	public boolean isSuccess() {
		return success;
	}

	public String getStrategy() {
		return strategy;
	}

	public String getMessage() {
		return message;
	}
}
