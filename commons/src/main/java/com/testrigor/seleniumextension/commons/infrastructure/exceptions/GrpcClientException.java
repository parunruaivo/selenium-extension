package com.testrigor.seleniumextension.commons.infrastructure.exceptions;

public class GrpcClientException extends SeleniumExtensionException {

	public GrpcClientException(String message) {
		super(message);
	}

	public GrpcClientException(String message, Throwable cause) {
		super(message, cause);
	}
}
