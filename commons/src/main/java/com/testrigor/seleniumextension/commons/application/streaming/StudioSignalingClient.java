package com.testrigor.seleniumextension.commons.application.streaming;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Minimal signaling preflight client to avoid script startup when Studio is unreachable.
 */
public class StudioSignalingClient {

	private final HttpClient httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(2))
			.build();

	public boolean isAvailable(StreamingSettings settings) {
		HttpRequest req = HttpRequest.newBuilder()
				.uri(URI.create(settings.getInfoUrl()))
				.timeout(Duration.ofSeconds(3))
				.GET()
				.build();
		try {
			HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
			return response.statusCode() >= 200 && response.statusCode() < 300;
		} catch (IOException | InterruptedException | RuntimeException ex) {
			if (ex instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			return false;
		}
	}
}
