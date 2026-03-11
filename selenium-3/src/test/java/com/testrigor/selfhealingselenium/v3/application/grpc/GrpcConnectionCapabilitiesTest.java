package com.testrigor.seleniumextension.v3.application.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testrigor.seleniumextension.commons.application.utils.JsonHelpers;
import com.typesafe.config.ConfigFactory;

public class GrpcConnectionCapabilitiesTest {

	@Test
	public void getDriverInfoAddsBrowserNameWhenMissingInCapabilitiesMap() {
		RemoteWebDriver driver = mock(RemoteWebDriver.class);
		Capabilities capabilities = mock(Capabilities.class);
		when(driver.getSessionId()).thenReturn(new SessionId("session-id"));
		when(driver.getCapabilities()).thenReturn(capabilities);
		when(capabilities.asMap()).thenReturn(Map.of("platformName", "any"));
		when(capabilities.getBrowserName()).thenReturn("chrome");

		GrpcConnection connection = new GrpcConnection(
			driver,
			ConfigFactory.parseString("testrigor.grpc.uri=\"localhost\"\ntestrigor.grpc.port=9091"),
			"token");

		Map<String, Object> serialized = JsonHelpers.deserializeJson(
			connection.getDriverInfo().getCapabilitiesJson(),
			new TypeReference<Map<String, Object>>() { });

		assertThat(serialized).containsEntry("platformName", "any");
		assertThat(serialized).containsEntry("browserName", "chrome");
	}
}
