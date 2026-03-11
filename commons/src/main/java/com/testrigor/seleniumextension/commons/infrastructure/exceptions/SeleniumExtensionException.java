package com.testrigor.seleniumextension.commons.infrastructure.exceptions;

public class SeleniumExtensionException extends RuntimeException {

	public SeleniumExtensionException() {
		super();
	}

	public SeleniumExtensionException(String message) {
		super(message);
	}

	public SeleniumExtensionException(String message, Throwable cause) {
		super(message, cause);
	}

	public SeleniumExtensionException(Throwable cause) {
		super(cause);
	}

	protected SeleniumExtensionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
