package com.testrigor.seleniumextension.v4.application.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openqa.selenium.json.Json.MAP_TYPE;

import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.annotations.Test;

import com.typesafe.config.ConfigFactory;

public class GrpcConnectionCapabilitiesTest {

	private static final Json JSON = new Json();

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

		Map<String, Object> serialized = JSON.toType(connection.getDriverInfo().getCapabilitiesJson(), MAP_TYPE);

		assertThat(serialized).containsEntry("platformName", "any");
		assertThat(serialized).containsEntry("browserName", "chrome");
	}
}
