package com.testrigor.seleniumextension.commons.application.grpc;

import com.testrigor.seleniumextension.grpc.lib.ClientMessage;

import io.grpc.stub.StreamObserver;

public interface GrpcDriverAdapter {

	/**
	 * @param testIdOverride if non-null and non-blank, used as {@link DriverInfo#getTestId()} for the gRPC {@code Driver}
	 *                       payload; otherwise implementations may fall back to {@link com.testrigor.seleniumextension.commons.application.context.TestRigorContext}
	 */
	DriverInfo getDriverInfo(String testIdOverride);

	/** Same as {@link #getDriverInfo(String)} with {@code null} override (e.g. current thread's test context). */
	default DriverInfo getDriverInfo() {
		return getDriverInfo(null);
	}

	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	void executeCommand(String messageId, String sessionId, String commandName, String parametersJson,
		StreamObserver<ClientMessage> responseObserver);

	Object resolveElementFromXpath(String xpath);
}
