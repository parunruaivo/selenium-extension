package com.testrigor.seleniumextension.commons.application.context;

public final class TestRigorContext {

	private static final ThreadLocal<String> TEST_ID_HOLDER = new ThreadLocal<>();

	private TestRigorContext() {
	}

	public static void setTestContext(String testId) {
		setIfPresent(TEST_ID_HOLDER, testId);
	}

	public static void clearTestContext() {
		TEST_ID_HOLDER.remove();
	}

	public static String getTestId() {
		return TEST_ID_HOLDER.get();
	}

	public static void withTestContext(String testId, Runnable runnable) {
		String previousTestId = TEST_ID_HOLDER.get();
		setTestContext(testId);
		try {
			runnable.run();
		} finally {
			restore(TEST_ID_HOLDER, previousTestId);
		}
	}

	private static void setIfPresent(ThreadLocal<String> holder, String value) {
		if (value == null || value.isBlank()) {
			holder.remove();
			return;
		}
		holder.set(value);
	}

	private static void restore(ThreadLocal<String> holder, String previous) {
		if (previous == null) {
			holder.remove();
			return;
		}
		holder.set(previous);
	}
}
