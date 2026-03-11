package com.testrigor.seleniumextension.v4.application.streaming;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;

import com.testrigor.seleniumextension.commons.application.streaming.ChromiumExtensionResolver;
import com.testrigor.seleniumextension.commons.application.streaming.StreamingSettings;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Helpers to install packaged Chromium streaming extension and capture flags before driver creation.
 */
public final class StreamingExtensionSetup {

	private static final String AUTO_SELECT_CAPTURE_SOURCE = "--auto-select-desktop-capture-source=Entire screen";
	/** Bypasses getDisplayMedia permission dialog; first source is auto-selected. */
	private static final String USE_FAKE_UI_FOR_MEDIA_STREAM = "--use-fake-ui-for-media-stream";
	/** Suppresses permission bubbles/chips for screen capture; GetDisplayMediaPermissionBubble may help with "access other apps" prompt. */
	private static final String DISABLE_PERMISSION_UI = "--disable-features=PermissionsBubbleScreenCapture,PermissionChip,GetDisplayMediaPermissionBubble";

	private StreamingExtensionSetup() {
	}

	public static void configureChromeOptions(ChromeOptions options, Config config) {
		StreamingSettings settings = StreamingSettings.fromConfig(config);
		if (settings.isEnabled()) {
			options.addArguments(AUTO_SELECT_CAPTURE_SOURCE);
			options.addArguments(USE_FAKE_UI_FOR_MEDIA_STREAM);
			options.addArguments(DISABLE_PERMISSION_UI);
			applyStreamingPrefs(options);
		}
		File extensionDir = resolveExtensionDir(config, settings);
		if (extensionDir != null) {
			options.addArguments("--disable-extensions-except=" + extensionDir.getAbsolutePath());
			options.addArguments("--load-extension=" + extensionDir.getAbsolutePath());
		}
	}

	public static void configureEdgeOptions(EdgeOptions options, Config config) {
		StreamingSettings settings = StreamingSettings.fromConfig(config);
		if (settings.isEnabled()) {
			options.addArguments(AUTO_SELECT_CAPTURE_SOURCE);
			options.addArguments(USE_FAKE_UI_FOR_MEDIA_STREAM);
			options.addArguments(DISABLE_PERMISSION_UI);
			applyStreamingPrefs(options);
		}
		File extensionDir = resolveExtensionDir(config, settings);
		if (extensionDir != null) {
			options.addArguments("--disable-extensions-except=" + extensionDir.getAbsolutePath());
			options.addArguments("--load-extension=" + extensionDir.getAbsolutePath());
		}
	}

	public static void configureChromeOptions(ChromeOptions options) {
		configureChromeOptions(options, ConfigFactory.load("application.properties").withFallback(ConfigFactory.load()));
	}

	public static void configureEdgeOptions(EdgeOptions options) {
		configureEdgeOptions(options, ConfigFactory.load("application.properties").withFallback(ConfigFactory.load()));
	}

	private static void applyStreamingPrefs(ChromeOptions options) {
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("profile.default_content_setting_values.media_stream_mic", 1);
		prefs.put("profile.default_content_setting_values.media_stream_camera", 1);
		options.setExperimentalOption("prefs", prefs);
	}

	private static void applyStreamingPrefs(EdgeOptions options) {
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("profile.default_content_setting_values.media_stream_mic", 1);
		prefs.put("profile.default_content_setting_values.media_stream_camera", 1);
		options.setExperimentalOption("prefs", prefs);
	}

	private static File resolveExtensionDir(Config config, StreamingSettings settings) {
		if (!settings.isEnabled() || !settings.isChromiumExtensionEnabled()) {
			return null;
		}
		return ChromiumExtensionResolver.resolveExtensionDirectory(settings);
	}
}
