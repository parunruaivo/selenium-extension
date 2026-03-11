package com.testrigor.seleniumextension.v4.application.commands;

import com.testrigor.seleniumextension.commons.application.commands.TestRigorSteps;
import com.testrigor.seleniumextension.v4.application.TestrigorDriver;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Fluent facade for validation/check commands.
 * <p>
 * Any method parameter describing an element follows testRigor's referencing syntax:
 * https://testrigor.com/docs/language#referencing
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessivePublicCount" })
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TestRigorValidations {

	TestrigorDriver driver;
	TestRigorSteps steps;

	public static TestRigorValidations validations(TestrigorDriver driver) {
		return new TestRigorValidations(driver, TestRigorSteps.create());
	}

	public static TestRigorValidations with(TestrigorDriver driver) {
		return validations(driver);
	}

	private TestRigorValidations withSteps(TestRigorSteps next) {
		return new TestRigorValidations(driver, next);
	}

	/** Readability no-op for fluent chaining between commands. */
	public TestRigorValidations and() {
		return this;
	}

	public TestRigorValidations checkPageContains(String text) {
		return withSteps(steps.checkPageContains(text));
	}

	public TestRigorValidations checkPageDoesNotContain(String text) {
		return withSteps(steps.checkPageDoesNotContain(text));
	}

	public TestRigorValidations checkPageContainsStoredValue(String varName) {
		return withSteps(steps.checkPageContainsStoredValue(varName));
	}

	public TestRigorValidations checkPageContainsRegex(String regex) {
		return withSteps(steps.checkPageContainsRegex(regex));
	}

	public TestRigorValidations checkPageDoesNotContainRegex(String regex) {
		return withSteps(steps.checkPageDoesNotContainRegex(regex));
	}

	public TestRigorValidations checkPageContainsTemplate(String template) {
		return withSteps(steps.checkPageContainsTemplate(template));
	}

	public TestRigorValidations checkPageDoesNotContainTemplate(String template) {
		return withSteps(steps.checkPageDoesNotContainTemplate(template));
	}

	public TestRigorValidations checkPageDidNotChange() {
		return withSteps(steps.checkPageDidNotChange());
	}

	public TestRigorValidations checkUrlContains(String text) {
		return withSteps(steps.checkUrlContains(text));
	}

	public TestRigorValidations checkUrlDoesNotContain(String text) {
		return withSteps(steps.checkUrlDoesNotContain(text));
	}

	public TestRigorValidations checkUrlStartsWith(String prefix) {
		return withSteps(steps.checkUrlStartsWith(prefix));
	}

	public TestRigorValidations checkUrlDoesNotStartWith(String prefix) {
		return withSteps(steps.checkUrlDoesNotStartWith(prefix));
	}

	public TestRigorValidations checkUrlEndsWith(String suffix) {
		return withSteps(steps.checkUrlEndsWith(suffix));
	}

	public TestRigorValidations checkUrlDoesNotEndWith(String suffix) {
		return withSteps(steps.checkUrlDoesNotEndWith(suffix));
	}

	public TestRigorValidations checkUrlIs(String url) {
		return withSteps(steps.checkUrlIs(url));
	}

	public TestRigorValidations checkUrlIsNot(String url) {
		return withSteps(steps.checkUrlIsNot(url));
	}

	public TestRigorValidations checkUrlMatchesRegex(String regex) {
		return withSteps(steps.checkUrlMatchesRegex(regex));
	}

	public TestRigorValidations checkUrlDoesNotMatchRegex(String regex) {
		return withSteps(steps.checkUrlDoesNotMatchRegex(regex));
	}

	public TestRigorValidations checkPageTitleIs(String title) {
		return withSteps(steps.checkPageTitleIs(title));
	}

	public TestRigorValidations checkPageTitleContains(String text) {
		return withSteps(steps.checkPageTitleContains(text));
	}

	public TestRigorValidations checkPageReturnCode(String code) {
		return withSteps(steps.checkPageReturnCode(code));
	}

	public TestRigorValidations checkButtonDisabled(String buttonDescription) {
		return withSteps(steps.checkButtonDisabled(buttonDescription));
	}

	public TestRigorValidations checkButtonEnabled(String buttonDescription) {
		return withSteps(steps.checkButtonEnabled(buttonDescription));
	}

	public TestRigorValidations checkThatElementContains(String elementDescription, String text) {
		return withSteps(steps.checkThatElementContains(elementDescription, text));
	}

	public TestRigorValidations checkThatElementDoesNotContain(String elementDescription, String text) {
		return withSteps(steps.checkThatElementDoesNotContain(elementDescription, text));
	}

	public TestRigorValidations checkThatElementContainsStoredValue(String elementDescription, String varName) {
		return withSteps(steps.checkThatElementContainsStoredValue(elementDescription, varName));
	}

	public TestRigorValidations checkThatElementDoesNotContainStoredValue(String elementDescription, String varName) {
		return withSteps(steps.checkThatElementDoesNotContainStoredValue(elementDescription, varName));
	}

	public TestRigorValidations checkThatInputHasValue(String elementDescription, String value) {
		return withSteps(steps.checkThatInputHasValue(elementDescription, value));
	}

	public TestRigorValidations checkThatInputHasStoredValue(String elementDescription, String varName) {
		return withSteps(steps.checkThatInputHasStoredValue(elementDescription, varName));
	}

	public TestRigorValidations checkThatInputDoesNotHaveValue(String elementDescription, String value) {
		return withSteps(steps.checkThatInputDoesNotHaveValue(elementDescription, value));
	}

	public TestRigorValidations checkThatInputDoesNotHaveStoredValue(String elementDescription, String varName) {
		return withSteps(steps.checkThatInputDoesNotHaveStoredValue(elementDescription, varName));
	}

	public TestRigorValidations checkThatCheckboxIsChecked(String elementDescription) {
		return withSteps(steps.checkThatCheckboxIsChecked(elementDescription));
	}

	public TestRigorValidations checkThatCheckboxIsUnchecked(String elementDescription) {
		return withSteps(steps.checkThatCheckboxIsUnchecked(elementDescription));
	}

	public TestRigorValidations checkThatElementIsEnabled(String elementDescription) {
		return withSteps(steps.checkThatElementIsEnabled(elementDescription));
	}

	public TestRigorValidations checkThatElementIsDisabled(String elementDescription) {
		return withSteps(steps.checkThatElementIsDisabled(elementDescription));
	}

	public TestRigorValidations checkThatElementIsVisible(String elementDescription) {
		return withSteps(steps.checkThatElementIsVisible(elementDescription));
	}

	public TestRigorValidations checkThatElementIsInvisible(String elementDescription) {
		return withSteps(steps.checkThatElementIsInvisible(elementDescription));
	}

	public TestRigorValidations checkThatElementIsClickable(String elementDescription) {
		return withSteps(steps.checkThatElementIsClickable(elementDescription));
	}

	public TestRigorValidations checkThatElementIsNotClickable(String elementDescription) {
		return withSteps(steps.checkThatElementIsNotClickable(elementDescription));
	}

	public TestRigorValidations checkThatSelectHasOptionSelected(String elementDescription, String option) {
		return withSteps(steps.checkThatSelectHasOptionSelected(elementDescription, option));
	}

	public TestRigorValidations checkThatSelectDoesNotHaveOptionSelected(String elementDescription, String option) {
		return withSteps(steps.checkThatSelectDoesNotHaveOptionSelected(elementDescription, option));
	}

	public TestRigorValidations checkThatElementMatchesRegex(String elementDescription, String regex) {
		return withSteps(steps.checkThatElementMatchesRegex(elementDescription, regex));
	}

	public TestRigorValidations checkThatElementDoesNotMatchRegex(String elementDescription, String regex) {
		return withSteps(steps.checkThatElementDoesNotMatchRegex(elementDescription, regex));
	}

	public TestRigorValidations checkThatElementMatchesTemplate(String elementDescription, String template) {
		return withSteps(steps.checkThatElementMatchesTemplate(elementDescription, template));
	}

	public TestRigorValidations checkThatElementDoesNotMatchTemplate(String elementDescription, String template) {
		return withSteps(steps.checkThatElementDoesNotMatchTemplate(elementDescription, template));
	}

	public TestRigorValidations checkThatElementHasAttribute(String elementDescription, String attributeName, String expectedValue) {
		return withSteps(steps.checkThatElementHasAttribute(elementDescription, attributeName, expectedValue));
	}

	public TestRigorValidations checkThatElementDoesNotHaveAttribute(String elementDescription, String attributeName) {
		return withSteps(steps.checkThatElementDoesNotHaveAttribute(elementDescription, attributeName));
	}

	public TestRigorValidations checkThatElementHasProperty(String elementDescription, String propertyName, String expectedValue) {
		return withSteps(steps.checkThatElementHasProperty(elementDescription, propertyName, expectedValue));
	}

	public TestRigorValidations checkThatElementDoesNotHaveProperty(String elementDescription, String propertyName) {
		return withSteps(steps.checkThatElementDoesNotHaveProperty(elementDescription, propertyName));
	}

	public TestRigorValidations checkThatElementHasCssClass(String elementDescription, String cssClass) {
		return withSteps(steps.checkThatElementHasCssClass(elementDescription, cssClass));
	}

	public TestRigorValidations checkThatElementDoesNotHaveCssClass(String elementDescription, String cssClass) {
		return withSteps(steps.checkThatElementDoesNotHaveCssClass(elementDescription, cssClass));
	}

	public TestRigorValidations checkThatElementColorIs(String elementDescription, String color) {
		return withSteps(steps.checkThatElementColorIs(elementDescription, color));
	}

	public TestRigorValidations checkThatElementColorIsNot(String elementDescription, String color) {
		return withSteps(steps.checkThatElementColorIsNot(elementDescription, color));
	}

	public TestRigorValidations checkThatElementBackgroundColorIs(String elementDescription, String color) {
		return withSteps(steps.checkThatElementBackgroundColorIs(elementDescription, color));
	}

	public TestRigorValidations checkThatElementBackgroundColorIsNot(String elementDescription, String color) {
		return withSteps(steps.checkThatElementBackgroundColorIsNot(elementDescription, color));
	}

	public TestRigorValidations checkThatElementCursorIs(String elementDescription, String cursor) {
		return withSteps(steps.checkThatElementCursorIs(elementDescription, cursor));
	}

	public TestRigorValidations checkThatElementCursorIsNot(String elementDescription, String cursor) {
		return withSteps(steps.checkThatElementCursorIsNot(elementDescription, cursor));
	}

	public TestRigorValidations checkThatElementHasLineStyle(String elementDescription, String lineStyle) {
		return withSteps(steps.checkThatElementHasLineStyle(elementDescription, lineStyle));
	}

	public TestRigorValidations checkThatElementHasNotLineStyle(String elementDescription, String lineStyle) {
		return withSteps(steps.checkThatElementHasNotLineStyle(elementDescription, lineStyle));
	}

	public TestRigorValidations checkThatElementIsBlank(String elementDescription) {
		return withSteps(steps.checkThatElementIsBlank(elementDescription));
	}

	public TestRigorValidations checkThatElementIsNotBlank(String elementDescription) {
		return withSteps(steps.checkThatElementIsNotBlank(elementDescription));
	}

	public TestRigorValidations checkThatElementIsNull(String elementDescription) {
		return withSteps(steps.checkThatElementIsNull(elementDescription));
	}

	public TestRigorValidations checkThatElementIsNotNull(String elementDescription) {
		return withSteps(steps.checkThatElementIsNotNull(elementDescription));
	}

	public TestRigorValidations checkThatElementIsEqual(String elementDescription, String value) {
		return withSteps(steps.checkThatElementIsEqual(elementDescription, value));
	}

	public TestRigorValidations checkThatElementIsNotEqual(String elementDescription, String value) {
		return withSteps(steps.checkThatElementIsNotEqual(elementDescription, value));
	}

	public TestRigorValidations checkThatElementIsEqualAsNumber(String elementDescription, String value) {
		return withSteps(steps.checkThatElementIsEqualAsNumber(elementDescription, value));
	}

	public TestRigorValidations checkThatElementIsNotEqualAsNumber(String elementDescription, String value) {
		return withSteps(steps.checkThatElementIsNotEqualAsNumber(elementDescription, value));
	}

	public TestRigorValidations checkThatElementIsGreaterThan(String elementDescription, String value) {
		return withSteps(steps.checkThatElementIsGreaterThan(elementDescription, value));
	}

	public TestRigorValidations checkThatElementIsGreaterThanOrEqualTo(String elementDescription, String value) {
		return withSteps(steps.checkThatElementIsGreaterThanOrEqualTo(elementDescription, value));
	}

	public TestRigorValidations checkThatElementIsLessThan(String elementDescription, String value) {
		return withSteps(steps.checkThatElementIsLessThan(elementDescription, value));
	}

	public TestRigorValidations checkThatElementIsLessThanOrEqualTo(String elementDescription, String value) {
		return withSteps(steps.checkThatElementIsLessThanOrEqualTo(elementDescription, value));
	}

	public TestRigorValidations checkThatElementIsLexicographicallyBefore(String elementDescription, String value) {
		return withSteps(steps.checkThatElementIsLexicographicallyBefore(elementDescription, value));
	}

	public TestRigorValidations checkThatElementIsLexicographicallyBeforeOrSame(String elementDescription, String value) {
		return withSteps(steps.checkThatElementIsLexicographicallyBeforeOrSame(elementDescription, value));
	}

	public TestRigorValidations checkThatElementIsLexicographicallyAfter(String elementDescription, String value) {
		return withSteps(steps.checkThatElementIsLexicographicallyAfter(elementDescription, value));
	}

	public TestRigorValidations checkThatElementIsLexicographicallyAfterOrSame(String elementDescription, String value) {
		return withSteps(steps.checkThatElementIsLexicographicallyAfterOrSame(elementDescription, value));
	}

	public TestRigorValidations checkThatStoredValueItselfContains(String varName, String text) {
		return withSteps(steps.checkThatStoredValueItselfContains(varName, text));
	}

	public TestRigorValidations checkThatStoredValueItselfDoesNotContain(String varName, String text) {
		return withSteps(steps.checkThatStoredValueItselfDoesNotContain(varName, text));
	}

	public TestRigorValidations checkThatStoredValueItselfMatchesRegex(String varName, String regex) {
		return withSteps(steps.checkThatStoredValueItselfMatchesRegex(varName, regex));
	}

	public TestRigorValidations checkThatStoredValueItselfDoesNotMatchRegex(String varName, String regex) {
		return withSteps(steps.checkThatStoredValueItselfDoesNotMatchRegex(varName, regex));
	}

	public TestRigorValidations checkThatStoredValueItselfMatchesTemplate(String varName, String template) {
		return withSteps(steps.checkThatStoredValueItselfMatchesTemplate(varName, template));
	}

	public TestRigorValidations checkThatStoredValueItselfDoesNotMatchTemplate(String varName, String template) {
		return withSteps(steps.checkThatStoredValueItselfDoesNotMatchTemplate(varName, template));
	}

	public TestRigorValidations checkThatStoredValueItselfIsBlank(String varName) {
		return withSteps(steps.checkThatStoredValueItselfIsBlank(varName));
	}

	public TestRigorValidations checkThatStoredValueItselfIsNotBlank(String varName) {
		return withSteps(steps.checkThatStoredValueItselfIsNotBlank(varName));
	}

	public TestRigorValidations checkThatStoredValueItselfIsNull(String varName) {
		return withSteps(steps.checkThatStoredValueItselfIsNull(varName));
	}

	public TestRigorValidations checkThatStoredValueItselfIsNotNull(String varName) {
		return withSteps(steps.checkThatStoredValueItselfIsNotNull(varName));
	}

	public TestRigorValidations checkThatStoredValueItselfIsEqual(String varName, String value) {
		return withSteps(steps.checkThatStoredValueItselfIsEqual(varName, value));
	}

	public TestRigorValidations checkThatStoredValueItselfIsNotEqual(String varName, String value) {
		return withSteps(steps.checkThatStoredValueItselfIsNotEqual(varName, value));
	}

	public TestRigorValidations checkThatStoredValueItselfIsEqualAsNumber(String varName, String value) {
		return withSteps(steps.checkThatStoredValueItselfIsEqualAsNumber(varName, value));
	}

	public TestRigorValidations checkThatStoredValueItselfIsNotEqualAsNumber(String varName, String value) {
		return withSteps(steps.checkThatStoredValueItselfIsNotEqualAsNumber(varName, value));
	}

	public TestRigorValidations checkThatStoredValueItselfIsGreaterThan(String varName, String value) {
		return withSteps(steps.checkThatStoredValueItselfIsGreaterThan(varName, value));
	}

	public TestRigorValidations checkThatStoredValueItselfIsGreaterThanOrEqualTo(String varName, String value) {
		return withSteps(steps.checkThatStoredValueItselfIsGreaterThanOrEqualTo(varName, value));
	}

	public TestRigorValidations checkThatStoredValueItselfIsLessThan(String varName, String value) {
		return withSteps(steps.checkThatStoredValueItselfIsLessThan(varName, value));
	}

	public TestRigorValidations checkThatStoredValueItselfIsLessThanOrEqualTo(String varName, String value) {
		return withSteps(steps.checkThatStoredValueItselfIsLessThanOrEqualTo(varName, value));
	}

	public void execute() {
		driver.executePrompt(steps.build());
	}

	public String buildPrompt() {
		return steps.build();
	}
}
