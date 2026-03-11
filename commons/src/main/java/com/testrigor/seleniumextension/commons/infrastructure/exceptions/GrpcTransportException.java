package com.testrigor.seleniumextension.commons.infrastructure.exceptions;

import lombok.Getter;

@Getter
public class GrpcTransportException extends GrpcClientException {
	private final boolean retryable;
	private final String operation;
	private final String messageId;
	private final String grpcStatusCode;
	private final String grpcStatusDescription;

	public GrpcTransportException(String message, Throwable cause, boolean retryable,
			String operation, String messageId, String grpcStatusCode, String grpcStatusDescription) {
		super(message, cause);
		this.retryable = retryable;
		this.operation = operation;
		this.messageId = messageId;
		this.grpcStatusCode = grpcStatusCode;
		this.grpcStatusDescription = grpcStatusDescription;
	}
}
