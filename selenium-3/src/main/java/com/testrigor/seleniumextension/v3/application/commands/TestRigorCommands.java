package com.testrigor.seleniumextension.v3.application.commands;

import com.testrigor.seleniumextension.commons.application.commands.TestRigorSteps;
import com.testrigor.seleniumextension.v3.application.TestrigorDriver;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Fluent facade to build testRigor command steps and execute them via {@link TestrigorDriver#executePrompt(String)}.
 * Delegates to {@link TestRigorSteps}; use {@link #execute()} to run the prompt or {@link #buildPrompt()} to get the string.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessivePublicCount" })
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TestRigorCommands {

	TestrigorDriver driver;
	TestRigorSteps steps;

	public static TestRigorCommands with(TestrigorDriver driver) {
		return new TestRigorCommands(driver, TestRigorSteps.create());
	}

	private TestRigorCommands withSteps(TestRigorSteps next) {
		return new TestRigorCommands(driver, next);
	}

	public TestRigorCommands click(String elementDescription) {
		return withSteps(steps.click(elementDescription));
	}

	public TestRigorCommands click(int nth, String elementDescription) {
		return withSteps(steps.click(nth, elementDescription));
	}

	public TestRigorCommands doubleClick(String elementDescription) {
		return withSteps(steps.doubleClick(elementDescription));
	}

	public TestRigorCommands rightClick(String elementDescription) {
		return withSteps(steps.rightClick(elementDescription));
	}

	public TestRigorCommands longClick(String elementDescription) {
		return withSteps(steps.longClick(elementDescription));
	}

	public EnterInto enter(String value) {
		return new EnterInto(value);
	}

	public EnterStoredInto enterStoredValue(String varName) {
		return new EnterStoredInto(varName);
	}

	public TestRigorCommands select(String value, String dropdownDescription) {
		return withSteps(steps.select(value, dropdownDescription));
	}

	public TestRigorCommands selectNthOption(int nthOption, String dropdownDescription) {
		return withSteps(steps.selectNthOption(nthOption, dropdownDescription));
	}

	public TestRigorCommands checkPageContains(String text) {
		return withSteps(steps.checkPageContains(text));
	}

	public TestRigorCommands checkPageContainsStoredValue(String varName) {
		return withSteps(steps.checkPageContainsStoredValue(varName));
	}

	public TestRigorCommands checkPageDidNotChange() {
		return withSteps(steps.checkPageDidNotChange());
	}

	public TestRigorCommands checkButtonDisabled(String buttonDescription) {
		return withSteps(steps.checkButtonDisabled(buttonDescription));
	}

	public TestRigorCommands checkButtonEnabled(String buttonDescription) {
		return withSteps(steps.checkButtonEnabled(buttonDescription));
	}

	public TestRigorCommands checkThatElementContains(String elementDescription, String text) {
		return withSteps(steps.checkThatElementContains(elementDescription, text));
	}

	public TestRigorCommands checkThatElementContainsStoredValue(String elementDescription, String varName) {
		return withSteps(steps.checkThatElementContainsStoredValue(elementDescription, varName));
	}

	public TestRigorCommands hoverOver(String elementDescription) {
		return withSteps(steps.hoverOver(elementDescription));
	}

	public TestRigorCommands hoverOver(int nth, String elementDescription) {
		return withSteps(steps.hoverOver(nth, elementDescription));
	}

	public TestRigorCommands scrollDown() {
		return withSteps(steps.scrollDown());
	}

	public TestRigorCommands scrollUp() {
		return withSteps(steps.scrollUp());
	}

	public TestRigorCommands scrollDownOn(String elementDescription) {
		return withSteps(steps.scrollDownOn(elementDescription));
	}

	public TestRigorCommands scrollDownUntilPageContains(String text) {
		return withSteps(steps.scrollDownUntilPageContains(text));
	}

	public TestRigorCommands openUrl(String url) {
		return withSteps(steps.openUrl(url));
	}

	public TestRigorCommands goBack() {
		return withSteps(steps.goBack());
	}

	public TestRigorCommands goForward() {
		return withSteps(steps.goForward());
	}

	public TestRigorCommands reload() {
		return withSteps(steps.reload());
	}

	public TestRigorCommands waitSec(int seconds) {
		return withSteps(steps.waitSec(seconds));
	}

	public TestRigorCommands drag(String sourceDescription, String targetDescription) {
		return withSteps(steps.drag(sourceDescription, targetDescription));
	}

	public TestRigorCommands login() {
		return withSteps(steps.login());
	}

	public TestRigorCommands fillOutForm() {
		return withSteps(steps.fillOutForm());
	}

	public TestRigorCommands fillOutRequiredFieldsInForm() {
		return withSteps(steps.fillOutRequiredFieldsInForm());
	}

	public TestRigorCommands type(String text) {
		return withSteps(steps.type(text));
	}

	public TestRigorCommands typeEnter() {
		return withSteps(steps.typeEnter());
	}

	public TestRigorCommands typeTab() {
		return withSteps(steps.typeTab());
	}

	public TestRigorCommands saveValue(String value, String varName) {
		return withSteps(steps.saveValue(value, varName));
	}

	public TestRigorCommands grabValueFrom(String elementDescription, String varName) {
		return withSteps(steps.grabValueFrom(elementDescription, varName));
	}

	public TestRigorCommands openNewTab() {
		return withSteps(steps.openNewTab());
	}

	public TestRigorCommands switchToTab(int tabIndex) {
		return withSteps(steps.switchToTab(tabIndex));
	}

	public TestRigorCommands switchToTab(String name) {
		return withSteps(steps.switchToTab(name));
	}

	public TestRigorCommands closeTab() {
		return withSteps(steps.closeTab());
	}

	public TestRigorCommands callApi(String url, String varName) {
		return withSteps(steps.callApi(url, varName));
	}

	public TestRigorCommands paste() {
		return withSteps(steps.paste());
	}

	public TestRigorCommands acceptPromptWithValue(String value) {
		return withSteps(steps.acceptPromptWithValue(value));
	}

	/** Runs the built prompt via the driver. */
	public void execute() {
		driver.executePrompt(steps.build());
	}

	/** Returns the built multi-line prompt string without executing. */
	public String buildPrompt() {
		return steps.build();
	}

	@RequiredArgsConstructor
	public final class EnterInto {
		final String value;

		public TestRigorCommands into(String fieldDescription) {
			return withSteps(steps.enter(value).into(fieldDescription));
		}
	}

	@RequiredArgsConstructor
	public final class EnterStoredInto {
		final String varName;

		public TestRigorCommands into(String fieldDescription) {
			return withSteps(steps.enterStoredValue(varName).into(fieldDescription));
		}
	}
}
