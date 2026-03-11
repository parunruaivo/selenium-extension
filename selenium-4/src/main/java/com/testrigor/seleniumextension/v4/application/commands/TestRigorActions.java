package com.testrigor.seleniumextension.v4.application.commands;

import com.testrigor.seleniumextension.commons.application.commands.TestRigorSteps;
import com.testrigor.seleniumextension.v4.application.TestrigorDriver;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Fluent facade for execute-only testRigor actions.
 * <p>
 * Any method parameter describing an element follows testRigor's referencing syntax:
 * https://testrigor.com/docs/language#referencing
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessivePublicCount" })
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TestRigorActions {

	TestrigorDriver driver;
	TestRigorSteps steps;

	public static TestRigorActions actions(TestrigorDriver driver) {
		return new TestRigorActions(driver, TestRigorSteps.create());
	}

	public static TestRigorActions with(TestrigorDriver driver) {
		return actions(driver);
	}

	private TestRigorActions withSteps(TestRigorSteps next) {
		return new TestRigorActions(driver, next);
	}

	/** Readability no-op for fluent chaining between commands. */
	public TestRigorActions and() {
		return this;
	}

	public TestRigorActions click(String elementDescription) {
		return withSteps(steps.click(elementDescription));
	}

	public TestRigorActions tap(String elementDescription) {
		return withSteps(steps.tap(elementDescription));
	}

	public TestRigorActions press(String elementDescription) {
		return withSteps(steps.press(elementDescription));
	}

	public TestRigorActions push(String elementDescription) {
		return withSteps(steps.push(elementDescription));
	}

	public TestRigorActions follow(String elementDescription) {
		return withSteps(steps.follow(elementDescription));
	}

	/** Fluent contextual click builder. Finalize with {@link ContextualClick#add()}. */
	public ContextualClick clickOn(String elementDescription) {
		return new ContextualClick(steps.clickOn(elementDescription));
	}

	public TestRigorActions click(int nth, String elementDescription) {
		return withSteps(steps.click(nth, elementDescription));
	}

	public TestRigorActions doubleClick(String elementDescription) {
		return withSteps(steps.doubleClick(elementDescription));
	}

	public TestRigorActions rightClick(String elementDescription) {
		return withSteps(steps.rightClick(elementDescription));
	}

	public TestRigorActions longClick(String elementDescription) {
		return withSteps(steps.longClick(elementDescription));
	}

	public TestRigorActions tripleClick(String elementDescription) {
		return withSteps(steps.tripleClick(elementDescription));
	}

	public TestRigorActions middleClick(String elementDescription) {
		return withSteps(steps.middleClick(elementDescription));
	}

	public TestRigorActions wheelClick(String elementDescription) {
		return withSteps(steps.wheelClick(elementDescription));
	}

	public TestRigorActions clickTimes(String elementDescription, int times) {
		return withSteps(steps.clickTimes(elementDescription, times));
	}

	public TestRigorActions doubleClickTimes(String elementDescription, int times) {
		return withSteps(steps.doubleClickTimes(elementDescription, times));
	}

	public TestRigorActions tripleClickTimes(String elementDescription, int times) {
		return withSteps(steps.tripleClickTimes(elementDescription, times));
	}

	public TestRigorActions rightClickTimes(String elementDescription, int times) {
		return withSteps(steps.rightClickTimes(elementDescription, times));
	}

	public TestRigorActions middleClickTimes(String elementDescription, int times) {
		return withSteps(steps.middleClickTimes(elementDescription, times));
	}

	public TestRigorActions wheelClickTimes(String elementDescription, int times) {
		return withSteps(steps.wheelClickTimes(elementDescription, times));
	}

	public TestRigorActions clickIfExists(String elementDescription) {
		return withSteps(steps.clickIfExists(elementDescription));
	}

	public TestRigorActions clickIfExistsWithWaiting(String elementDescription) {
		return withSteps(steps.clickIfExistsWithWaiting(elementDescription));
	}

	public TestRigorActions clickIfExistsWithoutWaiting(String elementDescription) {
		return withSteps(steps.clickIfExistsWithoutWaiting(elementDescription));
	}

	public TestRigorActions clickAndSwitchToNewTab(String elementDescription) {
		return withSteps(steps.clickAndSwitchToNewTab(elementDescription));
	}

	public TestRigorActions clickIfExistsAndSwitchToNewTab(String elementDescription) {
		return withSteps(steps.clickIfExistsAndSwitchToNewTab(elementDescription));
	}

	public TestRigorActions clickIfExistsWithWaitingAndSwitchToNewTab(String elementDescription) {
		return withSteps(steps.clickIfExistsWithWaitingAndSwitchToNewTab(elementDescription));
	}

	public TestRigorActions clickIfPageContains(String elementDescription, String expectedText) {
		return withSteps(steps.clickIfPageContains(elementDescription, expectedText));
	}

	public TestRigorActions clickIfPageContainsWithWaiting(String elementDescription, String expectedText) {
		return withSteps(steps.clickIfPageContainsWithWaiting(elementDescription, expectedText));
	}

	public TestRigorActions clickIfPageContainsWithoutWaiting(String elementDescription, String expectedText) {
		return withSteps(steps.clickIfPageContainsWithoutWaiting(elementDescription, expectedText));
	}

	public TestRigorActions clickIfPageDoesNotContain(String elementDescription, String expectedText) {
		return withSteps(steps.clickIfPageDoesNotContain(elementDescription, expectedText));
	}

	public TestRigorActions clickIfUrlContains(String elementDescription, String expectedText) {
		return withSteps(steps.clickIfUrlContains(elementDescription, expectedText));
	}

	public TestRigorActions pressIfPageContains(String elementDescription, String expectedText) {
		return withSteps(steps.pressIfPageContains(elementDescription, expectedText));
	}

	public EnterInto enter(String value) {
		return new EnterInto(value);
	}

	/** Fluent contextual enter builder. */
	public EnterFluent enterFluent(String value) {
		return new EnterFluent(value);
	}

	public EnterStoredInto enterStoredValue(String varName) {
		return new EnterStoredInto(varName);
	}

	public EnterKeyInto enterKey(String keyOrCombo) {
		return new EnterKeyInto(keyOrCombo);
	}

	public TestRigorActions select(String value, String dropdownDescription) {
		return withSteps(steps.select(value, dropdownDescription));
	}

	public TestRigorActions choose(String value, String dropdownDescription) {
		return withSteps(steps.choose(value, dropdownDescription));
	}

	public TestRigorActions insert(String value, String fieldDescription) {
		return withSteps(steps.insert(value, fieldDescription));
	}

	public TestRigorActions selectNthOption(int nthOption, String dropdownDescription) {
		return withSteps(steps.selectNthOption(nthOption, dropdownDescription));
	}

	public TestRigorActions selectOption(int optionNumber, String dropdownDescription) {
		return withSteps(steps.selectOption(optionNumber, dropdownDescription));
	}

	public TestRigorActions hoverOver(String elementDescription) {
		return withSteps(steps.hoverOver(elementDescription));
	}

	public TestRigorActions hoverOver(int nth, String elementDescription) {
		return withSteps(steps.hoverOver(nth, elementDescription));
	}

	public TestRigorActions scrollDown() {
		return withSteps(steps.scrollDown());
	}

	public TestRigorActions scrollUp() {
		return withSteps(steps.scrollUp());
	}

	public TestRigorActions scrollLeft() {
		return withSteps(steps.scrollLeft());
	}

	public TestRigorActions scrollRight() {
		return withSteps(steps.scrollRight());
	}

	public TestRigorActions scrollDownOn(String elementDescription) {
		return withSteps(steps.scrollDownOn(elementDescription));
	}

	public TestRigorActions scrollUpOn(String elementDescription) {
		return withSteps(steps.scrollUpOn(elementDescription));
	}

	public TestRigorActions scrollDownUntilPageContains(String text) {
		return withSteps(steps.scrollDownUntilPageContains(text));
	}

	public TestRigorActions scrollDownUntilPageContains(String text, int maxTimes) {
		return withSteps(steps.scrollDownUntilPageContains(text, maxTimes));
	}

	public TestRigorActions clickUntilPageContains(String elementDescription, String expectedText) {
		return withSteps(steps.clickUntilPageContains(elementDescription, expectedText));
	}

	public TestRigorActions clickUntilPageContainsStoredValue(String elementDescription, String varName) {
		return withSteps(steps.clickUntilPageContainsStoredValue(elementDescription, varName));
	}

	public TestRigorActions clickUntilPageContains(String elementDescription, String expectedText, int maxTimes) {
		return withSteps(steps.clickUntilPageContains(elementDescription, expectedText, maxTimes));
	}

	public TestRigorActions clickUntilPageContainsWithWaiting(String elementDescription, String expectedText) {
		return withSteps(steps.clickUntilPageContainsWithWaiting(elementDescription, expectedText));
	}

	public TestRigorActions clickUntilPageContainsWithoutWaiting(String elementDescription, String expectedText) {
		return withSteps(steps.clickUntilPageContainsWithoutWaiting(elementDescription, expectedText));
	}

	public TestRigorActions clickUntilPageContainsWithWaiting(String elementDescription, String expectedText, int maxTimes) {
		return withSteps(steps.clickUntilPageContainsWithWaiting(elementDescription, expectedText, maxTimes));
	}

	public TestRigorActions openUrl(String url) {
		return withSteps(steps.openUrl(url));
	}

	public TestRigorActions goBack() {
		return withSteps(steps.goBack());
	}

	public TestRigorActions goForward() {
		return withSteps(steps.goForward());
	}

	public TestRigorActions reload() {
		return withSteps(steps.reload());
	}

	public TestRigorActions waitSec(int seconds) {
		return withSteps(steps.waitSec(seconds));
	}

	public TestRigorActions waitUntilPageContains(String expectedText) {
		return withSteps(steps.waitUntilPageContains(expectedText));
	}

	public TestRigorActions waitUntilPageContains(String expectedText, int maxTimes) {
		return withSteps(steps.waitUntilPageContains(expectedText, maxTimes));
	}

	public TestRigorActions waitUntilPageContainsWithinSeconds(String expectedText, int timeoutSeconds) {
		return withSteps(steps.waitUntilPageContainsWithinSeconds(expectedText, timeoutSeconds));
	}

	public TestRigorActions waitUntilPageContainsWithinSecondsWithWaiting(String expectedText, int timeoutSeconds) {
		return withSteps(steps.waitUntilPageContainsWithinSecondsWithWaiting(expectedText, timeoutSeconds));
	}

	public TestRigorActions drag(String sourceDescription, String targetDescription) {
		return withSteps(steps.drag(sourceDescription, targetDescription));
	}

	public TestRigorActions login() {
		return withSteps(steps.login());
	}

	public TestRigorActions fillOutForm() {
		return withSteps(steps.fillOutForm());
	}

	public TestRigorActions fillOutRequiredFieldsInForm() {
		return withSteps(steps.fillOutRequiredFieldsInForm());
	}

	public TestRigorActions type(String text) {
		return withSteps(steps.type(text));
	}

	public TestRigorActions typeInto(String text, String fieldDescription) {
		return withSteps(steps.typeInto(text, fieldDescription));
	}

	public TestRigorActions enterUntilPageContains(String value, String fieldDescription, String expectedText) {
		return withSteps(steps.enterUntilPageContains(value, fieldDescription, expectedText));
	}

	public TestRigorActions enterStoredValueUntilPageContains(String varName, String fieldDescription, String expectedText) {
		return withSteps(steps.enterStoredValueUntilPageContains(varName, fieldDescription, expectedText));
	}

	public TestRigorActions enterStoredValueUntilPageContainsStoredValue(String varName, String fieldDescription, String conditionVarName) {
		return withSteps(steps.enterStoredValueUntilPageContainsStoredValue(varName, fieldDescription, conditionVarName));
	}

	public TestRigorActions typeKey(String keyOrCombo) {
		return withSteps(steps.typeKey(keyOrCombo));
	}

	public TestRigorActions typeKeyIfPageContains(String keyOrCombo, String expectedText) {
		return withSteps(steps.typeKeyIfPageContains(keyOrCombo, expectedText));
	}

	public TestRigorActions typeEnter() {
		return withSteps(steps.typeEnter());
	}

	public TestRigorActions typeTab() {
		return withSteps(steps.typeTab());
	}

	public TestRigorActions pressKey(String keyOrCombo) {
		return withSteps(steps.pressKey(keyOrCombo));
	}

	public TestRigorActions pressKeyIfPageContains(String keyOrCombo, String expectedText) {
		return withSteps(steps.pressKeyIfPageContains(keyOrCombo, expectedText));
	}

	public TestRigorActions saveValue(String value, String varName) {
		return withSteps(steps.saveValue(value, varName));
	}

	/** Stores a grabbed value into a server-side variable in the same prompt flow. */
	public TestRigorActions grabValueFrom(String elementDescription, String varName) {
		return withSteps(steps.grabValueFrom(elementDescription, varName));
	}

	public TestRigorActions openNewTab() {
		return withSteps(steps.openNewTab());
	}

	public TestRigorActions switchToTab(int tabIndex) {
		return withSteps(steps.switchToTab(tabIndex));
	}

	public TestRigorActions switchToTab(String name) {
		return withSteps(steps.switchToTab(name));
	}

	public TestRigorActions closeTab() {
		return withSteps(steps.closeTab());
	}

	public TestRigorActions callApi(String url, String varName) {
		return withSteps(steps.callApi(url, varName));
	}

	public TestRigorActions paste() {
		return withSteps(steps.paste());
	}

	public TestRigorActions acceptPromptWithValue(String value) {
		return withSteps(steps.acceptPromptWithValue(value));
	}

	public TestRigorActions acceptAlert() {
		return withSteps(steps.acceptAlert());
	}

	public TestRigorActions setGeoLocation(String latLong) {
		return withSteps(steps.setGeoLocation(latLong));
	}

	public TestRigorActions setGeoLocationStoredValue(String varName) {
		return withSteps(steps.setGeoLocationStoredValue(varName));
	}

	public void execute() {
		driver.executePrompt(steps.build());
	}

	public String buildPrompt() {
		return steps.build();
	}

	@RequiredArgsConstructor
	public final class EnterInto {
		final String value;

		public TestRigorActions into(String fieldDescription) {
			return withSteps(steps.enter(value).into(fieldDescription));
		}
	}

	@RequiredArgsConstructor
	public final class EnterFluent {
		final String value;

		public ContextualEnter into(String fieldDescription) {
			return new ContextualEnter(steps.enterFluent(value).into(fieldDescription));
		}
	}

	@RequiredArgsConstructor
	public final class EnterStoredInto {
		final String varName;

		public TestRigorActions into(String fieldDescription) {
			return withSteps(steps.enterStoredValue(varName).into(fieldDescription));
		}
	}

	@RequiredArgsConstructor
	public final class EnterKeyInto {
		final String keyOrCombo;

		public TestRigorActions into(String fieldDescription) {
			return withSteps(steps.enterKey(keyOrCombo).into(fieldDescription));
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public final class ContextualClick {
		final TestRigorSteps.ClickTarget clickTarget;

		public ContextualClick withinTable(String tableDescription) {
			return new ContextualClick(clickTarget.withinTable(tableDescription));
		}

		public ContextualClick rowContaining(String rowContaining) {
			return new ContextualClick(clickTarget.rowContaining(rowContaining));
		}

		public ContextualClick column(String columnDescription) {
			return new ContextualClick(clickTarget.column(columnDescription));
		}

		public ContextualClick inContext(String contextDescription) {
			return new ContextualClick(clickTarget.inContext(contextDescription));
		}

		public ContextualClick below(String anchorDescription) {
			return new ContextualClick(clickTarget.below(anchorDescription));
		}

		public ContextualClick roughlyBelow(String anchorDescription) {
			return new ContextualClick(clickTarget.roughlyBelow(anchorDescription));
		}

		public ContextualClick completelyBelow(String anchorDescription) {
			return new ContextualClick(clickTarget.completelyBelow(anchorDescription));
		}

		public ContextualClick rightOf(String anchorDescription) {
			return new ContextualClick(clickTarget.rightOf(anchorDescription));
		}

		/** Readability no-op for fluent chaining. */
		public ContextualClick and() {
			return this;
		}

		public TestRigorActions add() {
			return withSteps(clickTarget.add());
		}
	}

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public final class ContextualEnter {
		final TestRigorSteps.EnterTarget enterTarget;

		public ContextualEnter withinTable(String tableDescription) {
			return new ContextualEnter(enterTarget.withinTable(tableDescription));
		}

		public ContextualEnter rowContaining(String rowContaining) {
			return new ContextualEnter(enterTarget.rowContaining(rowContaining));
		}

		public ContextualEnter column(String columnDescription) {
			return new ContextualEnter(enterTarget.column(columnDescription));
		}

		public ContextualEnter inContext(String contextDescription) {
			return new ContextualEnter(enterTarget.inContext(contextDescription));
		}

		public ContextualEnter below(String anchorDescription) {
			return new ContextualEnter(enterTarget.below(anchorDescription));
		}

		public ContextualEnter roughlyBelow(String anchorDescription) {
			return new ContextualEnter(enterTarget.roughlyBelow(anchorDescription));
		}

		public ContextualEnter completelyBelow(String anchorDescription) {
			return new ContextualEnter(enterTarget.completelyBelow(anchorDescription));
		}

		public ContextualEnter rightOf(String anchorDescription) {
			return new ContextualEnter(enterTarget.rightOf(anchorDescription));
		}

		/** Readability no-op for fluent chaining. */
		public ContextualEnter and() {
			return this;
		}

		public TestRigorActions add() {
			return withSteps(enterTarget.add());
		}
	}
}
