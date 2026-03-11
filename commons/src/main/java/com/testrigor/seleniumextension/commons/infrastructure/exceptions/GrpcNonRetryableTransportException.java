package com.testrigor.seleniumextension.commons.infrastructure.exceptions;

public class GrpcNonRetryableTransportException extends GrpcTransportException {

	public GrpcNonRetryableTransportException(String message, Throwable cause,
			String operation, String messageId, String grpcStatusCode, String grpcStatusDescription) {
		super(message, cause, false, operation, messageId, grpcStatusCode, grpcStatusDescription);
	}
}
