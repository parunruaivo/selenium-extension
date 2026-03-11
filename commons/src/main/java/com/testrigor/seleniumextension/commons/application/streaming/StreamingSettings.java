package com.testrigor.seleniumextension.commons.application.streaming;

import java.util.HashMap;
import java.util.Map;

import com.typesafe.config.Config;

public class StreamingSettings {

	private final boolean enabled;
	private final StreamingMode mode;
	private final String signalingBaseUrl;
	private final String signalingPath;
	private final int startupTimeoutMs;
	private final int pollIntervalMs;
	private final int width;
	private final int height;
	private final int fps;
	private final boolean audio;
	private final boolean chromiumExtensionEnabled;
	private final String chromiumExtensionId;
	private final boolean chromiumExtensionRequired;
	private final String chromiumExtensionCrxPath;

	private StreamingSettings(
			boolean enabled,
			StreamingMode mode,
			String signalingBaseUrl,
			String signalingPath,
			int startupTimeoutMs,
			int pollIntervalMs,
			int width,
			int height,
			int fps,
			boolean audio,
			boolean chromiumExtensionEnabled,
			String chromiumExtensionId,
			boolean chromiumExtensionRequired,
			String chromiumExtensionCrxPath) {
		this.enabled = enabled;
		this.mode = mode;
		this.signalingBaseUrl = trimTrailingSlash(signalingBaseUrl);
		this.signalingPath = ensureLeadingSlash(signalingPath);
		this.startupTimeoutMs = startupTimeoutMs;
		this.pollIntervalMs = pollIntervalMs;
		this.width = width;
		this.height = height;
		this.fps = fps;
		this.audio = audio;
		this.chromiumExtensionEnabled = chromiumExtensionEnabled;
		this.chromiumExtensionId = chromiumExtensionId;
		this.chromiumExtensionRequired = chromiumExtensionRequired;
		this.chromiumExtensionCrxPath = chromiumExtensionCrxPath;
	}

	public static StreamingSettings fromConfig(Config config) {
		String prefix = "streaming";
		boolean enabled = getBoolean(config, prefix + ".enabled", false);
		StreamingMode mode = StreamingMode.fromString(getString(config, prefix + ".mode", "auto"));
		String signalingBaseUrl = getString(config, prefix + ".signaling.baseUrl", "http://localhost:8080");
		String signalingPath = getString(config, prefix + ".signaling.path", "/webrtc/signaling");
		int startupTimeoutMs = getInt(config, prefix + ".startupTimeoutMs", 30000);
		int pollIntervalMs = getInt(config, prefix + ".pollIntervalMs", 200);
		int width = getInt(config, prefix + ".video.width", 1280);
		int height = getInt(config, prefix + ".video.height", 720);
		int fps = getInt(config, prefix + ".video.fps", 24);
		boolean audio = getBoolean(config, prefix + ".video.audio", false);
		boolean chromiumExtensionEnabled = getBoolean(config, prefix + ".chromium.extension.enabled", true);
		String chromiumExtensionId = getString(config, prefix + ".chromium.extension.id", "");
		boolean chromiumExtensionRequired = getBoolean(config, prefix + ".chromium.extension.required", false);
		String chromiumExtensionCrxPath = getString(config, prefix + ".chromium.extension.crxPath", "");
		return new StreamingSettings(
				enabled, mode, signalingBaseUrl, signalingPath, startupTimeoutMs, pollIntervalMs,
				width, height, fps, audio,
				chromiumExtensionEnabled, chromiumExtensionId, chromiumExtensionRequired, chromiumExtensionCrxPath);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public StreamingMode getMode() {
		return mode;
	}

	public int getStartupTimeoutMs() {
		return startupTimeoutMs;
	}

	public int getPollIntervalMs() {
		return pollIntervalMs;
	}

	public boolean isChromiumExtensionEnabled() {
		return chromiumExtensionEnabled;
	}

	public String getChromiumExtensionId() {
		return chromiumExtensionId;
	}

	public boolean isChromiumExtensionRequired() {
		return chromiumExtensionRequired;
	}

	public String getChromiumExtensionCrxPath() {
		return chromiumExtensionCrxPath;
	}

	public String getInfoUrl() {
		return signalingBaseUrl + signalingPath;
	}

	public String getOfferUrl() {
		return signalingBaseUrl + signalingPath + "/offer";
	}

	public String getIceUrl() {
		return signalingBaseUrl + signalingPath + "/ice";
	}

	public Map<String, Object> toJsConfig(String sessionId, boolean chromiumHints) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("offerUrl", getOfferUrl());
		payload.put("iceUrl", getIceUrl());
		payload.put("width", width);
		payload.put("height", height);
		payload.put("fps", fps);
		payload.put("audio", audio);
		payload.put("chromiumHints", chromiumHints);
		payload.put("sessionId", sessionId);
		payload.put("extensionId", chromiumExtensionId);
		payload.put("actionStart", ChromiumExtensionContract.ACTION_START);
		payload.put("actionStop", ChromiumExtensionContract.ACTION_STOP);
		return payload;
	}

	private static String trimTrailingSlash(String s) {
		if (s == null) {
			return "";
		}
		return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
	}

	private static String ensureLeadingSlash(String s) {
		if (s == null || s.isEmpty()) {
			return "/webrtc/signaling";
		}
		return s.startsWith("/") ? s : "/" + s;
	}

	private static String getString(Config config, String path, String fallback) {
		return config.hasPath(path) ? config.getString(path) : fallback;
	}

	private static int getInt(Config config, String path, int fallback) {
		return config.hasPath(path) ? config.getInt(path) : fallback;
	}

	private static boolean getBoolean(Config config, String path, boolean fallback) {
		return config.hasPath(path) ? config.getBoolean(path) : fallback;
	}
}
