package com.testrigor.seleniumextension.commons.application.grpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

import com.google.protobuf.ByteString;
import com.testrigor.seleniumextension.grpc.lib.DriverCommandResponse;
import com.testrigor.seleniumextension.grpc.lib.ValueEncoding;

/**
 * Encodes Selenium command return values as {@code value_payload} + {@code value_encoding} for gRPC.
 */
public final class DriverCommandValueCodec {

	/** Gzip when UTF-8 JSON is at least this many bytes (wire-size win vs CPU). */
	private static final int GZIP_MIN_BYTES = 512;

	private DriverCommandValueCodec() {
	}

	public static void setEncodedValue(DriverCommandResponse.Builder builder, String jsonUtf8OrNull) {
		String s = jsonUtf8OrNull == null ? "" : jsonUtf8OrNull;
		byte[] utf8 = s.getBytes(StandardCharsets.UTF_8);
		if (utf8.length == 0) {
			builder.setValueEncoding(ValueEncoding.JSON_UTF8).clearValuePayload();
			return;
		}
		if (utf8.length >= GZIP_MIN_BYTES) {
			builder.setValueEncoding(ValueEncoding.GZIP_JSON_UTF8)
				.setValuePayload(ByteString.copyFrom(gzip(utf8)));
		} else {
			builder.setValueEncoding(ValueEncoding.JSON_UTF8)
				.setValuePayload(ByteString.copyFrom(utf8));
		}
	}

	private static byte[] gzip(byte[] data) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(Math.min(data.length, 256));
			try (GZIPOutputStream gos = new GZIPOutputStream(bos)) {
				gos.write(data);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
