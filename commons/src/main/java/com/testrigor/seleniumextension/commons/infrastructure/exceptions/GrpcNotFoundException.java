package com.testrigor.seleniumextension.commons.infrastructure.exceptions;

import com.google.rpc.Code;

public class GrpcNotFoundException extends GrpcServerStatusException {

	public GrpcNotFoundException(String message, String reason, String operation, String messageId) {
		super(message, Code.NOT_FOUND, reason, operation, messageId);
	}
}
