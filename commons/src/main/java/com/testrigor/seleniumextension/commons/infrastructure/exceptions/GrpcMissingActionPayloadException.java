package com.testrigor.seleniumextension.commons.infrastructure.exceptions;

public class GrpcMissingActionPayloadException extends GrpcInvalidArgumentException {

	public GrpcMissingActionPayloadException(String message, String reason, String operation, String messageId) {
		super(message, reason, operation, messageId);
	}
}
