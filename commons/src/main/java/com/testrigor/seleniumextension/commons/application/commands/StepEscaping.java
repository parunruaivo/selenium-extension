package com.testrigor.seleniumextension.commons.application.commands;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

/**
 * Escapes user-supplied strings for testRigor prompt lines.
 * Double-quoted segments in testRigor use backslash to escape " and \.
 */
@NoArgsConstructor(access = PRIVATE)
public final class StepEscaping {

	/**
	 * Escapes backslash and double-quote so the result can be wrapped in double quotes.
	 * E.g. {@code Say "Hi"} -> {@code Say \"Hi\"}.
	 */
	public static String quoted(String value) {
		if (value == null) {
			return "";
		}
		return value
			.replace("\\", "\\\\")
			.replace("\"", "\\\"");
	}

	/**
	 * Wraps the value in double quotes after escaping. E.g. {@code Login} -> {@code "Login"}.
	 */
	public static String quotedSegment(String value) {
		return "\"" + quoted(value) + "\"";
	}
}
