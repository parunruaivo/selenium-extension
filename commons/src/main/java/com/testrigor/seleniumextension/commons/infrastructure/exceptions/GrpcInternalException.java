package com.testrigor.seleniumextension.commons.infrastructure.exceptions;

import com.google.rpc.Code;

public class GrpcInternalException extends GrpcServerStatusException {

	public GrpcInternalException(String message, String reason, String operation, String messageId) {
		super(message, Code.INTERNAL, reason, operation, messageId);
	}
}
