package com.testrigor.seleniumextension.commons.application.streaming;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class ChromiumExtensionResolver {

	private static final String MANIFEST_RESOURCE = "/streaming/chromium-extension/manifest.json";
	private static final String WORKER_RESOURCE = "/streaming/chromium-extension/service_worker.js";
	private static final String CONTENT_BRIDGE_RESOURCE = "/streaming/chromium-extension/content_bridge.js";
	private static final String OFFSCREEN_HTML_RESOURCE = "/streaming/chromium-extension/offscreen.html";
	private static final String OFFSCREEN_JS_RESOURCE = "/streaming/chromium-extension/offscreen.js";

	private ChromiumExtensionResolver() {
	}

	/**
	 * Resolves unpacked extension directory from config or bundled resources.
	 * Returns null when no extension could be resolved.
	 */
	public static File resolveExtensionDirectory(StreamingSettings settings) {
		String configured = settings.getChromiumExtensionCrxPath();
		if (configured != null && !configured.trim().isEmpty()) {
			File file = new File(configured.trim());
			if (!file.exists()) {
				return null;
			}
			return file.isDirectory() ? file : null;
		}
		try {
			File extensionDir = Files.createTempDirectory("testrigor-streaming-extension").toFile();
			extensionDir.deleteOnExit();
			copyResourceToFile(MANIFEST_RESOURCE, extensionDir.toPath().resolve("manifest.json"));
			copyResourceToFile(WORKER_RESOURCE, extensionDir.toPath().resolve("service_worker.js"));
			copyResourceToFile(CONTENT_BRIDGE_RESOURCE, extensionDir.toPath().resolve("content_bridge.js"));
			copyResourceToFile(OFFSCREEN_HTML_RESOURCE, extensionDir.toPath().resolve("offscreen.html"));
			copyResourceToFile(OFFSCREEN_JS_RESOURCE, extensionDir.toPath().resolve("offscreen.js"));
			return extensionDir;
		} catch (IOException ex) {
			return null;
		}
	}

	private static void copyResourceToFile(String resourcePath, Path outputFile) throws IOException {
		try (InputStream in = ChromiumExtensionResolver.class.getResourceAsStream(resourcePath)) {
			if (in == null) {
				throw new IOException("Missing extension resource: " + resourcePath);
			}
			Files.copy(in, outputFile, StandardCopyOption.REPLACE_EXISTING);
		}
	}
}
