package com.testrigor.seleniumextension.commons.infrastructure.exceptions;

public class GrpcIssuesDetectedException extends GrpcInternalException {

	public GrpcIssuesDetectedException(String message, String reason, String operation, String messageId) {
		super(message, reason, operation, messageId);
	}
}
