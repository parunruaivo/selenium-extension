package com.testrigor.seleniumextension.commons.application.commands;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Fluent builder for testRigor plain-English command steps.
 * Each step is one line; {@link #build()} returns the full prompt (newline-separated).
 * Syntax follows https://testrigor.com/docs/language.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessivePublicCount" })
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TestRigorSteps {

	private static final int MAX_ORDINAL_INDEX = 20;

	List<String> steps;

	public static TestRigorSteps create() {
		return new TestRigorSteps(new ArrayList<>());
	}

	private TestRigorSteps addStep(String line) {
		List<String> next = new ArrayList<>(steps);
		next.add(line);
		return new TestRigorSteps(next);
	}

	/** Readability no-op for fluent chaining between commands. */
	public TestRigorSteps and() {
		return this;
	}

	/** Click "elementDescription". */
	public TestRigorSteps click(String elementDescription) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription));
	}

	/** Tap on "elementDescription". */
	public TestRigorSteps tap(String elementDescription) {
		return addStep("tap on " + StepEscaping.quotedSegment(elementDescription));
	}

	/** Press "elementDescription" (click alias). */
	public TestRigorSteps press(String elementDescription) {
		return addStep("press " + StepEscaping.quotedSegment(elementDescription));
	}

	/** Push "elementDescription" (click alias). */
	public TestRigorSteps push(String elementDescription) {
		return addStep("push " + StepEscaping.quotedSegment(elementDescription));
	}

	/** Follow "elementDescription" (click alias). */
	public TestRigorSteps follow(String elementDescription) {
		return addStep("follow " + StepEscaping.quotedSegment(elementDescription));
	}

	/**
	 * Fluent contextual click builder for advanced target scoping/positioning.
	 * Finalize with {@link ClickTarget#add()}.
	 */
	public ClickTarget clickOn(String elementDescription) {
		return new ClickTarget(this, elementDescription, null, null, null, null, null, null);
	}

	/** Click on the nth "hello" (n is 1-based). */
	public TestRigorSteps click(int nth, String elementDescription) {
		return addStep("click on the " + ordinal(nth) + " " + StepEscaping.quotedSegment(elementDescription));
	}

	public TestRigorSteps doubleClick(String elementDescription) {
		return addStep("double click on " + StepEscaping.quotedSegment(elementDescription));
	}

	public TestRigorSteps rightClick(String elementDescription) {
		return addStep("right click on " + StepEscaping.quotedSegment(elementDescription));
	}

	public TestRigorSteps longClick(String elementDescription) {
		return addStep("long click on " + StepEscaping.quotedSegment(elementDescription));
	}

	public TestRigorSteps tripleClick(String elementDescription) {
		return addStep("triple click " + StepEscaping.quotedSegment(elementDescription));
	}

	public TestRigorSteps middleClick(String elementDescription) {
		return addStep("middle click " + StepEscaping.quotedSegment(elementDescription));
	}

	public TestRigorSteps wheelClick(String elementDescription) {
		return addStep("wheel click " + StepEscaping.quotedSegment(elementDescription));
	}

	public TestRigorSteps clickTimes(String elementDescription, int times) {
		return clickTimesLine("click", elementDescription, times);
	}

	public TestRigorSteps doubleClickTimes(String elementDescription, int times) {
		return clickTimesLine("double click", elementDescription, times);
	}

	public TestRigorSteps tripleClickTimes(String elementDescription, int times) {
		return clickTimesLine("triple click", elementDescription, times);
	}

	public TestRigorSteps rightClickTimes(String elementDescription, int times) {
		return clickTimesLine("right click", elementDescription, times);
	}

	public TestRigorSteps middleClickTimes(String elementDescription, int times) {
		return clickTimesLine("middle click", elementDescription, times);
	}

	public TestRigorSteps wheelClickTimes(String elementDescription, int times) {
		return clickTimesLine("wheel click", elementDescription, times);
	}

	public TestRigorSteps clickIfExists(String elementDescription) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " if exists");
	}

	public TestRigorSteps clickIfExistsWithWaiting(String elementDescription) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " if exists with waiting");
	}

	public TestRigorSteps clickIfExistsWithoutWaiting(String elementDescription) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " if exists without waiting");
	}

	public TestRigorSteps clickAndSwitchToNewTab(String elementDescription) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " and switch to the new tab");
	}

	public TestRigorSteps clickIfExistsAndSwitchToNewTab(String elementDescription) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " if exists and switch to the new tab");
	}

	public TestRigorSteps clickIfExistsWithWaitingAndSwitchToNewTab(String elementDescription) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " if exists with waiting and switch to the new tab");
	}

	public TestRigorSteps clickIfPageContains(String elementDescription, String expectedText) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " if page contains " + StepEscaping.quotedSegment(expectedText));
	}

	public TestRigorSteps clickIfPageContainsWithWaiting(String elementDescription, String expectedText) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " if page contains "
			+ StepEscaping.quotedSegment(expectedText) + " with waiting");
	}

	public TestRigorSteps clickIfPageContainsWithoutWaiting(String elementDescription, String expectedText) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " if page contains "
			+ StepEscaping.quotedSegment(expectedText) + " without waiting");
	}

	public TestRigorSteps clickIfPageDoesNotContain(String elementDescription, String expectedText) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " if page does not contain "
			+ StepEscaping.quotedSegment(expectedText));
	}

	public TestRigorSteps clickIfUrlContains(String elementDescription, String expectedText) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " if url contains " + StepEscaping.quotedSegment(expectedText));
	}

	public TestRigorSteps pressIfPageContains(String elementDescription, String expectedText) {
		return addStep("press " + StepEscaping.quotedSegment(elementDescription) + " if page contains " + StepEscaping.quotedSegment(expectedText));
	}

	/**
	 * Enter "value" into "fieldDescription".
	 * Use {@link #enter(String)} and then {@link EnterInto#into(String)}.
	 */
	public EnterInto enter(String value) {
		return new EnterInto(value, this);
	}

	/**
	 * Fluent contextual enter builder for advanced target scoping/positioning.
	 * Use {@link EnterTargetInto#into(String)} then finalize with {@link EnterTarget#add()}.
	 */
	public EnterTargetInto enterFluent(String value) {
		return new EnterTargetInto(value, this);
	}

	/** Enter stored value "varName" into "fieldDescription". */
	public EnterStoredInto enterStoredValue(String varName) {
		return new EnterStoredInto(varName, this);
	}

	/** Select "value" from "dropdownDescription". */
	public TestRigorSteps select(String value, String dropdownDescription) {
		return addStep("select " + StepEscaping.quotedSegment(value) + " from " + StepEscaping.quotedSegment(dropdownDescription));
	}

	/** Choose "value" from "dropdownDescription". */
	public TestRigorSteps choose(String value, String dropdownDescription) {
		return addStep("choose " + StepEscaping.quotedSegment(value) + " from " + StepEscaping.quotedSegment(dropdownDescription));
	}

	/** Insert "value" into "fieldDescription". */
	public TestRigorSteps insert(String value, String fieldDescription) {
		return addStep("insert " + StepEscaping.quotedSegment(value) + " into " + StepEscaping.quotedSegment(fieldDescription));
	}

	/** Select nth option from "MySelect" (n is 1-based). */
	public TestRigorSteps selectNthOption(int nthOption, String dropdownDescription) {
		return addStep("select " + ordinal(nthOption) + " option from " + StepEscaping.quotedSegment(dropdownDescription));
	}

	/** Select option N from "MySelect" using numeric form (e.g. "select option 10 from"). */
	public TestRigorSteps selectOption(int optionNumber, String dropdownDescription) {
		return addStep("select option " + optionNumber + " from " + StepEscaping.quotedSegment(dropdownDescription));
	}

	/** Check that page contains "text". */
	public TestRigorSteps checkPageContains(String text) {
		return addStep("check that page contains " + StepEscaping.quotedSegment(text));
	}

	/** Check that page does not contain "text". */
	public TestRigorSteps checkPageDoesNotContain(String text) {
		return addStep("check that page does not contain " + StepEscaping.quotedSegment(text));
	}

	/** Check that page contains stored value from "varName". */
	public TestRigorSteps checkPageContainsStoredValue(String varName) {
		return addStep("check that page contains stored value from " + StepEscaping.quotedSegment(varName));
	}

	/** Check that page contains regex. */
	public TestRigorSteps checkPageContainsRegex(String regex) {
		return addStep("check that page has regex " + StepEscaping.quotedSegment(regex));
	}

	/** Check that page does not contain regex. */
	public TestRigorSteps checkPageDoesNotContainRegex(String regex) {
		return addStep("check that page does not have regex " + StepEscaping.quotedSegment(regex));
	}

	/** Check that page contains template. */
	public TestRigorSteps checkPageContainsTemplate(String template) {
		return addStep("check that page has simple template " + StepEscaping.quotedSegment(template));
	}

	/** Check that page does not contain template. */
	public TestRigorSteps checkPageDoesNotContainTemplate(String template) {
		return addStep("check that page does not have simple template " + StepEscaping.quotedSegment(template));
	}

	/** Check that page didn't change. */
	public TestRigorSteps checkPageDidNotChange() {
		return addStep("check that page didn't change");
	}

	/** Check that URL contains "text". */
	public TestRigorSteps checkUrlContains(String text) {
		return addStep("check that url contains " + StepEscaping.quotedSegment(text));
	}

	/** Check that URL does not contain "text". */
	public TestRigorSteps checkUrlDoesNotContain(String text) {
		return addStep("check that url does not contain " + StepEscaping.quotedSegment(text));
	}

	/** Check that URL starts with "prefix". */
	public TestRigorSteps checkUrlStartsWith(String prefix) {
		return addStep("check that url starts with " + StepEscaping.quotedSegment(prefix));
	}

	/** Check that URL does not start with "prefix". */
	public TestRigorSteps checkUrlDoesNotStartWith(String prefix) {
		return addStep("check that url does not start with " + StepEscaping.quotedSegment(prefix));
	}

	/** Check that URL ends with "suffix". */
	public TestRigorSteps checkUrlEndsWith(String suffix) {
		return addStep("check that url ends with " + StepEscaping.quotedSegment(suffix));
	}

	/** Check that URL does not end with "suffix". */
	public TestRigorSteps checkUrlDoesNotEndWith(String suffix) {
		return addStep("check that url does not end with " + StepEscaping.quotedSegment(suffix));
	}

	/** Check that URL is exactly "url". */
	public TestRigorSteps checkUrlIs(String url) {
		return addStep("check that url is " + StepEscaping.quotedSegment(url));
	}

	/** Check that URL is not exactly "url". */
	public TestRigorSteps checkUrlIsNot(String url) {
		return addStep("check that url is not " + StepEscaping.quotedSegment(url));
	}

	/** Check that URL matches regex. */
	public TestRigorSteps checkUrlMatchesRegex(String regex) {
		return addStep("check that url matches regex " + StepEscaping.quotedSegment(regex));
	}

	/** Check that URL does not match regex. */
	public TestRigorSteps checkUrlDoesNotMatchRegex(String regex) {
		return addStep("check that url does not match regex " + StepEscaping.quotedSegment(regex));
	}

	/** Check that page title is "title". */
	public TestRigorSteps checkPageTitleIs(String title) {
		return addStep("check that page title is " + StepEscaping.quotedSegment(title));
	}

	/** Check that page title contains "text". */
	public TestRigorSteps checkPageTitleContains(String text) {
		return addStep("check that page title contains " + StepEscaping.quotedSegment(text));
	}

	/** Check that page return code is "code". */
	public TestRigorSteps checkPageReturnCode(String code) {
		return addStep("check that page return code is " + StepEscaping.quotedSegment(code));
	}

	/** Check that button "buttonDescription" is disabled. */
	public TestRigorSteps checkButtonDisabled(String buttonDescription) {
		return addStep("check that button " + StepEscaping.quotedSegment(buttonDescription) + " is disabled");
	}

	/** Check that button "buttonDescription" is enabled. */
	public TestRigorSteps checkButtonEnabled(String buttonDescription) {
		return addStep("check that button " + StepEscaping.quotedSegment(buttonDescription) + " is enabled");
	}

	/** Check that "elementDescription" contains "text". */
	public TestRigorSteps checkThatElementContains(String elementDescription, String text) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " contains " + StepEscaping.quotedSegment(text));
	}

	/** Check that "elementDescription" does not contain "text". */
	public TestRigorSteps checkThatElementDoesNotContain(String elementDescription, String text) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " does not contain " + StepEscaping.quotedSegment(text));
	}

	/** Check that "elementDescription" contains stored value from "varName". */
	public TestRigorSteps checkThatElementContainsStoredValue(String elementDescription, String varName) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " contains stored value from " + StepEscaping.quotedSegment(varName));
	}

	/** Check that "elementDescription" does not contain stored value from "varName". */
	public TestRigorSteps checkThatElementDoesNotContainStoredValue(String elementDescription, String varName) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " does not contain stored value from " + StepEscaping.quotedSegment(varName));
	}

	/** Check that input "elementDescription" has value "value". */
	public TestRigorSteps checkThatInputHasValue(String elementDescription, String value) {
		return addStep("check that input " + StepEscaping.quotedSegment(elementDescription) + " has value " + StepEscaping.quotedSegment(value));
	}

	/** Check that input "elementDescription" has stored value from "varName". */
	public TestRigorSteps checkThatInputHasStoredValue(String elementDescription, String varName) {
		return addStep("check that input " + StepEscaping.quotedSegment(elementDescription) + " has value stored value from " + StepEscaping.quotedSegment(varName));
	}

	/** Check that input "elementDescription" does not have value "value". */
	public TestRigorSteps checkThatInputDoesNotHaveValue(String elementDescription, String value) {
		return addStep("check that input " + StepEscaping.quotedSegment(elementDescription) + " does not have value " + StepEscaping.quotedSegment(value));
	}

	/** Check that input "elementDescription" does not have stored value from "varName". */
	public TestRigorSteps checkThatInputDoesNotHaveStoredValue(String elementDescription, String varName) {
		return addStep("check that input " + StepEscaping.quotedSegment(elementDescription) + " does not have value stored value from "
			+ StepEscaping.quotedSegment(varName));
	}

	/** Check that checkbox "elementDescription" is checked. */
	public TestRigorSteps checkThatCheckboxIsChecked(String elementDescription) {
		return addStep("check that checkbox " + StepEscaping.quotedSegment(elementDescription) + " is checked");
	}

	/** Check that checkbox "elementDescription" is unchecked. */
	public TestRigorSteps checkThatCheckboxIsUnchecked(String elementDescription) {
		return addStep("check that checkbox " + StepEscaping.quotedSegment(elementDescription) + " is unchecked");
	}

	/** Check that "elementDescription" is enabled. */
	public TestRigorSteps checkThatElementIsEnabled(String elementDescription) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is enabled");
	}

	/** Check that "elementDescription" is disabled. */
	public TestRigorSteps checkThatElementIsDisabled(String elementDescription) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is disabled");
	}

	/** Check that "elementDescription" is visible. */
	public TestRigorSteps checkThatElementIsVisible(String elementDescription) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is visible");
	}

	/** Check that "elementDescription" is invisible. */
	public TestRigorSteps checkThatElementIsInvisible(String elementDescription) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is invisible");
	}

	/** Check that "elementDescription" is clickable. */
	public TestRigorSteps checkThatElementIsClickable(String elementDescription) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is clickable");
	}

	/** Check that "elementDescription" is not clickable. */
	public TestRigorSteps checkThatElementIsNotClickable(String elementDescription) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is not clickable");
	}

	/** Check that select "elementDescription" has option selected "option". */
	public TestRigorSteps checkThatSelectHasOptionSelected(String elementDescription, String option) {
		return addStep("check that select " + StepEscaping.quotedSegment(elementDescription) + " has option selected " + StepEscaping.quotedSegment(option));
	}

	/** Check that select "elementDescription" does not have option selected "option". */
	public TestRigorSteps checkThatSelectDoesNotHaveOptionSelected(String elementDescription, String option) {
		return addStep("check that select " + StepEscaping.quotedSegment(elementDescription) + " does not have option selected " + StepEscaping.quotedSegment(option));
	}

	/** Check that "elementDescription" matches regex. */
	public TestRigorSteps checkThatElementMatchesRegex(String elementDescription, String regex) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " matches regex " + StepEscaping.quotedSegment(regex));
	}

	/** Check that "elementDescription" does not match regex. */
	public TestRigorSteps checkThatElementDoesNotMatchRegex(String elementDescription, String regex) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " does not match regex " + StepEscaping.quotedSegment(regex));
	}

	/** Check that "elementDescription" matches template. */
	public TestRigorSteps checkThatElementMatchesTemplate(String elementDescription, String template) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " matches template " + StepEscaping.quotedSegment(template));
	}

	/** Check that "elementDescription" does not match template. */
	public TestRigorSteps checkThatElementDoesNotMatchTemplate(String elementDescription, String template) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " does not match template " + StepEscaping.quotedSegment(template));
	}

	/** Check that "elementDescription" has attribute "attributeName" equal to "expectedValue". */
	public TestRigorSteps checkThatElementHasAttribute(String elementDescription, String attributeName, String expectedValue) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " has attribute " + StepEscaping.quotedSegment(attributeName)
			+ " equal to " + StepEscaping.quotedSegment(expectedValue));
	}

	/** Check that "elementDescription" does not have attribute "attributeName". */
	public TestRigorSteps checkThatElementDoesNotHaveAttribute(String elementDescription, String attributeName) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " does not have attribute " + StepEscaping.quotedSegment(attributeName));
	}

	/** Check that "elementDescription" has property "propertyName" equal to "expectedValue". */
	public TestRigorSteps checkThatElementHasProperty(String elementDescription, String propertyName, String expectedValue) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " has property " + StepEscaping.quotedSegment(propertyName)
			+ " equal to " + StepEscaping.quotedSegment(expectedValue));
	}

	/** Check that "elementDescription" does not have property "propertyName". */
	public TestRigorSteps checkThatElementDoesNotHaveProperty(String elementDescription, String propertyName) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " does not have property " + StepEscaping.quotedSegment(propertyName));
	}

	/** Check that "elementDescription" has css class "cssClass". */
	public TestRigorSteps checkThatElementHasCssClass(String elementDescription, String cssClass) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " has css class " + StepEscaping.quotedSegment(cssClass));
	}

	/** Check that "elementDescription" does not have css class "cssClass". */
	public TestRigorSteps checkThatElementDoesNotHaveCssClass(String elementDescription, String cssClass) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " does not have css class " + StepEscaping.quotedSegment(cssClass));
	}

	/** Check that "elementDescription" color is "color". */
	public TestRigorSteps checkThatElementColorIs(String elementDescription, String color) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " color is " + StepEscaping.quotedSegment(color));
	}

	/** Check that "elementDescription" color is not "color". */
	public TestRigorSteps checkThatElementColorIsNot(String elementDescription, String color) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " color is not " + StepEscaping.quotedSegment(color));
	}

	/** Check that "elementDescription" background color is "color". */
	public TestRigorSteps checkThatElementBackgroundColorIs(String elementDescription, String color) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " background color is " + StepEscaping.quotedSegment(color));
	}

	/** Check that "elementDescription" background color is not "color". */
	public TestRigorSteps checkThatElementBackgroundColorIsNot(String elementDescription, String color) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " background color is not " + StepEscaping.quotedSegment(color));
	}

	/** Check that "elementDescription" cursor is "cursor". */
	public TestRigorSteps checkThatElementCursorIs(String elementDescription, String cursor) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " cursor is " + StepEscaping.quotedSegment(cursor));
	}

	/** Check that "elementDescription" cursor is not "cursor". */
	public TestRigorSteps checkThatElementCursorIsNot(String elementDescription, String cursor) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " cursor is not " + StepEscaping.quotedSegment(cursor));
	}

	/** Check that "elementDescription" has line style "lineStyle". */
	public TestRigorSteps checkThatElementHasLineStyle(String elementDescription, String lineStyle) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " has line style " + StepEscaping.quotedSegment(lineStyle));
	}

	/** Check that "elementDescription" has not line style "lineStyle". */
	public TestRigorSteps checkThatElementHasNotLineStyle(String elementDescription, String lineStyle) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " has not line style " + StepEscaping.quotedSegment(lineStyle));
	}

	/** Check that "elementDescription" is blank. */
	public TestRigorSteps checkThatElementIsBlank(String elementDescription) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is blank");
	}

	/** Check that "elementDescription" is not blank. */
	public TestRigorSteps checkThatElementIsNotBlank(String elementDescription) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is not blank");
	}

	/** Check that "elementDescription" is null. */
	public TestRigorSteps checkThatElementIsNull(String elementDescription) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is null");
	}

	/** Check that "elementDescription" is not null. */
	public TestRigorSteps checkThatElementIsNotNull(String elementDescription) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is not null");
	}

	/** Check that "elementDescription" is equal to "value". */
	public TestRigorSteps checkThatElementIsEqual(String elementDescription, String value) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is equal to " + StepEscaping.quotedSegment(value));
	}

	/** Check that "elementDescription" is not equal to "value". */
	public TestRigorSteps checkThatElementIsNotEqual(String elementDescription, String value) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is not equal to " + StepEscaping.quotedSegment(value));
	}

	/** Check that "elementDescription" is equal as number to "value". */
	public TestRigorSteps checkThatElementIsEqualAsNumber(String elementDescription, String value) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is equal as number to " + StepEscaping.quotedSegment(value));
	}

	/** Check that "elementDescription" is not equal as number to "value". */
	public TestRigorSteps checkThatElementIsNotEqualAsNumber(String elementDescription, String value) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is not equal as number to " + StepEscaping.quotedSegment(value));
	}

	/** Check that "elementDescription" is greater than "value". */
	public TestRigorSteps checkThatElementIsGreaterThan(String elementDescription, String value) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is greater than " + StepEscaping.quotedSegment(value));
	}

	/** Check that "elementDescription" is greater than or equal to "value". */
	public TestRigorSteps checkThatElementIsGreaterThanOrEqualTo(String elementDescription, String value) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is greater than or equal to " + StepEscaping.quotedSegment(value));
	}

	/** Check that "elementDescription" is less than "value". */
	public TestRigorSteps checkThatElementIsLessThan(String elementDescription, String value) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is less than " + StepEscaping.quotedSegment(value));
	}

	/** Check that "elementDescription" is less than or equal to "value". */
	public TestRigorSteps checkThatElementIsLessThanOrEqualTo(String elementDescription, String value) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " is less than or equal to " + StepEscaping.quotedSegment(value));
	}

	/** Check lexicographic comparison where element is before value. */
	public TestRigorSteps checkThatElementIsLexicographicallyBefore(String elementDescription, String value) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " lexicographically before " + StepEscaping.quotedSegment(value));
	}

	/** Check lexicographic comparison where element is before or same as value. */
	public TestRigorSteps checkThatElementIsLexicographicallyBeforeOrSame(String elementDescription, String value) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " lexicographically before or same " + StepEscaping.quotedSegment(value));
	}

	/** Check lexicographic comparison where element is after value. */
	public TestRigorSteps checkThatElementIsLexicographicallyAfter(String elementDescription, String value) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " lexicographically after " + StepEscaping.quotedSegment(value));
	}

	/** Check lexicographic comparison where element is after or same as value. */
	public TestRigorSteps checkThatElementIsLexicographicallyAfterOrSame(String elementDescription, String value) {
		return addStep("check that " + StepEscaping.quotedSegment(elementDescription) + " lexicographically after or same " + StepEscaping.quotedSegment(value));
	}

	/** Check that stored value "varName" itself contains "text". */
	public TestRigorSteps checkThatStoredValueItselfContains(String varName, String text) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself contains " + StepEscaping.quotedSegment(text));
	}

	/** Check that stored value "varName" itself does not contain "text". */
	public TestRigorSteps checkThatStoredValueItselfDoesNotContain(String varName, String text) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself does not contain " + StepEscaping.quotedSegment(text));
	}

	/** Check that stored value "varName" itself matches regex. */
	public TestRigorSteps checkThatStoredValueItselfMatchesRegex(String varName, String regex) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself matches regex " + StepEscaping.quotedSegment(regex));
	}

	/** Check that stored value "varName" itself does not match regex. */
	public TestRigorSteps checkThatStoredValueItselfDoesNotMatchRegex(String varName, String regex) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself does not match regex " + StepEscaping.quotedSegment(regex));
	}

	/** Check that stored value "varName" itself matches template. */
	public TestRigorSteps checkThatStoredValueItselfMatchesTemplate(String varName, String template) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself matches template " + StepEscaping.quotedSegment(template));
	}

	/** Check that stored value "varName" itself does not match template. */
	public TestRigorSteps checkThatStoredValueItselfDoesNotMatchTemplate(String varName, String template) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself does not match template " + StepEscaping.quotedSegment(template));
	}

	/** Check that stored value "varName" itself is blank. */
	public TestRigorSteps checkThatStoredValueItselfIsBlank(String varName) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself is blank");
	}

	/** Check that stored value "varName" itself is not blank. */
	public TestRigorSteps checkThatStoredValueItselfIsNotBlank(String varName) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself is not blank");
	}

	/** Check that stored value "varName" itself is null. */
	public TestRigorSteps checkThatStoredValueItselfIsNull(String varName) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself is null");
	}

	/** Check that stored value "varName" itself is not null. */
	public TestRigorSteps checkThatStoredValueItselfIsNotNull(String varName) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself is not null");
	}

	/** Check that stored value "varName" itself is equal to "value". */
	public TestRigorSteps checkThatStoredValueItselfIsEqual(String varName, String value) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself is equal to " + StepEscaping.quotedSegment(value));
	}

	/** Check that stored value "varName" itself is not equal to "value". */
	public TestRigorSteps checkThatStoredValueItselfIsNotEqual(String varName, String value) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself is not equal to " + StepEscaping.quotedSegment(value));
	}

	/** Check that stored value "varName" itself is equal as number to "value". */
	public TestRigorSteps checkThatStoredValueItselfIsEqualAsNumber(String varName, String value) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself is equal as number to " + StepEscaping.quotedSegment(value));
	}

	/** Check that stored value "varName" itself is not equal as number to "value". */
	public TestRigorSteps checkThatStoredValueItselfIsNotEqualAsNumber(String varName, String value) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself is not equal as number to " + StepEscaping.quotedSegment(value));
	}

	/** Check that stored value "varName" itself is greater than "value". */
	public TestRigorSteps checkThatStoredValueItselfIsGreaterThan(String varName, String value) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself is greater than " + StepEscaping.quotedSegment(value));
	}

	/** Check that stored value "varName" itself is greater than or equal to "value". */
	public TestRigorSteps checkThatStoredValueItselfIsGreaterThanOrEqualTo(String varName, String value) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself is greater than or equal to " + StepEscaping.quotedSegment(value));
	}

	/** Check that stored value "varName" itself is less than "value". */
	public TestRigorSteps checkThatStoredValueItselfIsLessThan(String varName, String value) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself is less than " + StepEscaping.quotedSegment(value));
	}

	/** Check that stored value "varName" itself is less than or equal to "value". */
	public TestRigorSteps checkThatStoredValueItselfIsLessThanOrEqualTo(String varName, String value) {
		return addStep("check that stored value " + StepEscaping.quotedSegment(varName) + " itself is less than or equal to " + StepEscaping.quotedSegment(value));
	}

	/** Hover over "elementDescription". */
	public TestRigorSteps hoverOver(String elementDescription) {
		return addStep("hover over " + StepEscaping.quotedSegment(elementDescription));
	}

	/** Hover over nth "element" (n is 1-based). */
	public TestRigorSteps hoverOver(int nth, String elementDescription) {
		return addStep("hover over " + ordinal(nth) + " " + StepEscaping.quotedSegment(elementDescription));
	}

	/** Scroll down. */
	public TestRigorSteps scrollDown() {
		return addStep("scroll down");
	}

	public TestRigorSteps scrollUp() {
		return addStep("scroll up");
	}

	/** Scroll left. */
	public TestRigorSteps scrollLeft() {
		return addStep("scroll left");
	}

	/** Scroll right. */
	public TestRigorSteps scrollRight() {
		return addStep("scroll right");
	}

	/** Scroll down on "elementDescription". */
	public TestRigorSteps scrollDownOn(String elementDescription) {
		return addStep("scroll down on " + StepEscaping.quotedSegment(elementDescription));
	}

	/** Scroll up on "elementDescription". */
	public TestRigorSteps scrollUpOn(String elementDescription) {
		return addStep("scroll up on " + StepEscaping.quotedSegment(elementDescription));
	}

	/** Scroll down until page contains "text". */
	public TestRigorSteps scrollDownUntilPageContains(String text) {
		return addStep("scroll down until page contains " + StepEscaping.quotedSegment(text));
	}

	/** Scroll down up to N times until page contains "text". */
	public TestRigorSteps scrollDownUntilPageContains(String text, int maxTimes) {
		return addStep("scroll down up to " + maxTimes + " times until page contains " + StepEscaping.quotedSegment(text));
	}

	/** Click until page contains "text". */
	public TestRigorSteps clickUntilPageContains(String elementDescription, String expectedText) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " until page contains " + StepEscaping.quotedSegment(expectedText));
	}

	/** Click until page contains stored value "varName". */
	public TestRigorSteps clickUntilPageContainsStoredValue(String elementDescription, String varName) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " until page contains stored value " + StepEscaping.quotedSegment(varName));
	}

	/** Click up to N times until page contains "text". */
	public TestRigorSteps clickUntilPageContains(String elementDescription, String expectedText, int maxTimes) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " up to " + maxTimes + " times until page contains "
			+ StepEscaping.quotedSegment(expectedText));
	}

	/** Click until page contains "text" with waiting. */
	public TestRigorSteps clickUntilPageContainsWithWaiting(String elementDescription, String expectedText) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " until page contains "
			+ StepEscaping.quotedSegment(expectedText) + " with waiting");
	}

	/** Click until page contains "text" without waiting. */
	public TestRigorSteps clickUntilPageContainsWithoutWaiting(String elementDescription, String expectedText) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " until page contains "
			+ StepEscaping.quotedSegment(expectedText) + " without waiting");
	}

	/** Click up to N times until page contains "text" with waiting. */
	public TestRigorSteps clickUntilPageContainsWithWaiting(String elementDescription, String expectedText, int maxTimes) {
		return addStep("click " + StepEscaping.quotedSegment(elementDescription) + " up to " + maxTimes + " times until page contains "
			+ StepEscaping.quotedSegment(expectedText) + " with waiting");
	}

	/** Open url "url". */
	public TestRigorSteps openUrl(String url) {
		return addStep("open url " + StepEscaping.quotedSegment(url));
	}

	public TestRigorSteps goBack() {
		return addStep("go back");
	}

	public TestRigorSteps goForward() {
		return addStep("go forward");
	}

	public TestRigorSteps reload() {
		return addStep("reload");
	}

	/** Wait N sec (testRigor max 2 min). */
	public TestRigorSteps waitSec(int seconds) {
		return addStep("wait " + seconds + " sec");
	}

	/** Wait 1 sec until page contains "text". */
	public TestRigorSteps waitUntilPageContains(String expectedText) {
		return addStep("wait 1 sec until page contains " + StepEscaping.quotedSegment(expectedText));
	}

	/** Wait 1 sec up to N times until page contains "text". */
	public TestRigorSteps waitUntilPageContains(String expectedText, int maxTimes) {
		return addStep("wait 1 sec up to " + maxTimes + " times until page contains " + StepEscaping.quotedSegment(expectedText));
	}

	/** Wait up to N seconds until page contains "text". */
	public TestRigorSteps waitUntilPageContainsWithinSeconds(String expectedText, int timeoutSeconds) {
		return addStep("wait up to " + timeoutSeconds + " seconds until page contains " + StepEscaping.quotedSegment(expectedText));
	}

	/** Wait up to N seconds until page contains "text" with waiting. */
	public TestRigorSteps waitUntilPageContainsWithinSecondsWithWaiting(String expectedText, int timeoutSeconds) {
		return addStep("wait up to " + timeoutSeconds + " seconds until page contains "
			+ StepEscaping.quotedSegment(expectedText) + " with waiting");
	}

	/** Drag "source" to "target". */
	public TestRigorSteps drag(String sourceDescription, String targetDescription) {
		return addStep("drag " + StepEscaping.quotedSegment(sourceDescription) + " to " + StepEscaping.quotedSegment(targetDescription));
	}

	/** Login (uses stored username/password). */
	public TestRigorSteps login() {
		return addStep("login");
	}

	/** Fill out form. */
	public TestRigorSteps fillOutForm() {
		return addStep("fill out form");
	}

	/** Fill out required fields in form. */
	public TestRigorSteps fillOutRequiredFieldsInForm() {
		return addStep("fill out required fields in form");
	}

	/** Type "text" (cursor already in field). */
	public TestRigorSteps type(String text) {
		return addStep("type " + StepEscaping.quotedSegment(text));
	}

	/** Type "text" into "fieldDescription". */
	public TestRigorSteps typeInto(String text, String fieldDescription) {
		return addStep("type " + StepEscaping.quotedSegment(text) + " into " + StepEscaping.quotedSegment(fieldDescription));
	}

	/** Enter "value" into "fieldDescription" until page contains "text". */
	public TestRigorSteps enterUntilPageContains(String value, String fieldDescription, String expectedText) {
		return addStep("enter " + StepEscaping.quotedSegment(value) + " into " + StepEscaping.quotedSegment(fieldDescription)
			+ " until page contains " + StepEscaping.quotedSegment(expectedText));
	}

	/** Enter stored value "varName" into "fieldDescription" until page contains "text". */
	public TestRigorSteps enterStoredValueUntilPageContains(String varName, String fieldDescription, String expectedText) {
		return addStep("enter stored value " + StepEscaping.quotedSegment(varName) + " into " + StepEscaping.quotedSegment(fieldDescription)
			+ " until page contains " + StepEscaping.quotedSegment(expectedText));
	}

	/** Enter stored value "varName" into "fieldDescription" until page contains stored value "conditionVarName". */
	public TestRigorSteps enterStoredValueUntilPageContainsStoredValue(String varName, String fieldDescription, String conditionVarName) {
		return addStep("enter stored value " + StepEscaping.quotedSegment(varName) + " into " + StepEscaping.quotedSegment(fieldDescription)
			+ " until page contains stored value " + StepEscaping.quotedSegment(conditionVarName));
	}

	/**
	 * Type a raw key or key combination (e.g. {@code enter}, {@code ctrl+a}, {@code win+r}).
	 * This is intentionally not quoted so testRigor parses it as keyboard input.
	 */
	public TestRigorSteps typeKey(String keyOrCombo) {
		return addStep("type " + rawToken(keyOrCombo));
	}

	/** Type a raw key/key-combo only if page contains specific text. */
	public TestRigorSteps typeKeyIfPageContains(String keyOrCombo, String expectedText) {
		return addStep("type " + rawToken(keyOrCombo) + " if page contains " + StepEscaping.quotedSegment(expectedText));
	}

	/** Type enter. */
	public TestRigorSteps typeEnter() {
		return addStep("type enter");
	}

	/** Type tab. */
	public TestRigorSteps typeTab() {
		return addStep("type tab");
	}

	/**
	 * Press a raw key or key combination (e.g. {@code enter}, {@code ctrl+a}, {@code shift+ctrl+alt+f1}).
	 * This is intentionally not quoted so testRigor parses it as keyboard input.
	 */
	public TestRigorSteps pressKey(String keyOrCombo) {
		return addStep("press " + rawToken(keyOrCombo));
	}

	/** Press a raw key/key-combo only if page contains specific text. */
	public TestRigorSteps pressKeyIfPageContains(String keyOrCombo, String expectedText) {
		return addStep("press " + rawToken(keyOrCombo) + " if page contains " + StepEscaping.quotedSegment(expectedText));
	}

	/** Enter a raw key or key combination into a target field. */
	public EnterKeyInto enterKey(String keyOrCombo) {
		return new EnterKeyInto(keyOrCombo, this);
	}

	/** Save value "value" as "varName". */
	public TestRigorSteps saveValue(String value, String varName) {
		return addStep("save value " + StepEscaping.quotedSegment(value) + " as " + StepEscaping.quotedSegment(varName));
	}

	/** Grab value from "elementDescription" and save as "varName". */
	public TestRigorSteps grabValueFrom(String elementDescription, String varName) {
		return addStep("grab value from " + StepEscaping.quotedSegment(elementDescription) + " and save it as " + StepEscaping.quotedSegment(varName));
	}

	/** Open new tab. */
	public TestRigorSteps openNewTab() {
		return addStep("open new tab");
	}

	/** Switch to tab N. */
	public TestRigorSteps switchToTab(int tabIndex) {
		return addStep("switch to tab " + tabIndex);
	}

	/** Switch to tab "name". */
	public TestRigorSteps switchToTab(String name) {
		return addStep("switch to tab " + StepEscaping.quotedSegment(name));
	}

	/** Close tab. */
	public TestRigorSteps closeTab() {
		return addStep("close tab");
	}

	/** Call api "url" and save as "varName". */
	public TestRigorSteps callApi(String url, String varName) {
		return addStep("call api " + StepEscaping.quotedSegment(url) + " and save it as " + StepEscaping.quotedSegment(varName));
	}

	/** Paste (from clipboard). */
	public TestRigorSteps paste() {
		return addStep("paste");
	}

	/** Accept prompt with value "value". */
	public TestRigorSteps acceptPromptWithValue(String value) {
		return addStep("accept prompt with value " + StepEscaping.quotedSegment(value));
	}

	/** Accept alert/prompt without value. */
	public TestRigorSteps acceptAlert() {
		return addStep("accept prompt");
	}

	/** Set geo location to a literal "lat,long". */
	public TestRigorSteps setGeoLocation(String latLong) {
		return addStep("set geo location " + StepEscaping.quotedSegment(latLong));
	}

	/** Set geo location from stored value "varName". */
	public TestRigorSteps setGeoLocationStoredValue(String varName) {
		return addStep("set geo location stored value " + StepEscaping.quotedSegment(varName));
	}

	/** Returns the full prompt string (one step per line). */
	public String build() {
		return String.join("\n", steps);
	}

	private static String ordinal(int position) {
		if (position < 1 || position > MAX_ORDINAL_INDEX) {
			return String.valueOf(position);
		}
		String[] ordinals = { "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th",
			"11th", "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th", "20th" };
		return ordinals[position - 1];
	}

	/** Fluent part for enter(value).into(field). */
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@RequiredArgsConstructor
	public static final class EnterInto {
		String value;
		TestRigorSteps parent;

		public TestRigorSteps into(String fieldDescription) {
			return parent.addStep("enter " + StepEscaping.quotedSegment(value) + " into " + StepEscaping.quotedSegment(fieldDescription));
		}
	}

	/** Fluent part for enterStoredValue(varName).into(field). */
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@RequiredArgsConstructor
	public static final class EnterStoredInto {
		String varName;
		TestRigorSteps parent;

		public TestRigorSteps into(String fieldDescription) {
			return parent.addStep("enter stored value " + StepEscaping.quotedSegment(varName) + " into " + StepEscaping.quotedSegment(fieldDescription));
		}
	}

	/** Fluent part for enterKey(keyOrCombo).into(field). */
	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@RequiredArgsConstructor
	public static final class EnterKeyInto {
		String keyOrCombo;
		TestRigorSteps parent;

		public TestRigorSteps into(String fieldDescription) {
			return parent.addStep("enter " + rawToken(keyOrCombo) + " into " + StepEscaping.quotedSegment(fieldDescription));
		}
	}

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@RequiredArgsConstructor
	public static final class EnterTargetInto {
		String value;
		TestRigorSteps parent;

		public EnterTarget into(String fieldDescription) {
			return new EnterTarget(parent, value, fieldDescription, null, null, null, null, null, null);
		}
	}

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class ClickTarget {
		TestRigorSteps parent;
		String elementDescription;
		String tableDescription;
		String rowContaining;
		String columnDescription;
		String contextDescription;
		Anchor belowAnchor;
		Anchor rightAnchor;

		public ClickTarget withinTable(String tableDescription) {
			return new ClickTarget(parent, elementDescription, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		}

		public ClickTarget rowContaining(String rowContaining) {
			return new ClickTarget(parent, elementDescription, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		}

		public ClickTarget column(String columnDescription) {
			return new ClickTarget(parent, elementDescription, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		}

		public ClickTarget inContext(String contextDescription) {
			return new ClickTarget(parent, elementDescription, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		}

		public ClickTarget below(String anchorDescription) {
			return new ClickTarget(parent, elementDescription, tableDescription, rowContaining, columnDescription, contextDescription,
				Anchor.below(anchorDescription), rightAnchor);
		}

		public ClickTarget roughlyBelow(String anchorDescription) {
			return new ClickTarget(parent, elementDescription, tableDescription, rowContaining, columnDescription, contextDescription,
				Anchor.roughlyBelow(anchorDescription), rightAnchor);
		}

		public ClickTarget completelyBelow(String anchorDescription) {
			return new ClickTarget(parent, elementDescription, tableDescription, rowContaining, columnDescription, contextDescription,
				Anchor.completelyBelow(anchorDescription), rightAnchor);
		}

		public ClickTarget rightOf(String anchorDescription) {
			return new ClickTarget(parent, elementDescription, tableDescription, rowContaining, columnDescription, contextDescription,
				belowAnchor, Anchor.rightOf(anchorDescription));
		}

		/** Readability no-op for fluent chaining. */
		public ClickTarget and() {
			return this;
		}

		public TestRigorSteps add() {
			return parent.addStep(renderClickLine(elementDescription, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor));
		}
	}

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class EnterTarget {
		TestRigorSteps parent;
		String value;
		String fieldDescription;
		String tableDescription;
		String rowContaining;
		String columnDescription;
		String contextDescription;
		Anchor belowAnchor;
		Anchor rightAnchor;

		public EnterTarget withinTable(String tableDescription) {
			return new EnterTarget(parent, value, fieldDescription, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		}

		public EnterTarget rowContaining(String rowContaining) {
			return new EnterTarget(parent, value, fieldDescription, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		}

		public EnterTarget column(String columnDescription) {
			return new EnterTarget(parent, value, fieldDescription, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		}

		public EnterTarget inContext(String contextDescription) {
			return new EnterTarget(parent, value, fieldDescription, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		}

		public EnterTarget below(String anchorDescription) {
			return new EnterTarget(parent, value, fieldDescription, tableDescription, rowContaining, columnDescription, contextDescription,
				Anchor.below(anchorDescription), rightAnchor);
		}

		public EnterTarget roughlyBelow(String anchorDescription) {
			return new EnterTarget(parent, value, fieldDescription, tableDescription, rowContaining, columnDescription, contextDescription,
				Anchor.roughlyBelow(anchorDescription), rightAnchor);
		}

		public EnterTarget completelyBelow(String anchorDescription) {
			return new EnterTarget(parent, value, fieldDescription, tableDescription, rowContaining, columnDescription, contextDescription,
				Anchor.completelyBelow(anchorDescription), rightAnchor);
		}

		public EnterTarget rightOf(String anchorDescription) {
			return new EnterTarget(parent, value, fieldDescription, tableDescription, rowContaining, columnDescription, contextDescription,
				belowAnchor, Anchor.rightOf(anchorDescription));
		}

		/** Readability no-op for fluent chaining. */
		public EnterTarget and() {
			return this;
		}

		public TestRigorSteps add() {
			return parent.addStep(renderEnterLine(value, fieldDescription, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor));
		}
	}

	private static String renderClickLine(String elementDescription, String tableDescription, String rowContaining, String columnDescription,
			String contextDescription, Anchor belowAnchor, Anchor rightAnchor) {
		StringBuilder line = new StringBuilder("click on ").append(StepEscaping.quotedSegment(elementDescription));
		appendContextAndAnchors(line, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		return line.toString();
	}

	private static String renderEnterLine(String value, String fieldDescription, String tableDescription, String rowContaining, String columnDescription,
			String contextDescription, Anchor belowAnchor, Anchor rightAnchor) {
		StringBuilder line = new StringBuilder("enter ")
			.append(StepEscaping.quotedSegment(value))
			.append(" into ")
			.append(StepEscaping.quotedSegment(fieldDescription));
		appendContextAndAnchors(line, tableDescription, rowContaining, columnDescription, contextDescription, belowAnchor, rightAnchor);
		return line.toString();
	}

	private TestRigorSteps clickTimesLine(String clickPrefix, String elementDescription, int times) {
		return addStep(clickPrefix + " " + StepEscaping.quotedSegment(elementDescription) + " " + times + " times");
	}

	private static void appendContextAndAnchors(StringBuilder line, String tableDescription, String rowContaining, String columnDescription,
			String contextDescription, Anchor belowAnchor, Anchor rightAnchor) {
		if (tableDescription != null) {
			line.append(" within the context of table ").append(StepEscaping.quotedSegment(tableDescription));
			if (rowContaining != null) {
				line.append(" at row containing ").append(StepEscaping.quotedSegment(rowContaining));
			}
			if (columnDescription != null) {
				if (rowContaining != null) {
					line.append(" and");
				} else {
					line.append(" at");
				}
				line.append(" column ").append(StepEscaping.quotedSegment(columnDescription));
			}
		} else if (contextDescription != null) {
			line.append(" within the context of ").append(StepEscaping.quotedSegment(contextDescription));
		}

		if (belowAnchor != null) {
			line.append(" ").append(belowAnchor.render());
		}
		if (rightAnchor != null) {
			line.append(belowAnchor == null ? " " : " and ").append(rightAnchor.render());
		}
	}

	private static String rawToken(String value) {
		return value == null ? "" : value.trim();
	}

	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	private static final class Anchor {
		String relationPrefix;
		String anchorDescription;

		static Anchor below(String anchorDescription) {
			return new Anchor("below ", anchorDescription);
		}

		static Anchor roughlyBelow(String anchorDescription) {
			return new Anchor("roughly below ", anchorDescription);
		}

		static Anchor completelyBelow(String anchorDescription) {
			return new Anchor("completely below ", anchorDescription);
		}

		static Anchor rightOf(String anchorDescription) {
			return new Anchor("to the right of ", anchorDescription);
		}

		String render() {
			return relationPrefix + StepEscaping.quotedSegment(anchorDescription);
		}
	}
}
