package com.testrigor.seleniumextension.commons.application.grpc;

/**
 * Action names for {@code executeAction} — must match
 * {@code com.testrigor.seleniumextension.application.grpc.GrpcActionContracts} on the server.
 */
public final class SeleniumExtensionGrpcActions {

	public static final String SAVE_SELENIUM_LOCATOR = "saveSeleniumLocator";
	public static final String GET_HEALED_SELENIUM_LOCATOR = "getHealedSeleniumLocator";
	public static final String RECORD_SELENIUM_LOCATOR_MAPPING = "recordSeleniumLocatorMapping";

	public static final String KEY_LOCATOR_TYPE = "locatorType";
	public static final String KEY_LOCATOR_VALUE = "locatorValue";

	public static final String KEY_ORIGINAL_LOCATOR_TYPE = "originalLocatorType";
	public static final String KEY_ORIGINAL_LOCATOR_VALUE = "originalLocatorValue";
	public static final String KEY_HEALED_LOCATOR_TYPE = "healedLocatorType";
	public static final String KEY_HEALED_LOCATOR_VALUE = "healedLocatorValue";

	private SeleniumExtensionGrpcActions() {
	}
}
