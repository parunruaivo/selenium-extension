package com.testrigor.seleniumextension.commons.infrastructure.exceptions;

public class GrpcActionExecutionException extends GrpcInternalException {

	public GrpcActionExecutionException(String message, String reason, String operation, String messageId) {
		super(message, reason, operation, messageId);
	}
}
