package com.testrigor.seleniumextension.commons.infrastructure.exceptions;

import com.google.rpc.Code;

public class GrpcInvalidArgumentException extends GrpcServerStatusException {

	public GrpcInvalidArgumentException(String message, String reason, String operation, String messageId) {
		super(message, Code.INVALID_ARGUMENT, reason, operation, messageId);
	}
}
