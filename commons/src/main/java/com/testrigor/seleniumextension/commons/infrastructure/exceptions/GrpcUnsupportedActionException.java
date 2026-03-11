package com.testrigor.seleniumextension.commons.infrastructure.exceptions;

public class GrpcUnsupportedActionException extends GrpcInvalidArgumentException {

	public GrpcUnsupportedActionException(String message, String reason, String operation, String messageId) {
		super(message, reason, operation, messageId);
	}
}
