package com.testrigor.seleniumextension.commons.infrastructure.exceptions;

public class GrpcRetryableTransportException extends GrpcTransportException {

	public GrpcRetryableTransportException(String message, Throwable cause,
			String operation, String messageId, String grpcStatusCode, String grpcStatusDescription) {
		super(message, cause, true, operation, messageId, grpcStatusCode, grpcStatusDescription);
	}
}
