package com.testrigor.seleniumextension.commons.infrastructure.exceptions;

import com.google.rpc.Code;

import lombok.Getter;

@Getter
public class GrpcServerStatusException extends GrpcClientException {
	private final Code grpcCode;
	private final String reason;
	private final String operation;
	private final String messageId;

	public GrpcServerStatusException(String message, Code grpcCode, String reason, String operation, String messageId) {
		super(message);
		this.grpcCode = grpcCode;
		this.reason = reason;
		this.operation = operation;
		this.messageId = messageId;
	}
}
