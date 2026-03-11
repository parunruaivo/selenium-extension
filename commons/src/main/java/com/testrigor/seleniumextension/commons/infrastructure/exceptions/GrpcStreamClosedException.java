package com.testrigor.seleniumextension.commons.infrastructure.exceptions;

public class GrpcStreamClosedException extends GrpcNonRetryableTransportException {

	public GrpcStreamClosedException(String message, String operation, String messageId) {
		super(message, null, operation, messageId, "UNKNOWN", "stream completed");
	}
}
