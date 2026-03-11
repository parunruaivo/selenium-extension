package com.testrigor.seleniumextension.commons.application.grpc;

import lombok.Getter;

@Getter
public class DriverInfo {
	private final String sessionId;
	private final String capabilitiesJson;
	private final String testId;

	public DriverInfo(String sessionId, String capabilitiesJson) {
		this(sessionId, capabilitiesJson, null);
	}

	public DriverInfo(String sessionId, String capabilitiesJson, String testId) {
		this.sessionId = sessionId;
		this.capabilitiesJson = capabilitiesJson;
		this.testId = testId;
	}
}
