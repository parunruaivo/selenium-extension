package com.testrigor.seleniumextension.commons.application.streaming;

public enum StreamingMode {
	AUTO,
	CHROMIUM_PRIMARY,
	DISPLAY_MEDIA_ONLY;

	public static StreamingMode fromString(String raw) {
		if (raw == null) {
			return AUTO;
		}
		String normalized = raw.trim().toUpperCase().replace('-', '_');
		switch (normalized) {
			case "CHROMIUM_PRIMARY":
				return CHROMIUM_PRIMARY;
			case "DISPLAY_MEDIA_ONLY":
				return DISPLAY_MEDIA_ONLY;
			case "AUTO":
			default:
				return AUTO;
		}
	}
}
