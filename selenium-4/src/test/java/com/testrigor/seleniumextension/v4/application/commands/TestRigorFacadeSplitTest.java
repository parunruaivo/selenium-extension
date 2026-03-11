package com.testrigor.seleniumextension.v4.application.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.testrigor.seleniumextension.v4.application.TestrigorDriver;

import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class TestRigorFacadeSplitTest {

	@Test
	public void actions_buildPrompt_and_execute_use_executePrompt() {
		TestrigorDriver driver = mock(TestrigorDriver.class);

		TestRigorActions actions = TestRigorActions.actions(driver)
			.click("Login")
			.enter("john@example.com").into("Email")
			.typeEnter();

		String expected = String.join("\n",
			"click \"Login\"",
			"enter \"john@example.com\" into \"Email\"",
			"type enter");

		assertThat(actions.buildPrompt()).isEqualTo(expected);

		actions.execute();

		verify(driver).executePrompt(expected);
	}

	@Test
	public void actions_contextual_reference_builds_expected_prompt() {
		TestrigorDriver driver = mock(TestrigorDriver.class);

		String prompt = TestRigorActions.actions(driver)
			.clickOn("Delete")
			.inContext("sectionTwo")
			.completelyBelow("Actions")
			.and()
			.rightOf("rowName")
			.add()
			.buildPrompt();

		assertThat(prompt).isEqualTo(
			"click on \"Delete\" within the context of \"sectionTwo\" completely below \"Actions\" and to the right of \"rowName\"");
	}

	@Test
	public void actions_keyboard_combinations_build_expected_prompt_lines() {
		TestrigorDriver driver = mock(TestrigorDriver.class);

		String prompt = TestRigorActions.actions(driver)
			.typeKey("win+r")
			.pressKey("ctrl+a")
			.enterKey("shift+ctrl+alt+f1").into("Editor")
			.buildPrompt();

		String expected = String.join("\n",
			"type win+r",
			"press ctrl+a",
			"enter shift+ctrl+alt+f1 into \"Editor\"");
		assertThat(prompt).isEqualTo(expected);
	}

	@Test
	public void actions_enter_family_examples_build_requested_lines() {
		TestrigorDriver driver = mock(TestrigorDriver.class);

		String prompt = TestRigorActions.actions(driver)
			.typeEnter()
			.typeTab()
			.typeKey("backspace")
			.typeKey("arrow down")
			.typeKey("arrow up")
			.typeKey("left arrow")
			.typeKey("f1")
			.typeKey("ctrl+a")
			.typeKey("control+alt+f1")
			.typeKey("win+r")
			.typeKey("command+shift+c")
			.typeKey("alt+arrow down++")
			.pressKey("enter")
			.pressKey("tab")
			.pressKey("backspace")
			.pressKey("arrow down")
			.pressKey("f1")
			.pressKey("ctrl+a")
			.pressKey("shift+ctrl+alt+f1")
			.enterKey("enter").into("Search")
			.enterKey("tab").into("Search")
			.enterKey("backspace").into("Search")
			.enterKey("ctrl+a").into("Editor")
			.enterKey("win+r").into("Input")
			.typeKeyIfPageContains("enter", "Welcome")
			.pressKeyIfPageContains("ctrl+a", "text field")
			.enter("john").into("Username")
			.typeInto("john", "Username")
			.insert("john", "Username")
			.select("john", "User dropdown")
			.choose("john", "User dropdown")
			.selectNthOption(1, "Country")
			.selectOption(10, "Country")
			.buildPrompt();

		assertThat(prompt).isEqualTo(String.join("\n",
			"type enter",
			"type tab",
			"type backspace",
			"type arrow down",
			"type arrow up",
			"type left arrow",
			"type f1",
			"type ctrl+a",
			"type control+alt+f1",
			"type win+r",
			"type command+shift+c",
			"type alt+arrow down++",
			"press enter",
			"press tab",
			"press backspace",
			"press arrow down",
			"press f1",
			"press ctrl+a",
			"press shift+ctrl+alt+f1",
			"enter enter into \"Search\"",
			"enter tab into \"Search\"",
			"enter backspace into \"Search\"",
			"enter ctrl+a into \"Editor\"",
			"enter win+r into \"Input\"",
			"type enter if page contains \"Welcome\"",
			"press ctrl+a if page contains \"text field\"",
			"enter \"john\" into \"Username\"",
			"type \"john\" into \"Username\"",
			"insert \"john\" into \"Username\"",
			"select \"john\" from \"User dropdown\"",
			"choose \"john\" from \"User dropdown\"",
			"select 1st option from \"Country\"",
			"select option 10 from \"Country\""));
	}

	@Test
	public void actions_click_family_examples_build_requested_lines() {
		TestrigorDriver driver = mock(TestrigorDriver.class);

		String prompt = TestRigorActions.actions(driver)
			.tap("Primary Button")
			.press("Secondary Button")
			.push("CTA")
			.follow("Checkout")
			.tripleClick("Menu")
			.middleClick("Menu")
			.wheelClick("Menu")
			.clickTimes("Delete", 5)
			.doubleClickTimes("Delete", 2)
			.tripleClickTimes("Delete", 3)
			.rightClickTimes("Delete", 4)
			.middleClickTimes("Delete", 2)
			.wheelClickTimes("Delete", 2)
			.clickIfExists("Delete")
			.clickIfExistsWithWaiting("Delete")
			.clickIfExistsWithoutWaiting("Delete")
			.clickAndSwitchToNewTab("Delete")
			.clickIfExistsAndSwitchToNewTab("Delete")
			.clickIfExistsWithWaitingAndSwitchToNewTab("Delete")
			.clickIfPageContains("Delete", "Ready")
			.pressIfPageContains("Delete", "Ready")
			.buildPrompt();

		assertThat(prompt).isEqualTo(String.join("\n",
			"tap on \"Primary Button\"",
			"press \"Secondary Button\"",
			"push \"CTA\"",
			"follow \"Checkout\"",
			"triple click \"Menu\"",
			"middle click \"Menu\"",
			"wheel click \"Menu\"",
			"click \"Delete\" 5 times",
			"double click \"Delete\" 2 times",
			"triple click \"Delete\" 3 times",
			"right click \"Delete\" 4 times",
			"middle click \"Delete\" 2 times",
			"wheel click \"Delete\" 2 times",
			"click \"Delete\" if exists",
			"click \"Delete\" if exists with waiting",
			"click \"Delete\" if exists without waiting",
			"click \"Delete\" and switch to the new tab",
			"click \"Delete\" if exists and switch to the new tab",
			"click \"Delete\" if exists with waiting and switch to the new tab",
			"click \"Delete\" if page contains \"Ready\"",
			"press \"Delete\" if page contains \"Ready\""));
	}

	@Test
	public void actions_conditional_if_until_examples_build_parser_valid_lines() {
		TestrigorDriver driver = mock(TestrigorDriver.class);

		String prompt = TestRigorActions.actions(driver)
			.clickIfUrlContains("Delete", "checkout")
			.clickIfPageDoesNotContain("Delete", "Error")
			.clickIfPageContainsWithWaiting("Delete", "Ready")
			.clickIfPageContainsWithoutWaiting("Delete", "Ready")
			.clickUntilPageContains("Login", "value")
			.clickUntilPageContains("button", "result", 5)
			.clickUntilPageContainsWithWaiting("button", "result")
			.clickUntilPageContainsWithoutWaiting("button", "result")
			.clickUntilPageContainsWithWaiting("button", "result", 5)
			.clickUntilPageContainsStoredValue("button", "name")
			.waitUntilPageContains("an_element")
			.waitUntilPageContains("an_element", 10)
			.waitUntilPageContainsWithinSeconds("an_element", 60)
			.waitUntilPageContainsWithinSecondsWithWaiting("an_element", 60)
			.scrollDownUntilPageContains("Submit", 3)
			.enterUntilPageContains("john", "username", "test")
			.enterStoredValueUntilPageContains("username", "username", "name")
			.enterStoredValueUntilPageContainsStoredValue("username", "username", "name")
			.buildPrompt();

		assertThat(prompt).isEqualTo(String.join("\n",
			"click \"Delete\" if url contains \"checkout\"",
			"click \"Delete\" if page does not contain \"Error\"",
			"click \"Delete\" if page contains \"Ready\" with waiting",
			"click \"Delete\" if page contains \"Ready\" without waiting",
			"click \"Login\" until page contains \"value\"",
			"click \"button\" up to 5 times until page contains \"result\"",
			"click \"button\" until page contains \"result\" with waiting",
			"click \"button\" until page contains \"result\" without waiting",
			"click \"button\" up to 5 times until page contains \"result\" with waiting",
			"click \"button\" until page contains stored value \"name\"",
			"wait 1 sec until page contains \"an_element\"",
			"wait 1 sec up to 10 times until page contains \"an_element\"",
			"wait up to 60 seconds until page contains \"an_element\"",
			"wait up to 60 seconds until page contains \"an_element\" with waiting",
			"scroll down up to 3 times until page contains \"Submit\"",
			"enter \"john\" into \"username\" until page contains \"test\"",
			"enter stored value \"username\" into \"username\" until page contains \"name\"",
			"enter stored value \"username\" into \"username\" until page contains stored value \"name\""));
	}

	@Test
	public void actions_navigation_utility_family_examples_build_requested_lines() {
		TestrigorDriver driver = mock(TestrigorDriver.class);

		String prompt = TestRigorActions.actions(driver)
			.waitSec(1)
			.goBack()
			.goForward()
			.reload()
			.openUrl("https://google.com")
			.switchToTab(3)
			.switchToTab("abc")
			.openNewTab()
			.closeTab()
			.hoverOver("peter")
			.hoverOver(3, "hello")
			.scrollDown()
			.scrollUp()
			.scrollLeft()
			.scrollRight()
			.scrollDownOn("panel")
			.scrollUpOn("panel")
			.drag("source", "target")
			.acceptAlert()
			.acceptPromptWithValue("John")
			.setGeoLocation("10,20")
			.setGeoLocationStoredValue("latlong")
			.buildPrompt();

		assertThat(prompt).isEqualTo(String.join("\n",
			"wait 1 sec",
			"go back",
			"go forward",
			"reload",
			"open url \"https://google.com\"",
			"switch to tab 3",
			"switch to tab \"abc\"",
			"open new tab",
			"close tab",
			"hover over \"peter\"",
			"hover over 3rd \"hello\"",
			"scroll down",
			"scroll up",
			"scroll left",
			"scroll right",
			"scroll down on \"panel\"",
			"scroll up on \"panel\"",
			"drag \"source\" to \"target\"",
			"accept prompt",
			"accept prompt with value \"John\"",
			"set geo location \"10,20\"",
			"set geo location stored value \"latlong\""));
	}

	@Test
	public void validations_validate_family_examples_build_requested_lines() {
		TestrigorDriver driver = mock(TestrigorDriver.class);

		String prompt = TestRigorValidations.validations(driver)
			.checkPageContains("welcome")
			.checkPageDoesNotContain("error")
			.checkPageContainsStoredValue("greeting")
			.checkPageContainsRegex("[0-9]{1,3}")
			.checkPageDoesNotContainRegex("error.*")
			.checkPageContainsTemplate("asb###***")
			.checkPageDoesNotContainTemplate("zzz###***")
			.checkPageDidNotChange()
			.checkUrlContains("google")
			.checkUrlDoesNotContain("bing")
			.checkUrlStartsWith("https://")
			.checkUrlDoesNotStartWith("http://")
			.checkUrlEndsWith("/home")
			.checkUrlDoesNotEndWith("/logout")
			.checkUrlIs("https://example.com")
			.checkUrlIsNot("https://evil.example")
			.checkUrlMatchesRegex("https://.*")
			.checkUrlDoesNotMatchRegex("http://.*")
			.checkPageTitleIs("Dashboard")
			.checkPageTitleContains("Dash")
			.checkPageReturnCode("200")
			.checkThatElementContains("status", "Ready")
			.checkThatElementDoesNotContain("status", "Failed")
			.checkThatElementContainsStoredValue("status", "savedStatus")
			.checkThatElementDoesNotContainStoredValue("status", "forbiddenStatus")
			.checkThatInputHasValue("Email", "john@example.com")
			.checkThatInputHasStoredValue("Email", "savedEmail")
			.checkThatInputDoesNotHaveValue("Email", "blocked@example.com")
			.checkThatInputDoesNotHaveStoredValue("Email", "blockedEmail")
			.checkThatCheckboxIsChecked("Terms")
			.checkThatCheckboxIsUnchecked("Newsletter")
			.checkThatElementIsEnabled("Submit")
			.checkThatElementIsDisabled("Delete")
			.checkThatElementIsVisible("Banner")
			.checkThatElementIsInvisible("Spinner")
			.checkThatElementIsClickable("Save")
			.checkThatElementIsNotClickable("Archive")
			.checkThatSelectHasOptionSelected("Country", "USA")
			.checkThatSelectDoesNotHaveOptionSelected("Country", "Mars")
			.checkThatElementMatchesRegex("Progress", "[0-9]{1,3}")
			.checkThatElementDoesNotMatchRegex("Progress", "abc.*")
			.checkThatElementMatchesTemplate("Code", "ABC###")
			.checkThatElementDoesNotMatchTemplate("Code", "ZZZ###")
			.checkThatElementHasAttribute("Link", "href", "https://example.com")
			.checkThatElementDoesNotHaveAttribute("Link", "download")
			.checkThatElementHasProperty("Input", "value", "John")
			.checkThatElementDoesNotHaveProperty("Input", "readonly")
			.checkThatElementHasCssClass("Toast", "visible")
			.checkThatElementDoesNotHaveCssClass("Toast", "hidden")
			.checkThatElementColorIs("Label", "#aabbcc")
			.checkThatElementColorIsNot("Label", "#000000")
			.checkThatElementBackgroundColorIs("Panel", "#ffffff")
			.checkThatElementBackgroundColorIsNot("Panel", "#121212")
			.checkThatElementCursorIs("Button", "pointer")
			.checkThatElementCursorIsNot("Button", "not-allowed")
			.checkThatElementHasLineStyle("Input", "solid")
			.checkThatElementHasNotLineStyle("Input", "dashed")
			.checkThatElementIsBlank("Notes")
			.checkThatElementIsNotBlank("Summary")
			.checkThatElementIsNull("optional")
			.checkThatElementIsNotNull("required")
			.checkThatElementIsEqual("price", "10")
			.checkThatElementIsNotEqual("price", "11")
			.checkThatElementIsEqualAsNumber("price", "10")
			.checkThatElementIsNotEqualAsNumber("price", "11")
			.checkThatElementIsGreaterThan("price", "9")
			.checkThatElementIsGreaterThanOrEqualTo("price", "10")
			.checkThatElementIsLessThan("price", "11")
			.checkThatElementIsLessThanOrEqualTo("price", "10")
			.checkThatElementIsLexicographicallyBefore("name", "zzzz")
			.checkThatElementIsLexicographicallyBeforeOrSame("name", "name")
			.checkThatElementIsLexicographicallyAfter("name", "aaaa")
			.checkThatElementIsLexicographicallyAfterOrSame("name", "name")
			.checkThatStoredValueItselfContains("a", "hello")
			.checkThatStoredValueItselfDoesNotContain("a", "bye")
			.checkThatStoredValueItselfMatchesRegex("a", "[a-z]+")
			.checkThatStoredValueItselfDoesNotMatchRegex("a", "[0-9]+")
			.checkThatStoredValueItselfMatchesTemplate("a", "abc###")
			.checkThatStoredValueItselfDoesNotMatchTemplate("a", "zzz###")
			.checkThatStoredValueItselfIsBlank("a")
			.checkThatStoredValueItselfIsNotBlank("a")
			.checkThatStoredValueItselfIsNull("a")
			.checkThatStoredValueItselfIsNotNull("a")
			.checkThatStoredValueItselfIsEqual("a", "10")
			.checkThatStoredValueItselfIsNotEqual("a", "11")
			.checkThatStoredValueItselfIsEqualAsNumber("a", "10")
			.checkThatStoredValueItselfIsNotEqualAsNumber("a", "11")
			.checkThatStoredValueItselfIsGreaterThan("a", "9")
			.checkThatStoredValueItselfIsGreaterThanOrEqualTo("a", "10")
			.checkThatStoredValueItselfIsLessThan("a", "11")
			.checkThatStoredValueItselfIsLessThanOrEqualTo("a", "10")
			.buildPrompt();

		assertThat(prompt).isEqualTo(String.join("\n",
			"check that page contains \"welcome\"",
			"check that page does not contain \"error\"",
			"check that page contains stored value from \"greeting\"",
			"check that page has regex \"[0-9]{1,3}\"",
			"check that page does not have regex \"error.*\"",
			"check that page has simple template \"asb###***\"",
			"check that page does not have simple template \"zzz###***\"",
			"check that page didn't change",
			"check that url contains \"google\"",
			"check that url does not contain \"bing\"",
			"check that url starts with \"https://\"",
			"check that url does not start with \"http://\"",
			"check that url ends with \"/home\"",
			"check that url does not end with \"/logout\"",
			"check that url is \"https://example.com\"",
			"check that url is not \"https://evil.example\"",
			"check that url matches regex \"https://.*\"",
			"check that url does not match regex \"http://.*\"",
			"check that page title is \"Dashboard\"",
			"check that page title contains \"Dash\"",
			"check that page return code is \"200\"",
			"check that \"status\" contains \"Ready\"",
			"check that \"status\" does not contain \"Failed\"",
			"check that \"status\" contains stored value from \"savedStatus\"",
			"check that \"status\" does not contain stored value from \"forbiddenStatus\"",
			"check that input \"Email\" has value \"john@example.com\"",
			"check that input \"Email\" has value stored value from \"savedEmail\"",
			"check that input \"Email\" does not have value \"blocked@example.com\"",
			"check that input \"Email\" does not have value stored value from \"blockedEmail\"",
			"check that checkbox \"Terms\" is checked",
			"check that checkbox \"Newsletter\" is unchecked",
			"check that \"Submit\" is enabled",
			"check that \"Delete\" is disabled",
			"check that \"Banner\" is visible",
			"check that \"Spinner\" is invisible",
			"check that \"Save\" is clickable",
			"check that \"Archive\" is not clickable",
			"check that select \"Country\" has option selected \"USA\"",
			"check that select \"Country\" does not have option selected \"Mars\"",
			"check that \"Progress\" matches regex \"[0-9]{1,3}\"",
			"check that \"Progress\" does not match regex \"abc.*\"",
			"check that \"Code\" matches template \"ABC###\"",
			"check that \"Code\" does not match template \"ZZZ###\"",
			"check that \"Link\" has attribute \"href\" equal to \"https://example.com\"",
			"check that \"Link\" does not have attribute \"download\"",
			"check that \"Input\" has property \"value\" equal to \"John\"",
			"check that \"Input\" does not have property \"readonly\"",
			"check that \"Toast\" has css class \"visible\"",
			"check that \"Toast\" does not have css class \"hidden\"",
			"check that \"Label\" color is \"#aabbcc\"",
			"check that \"Label\" color is not \"#000000\"",
			"check that \"Panel\" background color is \"#ffffff\"",
			"check that \"Panel\" background color is not \"#121212\"",
			"check that \"Button\" cursor is \"pointer\"",
			"check that \"Button\" cursor is not \"not-allowed\"",
			"check that \"Input\" has line style \"solid\"",
			"check that \"Input\" has not line style \"dashed\"",
			"check that \"Notes\" is blank",
			"check that \"Summary\" is not blank",
			"check that \"optional\" is null",
			"check that \"required\" is not null",
			"check that \"price\" is equal to \"10\"",
			"check that \"price\" is not equal to \"11\"",
			"check that \"price\" is equal as number to \"10\"",
			"check that \"price\" is not equal as number to \"11\"",
			"check that \"price\" is greater than \"9\"",
			"check that \"price\" is greater than or equal to \"10\"",
			"check that \"price\" is less than \"11\"",
			"check that \"price\" is less than or equal to \"10\"",
			"check that \"name\" lexicographically before \"zzzz\"",
			"check that \"name\" lexicographically before or same \"name\"",
			"check that \"name\" lexicographically after \"aaaa\"",
			"check that \"name\" lexicographically after or same \"name\"",
			"check that stored value \"a\" itself contains \"hello\"",
			"check that stored value \"a\" itself does not contain \"bye\"",
			"check that stored value \"a\" itself matches regex \"[a-z]+\"",
			"check that stored value \"a\" itself does not match regex \"[0-9]+\"",
			"check that stored value \"a\" itself matches template \"abc###\"",
			"check that stored value \"a\" itself does not match template \"zzz###\"",
			"check that stored value \"a\" itself is blank",
			"check that stored value \"a\" itself is not blank",
			"check that stored value \"a\" itself is null",
			"check that stored value \"a\" itself is not null",
			"check that stored value \"a\" itself is equal to \"10\"",
			"check that stored value \"a\" itself is not equal to \"11\"",
			"check that stored value \"a\" itself is equal as number to \"10\"",
			"check that stored value \"a\" itself is not equal as number to \"11\"",
			"check that stored value \"a\" itself is greater than \"9\"",
			"check that stored value \"a\" itself is greater than or equal to \"10\"",
			"check that stored value \"a\" itself is less than \"11\"",
			"check that stored value \"a\" itself is less than or equal to \"10\""));
	}

	@Test
	public void validations_buildPrompt_and_execute_use_executePrompt() {
		TestrigorDriver driver = mock(TestrigorDriver.class);

		TestRigorValidations validations = TestRigorValidations.validations(driver)
			.checkPageContains("Welcome")
			.and()
			.checkButtonEnabled("Submit");

		String expected = String.join("\n",
			"check that page contains \"Welcome\"",
			"check that button \"Submit\" is enabled");

		assertThat(validations.buildPrompt()).isEqualTo(expected);

		validations.execute();

		verify(driver).executePrompt(expected);
	}

	@Test
	public void queries_grabValue_delegates_and_returns() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("total price")).thenReturn("$12.00");

		String value = TestRigorQueries.queries(driver).grabValue("total price");

		assertThat(value).isEqualTo("$12.00");
		verify(driver).grabValue("total price");
	}

	@Test
	public void queries_grabValue_typed_element_sends_expected_phrase() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value from input \"Converter to\"")).thenReturn("USD");

		String value = TestRigorQueries.queries(driver).grabValue("input", "Converter to");

		assertThat(value).isEqualTo("USD");
		verify(driver).grabValue("grab value from input \"Converter to\"");
	}

	@Test
	public void queries_grabValueByTemplate_extracts_from_contextual_reference() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value of template \"(###) ###-####\" from \"element below \\\"Phone\\\"\"")).thenReturn("(555) 123-9876");

		String value = TestRigorQueries.queries(driver)
			.grabValueByTemplate("(###) ###-####", "element below \"Phone\"");

		assertThat(value).isEqualTo("(555) 123-9876");
		verify(driver).grabValue("grab value of template \"(###) ###-####\" from \"element below \\\"Phone\\\"\"");
	}

	@Test
	public void queries_grabValueByTemplate_fluent_from_extracts() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value of template \"(###) ###-####\" from \"element below \\\"Phone\\\"\"")).thenReturn("(555) 123-9876");

		String value = TestRigorQueries.queries(driver)
			.grabValueByTemplate("(###) ###-####")
			.from("element below \"Phone\"");

		assertThat(value).isEqualTo("(555) 123-9876");
		verify(driver).grabValue("grab value of template \"(###) ###-####\" from \"element below \\\"Phone\\\"\"");
	}

	@Test
	public void queries_grabValueByTemplate_typed_element_sends_expected_phrase() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value of template \"(###) ###-####\" from input \"Phone\""))
			.thenReturn("(555) 123-9876");

		String value = TestRigorQueries.queries(driver)
			.grabValueByTemplate("(###) ###-####", "input", "Phone");

		assertThat(value).isEqualTo("(555) 123-9876");
		verify(driver).grabValue("grab value of template \"(###) ###-####\" from input \"Phone\"");
	}

	@Test
	public void queries_grabValueByRegex_extracts_from_contextual_reference() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value of regex \"\\\\(\\\\d{3}\\\\) \\\\d{3}-\\\\d{4}\" from \"element below \\\"Phone\\\"\""))
			.thenReturn("(555) 123-9876");

		String value = TestRigorQueries.queries(driver)
			.grabValueByRegex("\\(\\d{3}\\) \\d{3}-\\d{4}", "element below \"Phone\"");

		assertThat(value).isEqualTo("(555) 123-9876");
		verify(driver).grabValue("grab value of regex \"\\\\(\\\\d{3}\\\\) \\\\d{3}-\\\\d{4}\" from \"element below \\\"Phone\\\"\"");
	}

	@Test
	public void queries_grabValueByRegex_typed_element_sends_expected_phrase() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value of regex \"\\\\(\\\\d{3}\\\\) \\\\d{3}-\\\\d{4}\" from input \"Phone\""))
			.thenReturn("(555) 123-9876");

		String value = TestRigorQueries.queries(driver)
			.grabValueOfRegex("\\(\\d{3}\\) \\d{3}-\\d{4}", "input", "Phone");

		assertThat(value).isEqualTo("(555) 123-9876");
		verify(driver).grabValue("grab value of regex \"\\\\(\\\\d{3}\\\\) \\\\d{3}-\\\\d{4}\" from input \"Phone\"");
	}

	@Test
	public void queries_contextualGrab_get_builds_reference_like_contextual_click() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue(anyString())).thenReturn("(555) 123-9876");

		String value = TestRigorQueries.queries(driver)
			.grabValueFrom("phone value")
			.inContext("sectionTwo")
			.completelyBelow("Actions")
			.and()
			.rightOf("rowName")
			.get();

		assertThat(value).isEqualTo("(555) 123-9876");
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(driver).grabValue(captor.capture());
		assertThat(captor.getValue()).contains("within the context of \"sectionTwo\"");
		assertThat(captor.getValue()).contains("completely below \"Actions\"");
		assertThat(captor.getValue()).contains("to the right of \"rowName\"");
		assertThat(captor.getValue()).contains("containing \"phone value\"");
	}

	@Test
	public void queries_contextualGrab_template_matches_user_example() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value of template \"(###) ###-####\" from element below \"Phone\" containing \"phone\""))
			.thenReturn("(555) 123-9876");

		String phone = TestRigorQueries.queries(driver)
			.grabValueFrom("phone")
			.below("Phone")
			.byTemplate("(###) ###-####");

		assertThat(phone).isEqualTo("(555) 123-9876");
		verify(driver).grabValue("grab value of template \"(###) ###-####\" from element below \"Phone\" containing \"phone\"");
	}

	@Test
	public void queries_contextualGrab_typed_chain_renders_typed_context() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value of template \"(###) ###-####\" from input below \"Phone\" containing \"phone\""))
			.thenReturn("(555) 123-9876");

		String phone = TestRigorQueries.queries(driver)
			.grabValueFrom("phone")
			.ofType("input")
			.below("Phone")
			.byTemplate("(###) ###-####");

		assertThat(phone).isEqualTo("(555) 123-9876");
		verify(driver).grabValue("grab value of template \"(###) ###-####\" from input below \"Phone\" containing \"phone\"");
	}

	@Test
	public void queries_grabValueOfRegex_page_level_extracts() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value of regex \"(?<=UserName\\\\:)[^ ]+\"")).thenReturn("john_doe");

		String username = TestRigorQueries.queries(driver).grabValueOfRegex("(?<=UserName\\:)[^ ]+");

		assertThat(username).isEqualTo("john_doe");
		verify(driver).grabValue("grab value of regex \"(?<=UserName\\\\:)[^ ]+\"");
	}

	@Test
	public void queries_grabValueByTemplateFromPage_extracts() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value of template \"(###) ###-####\"")).thenReturn("(555) 123-9876");

		String phone = TestRigorQueries.queries(driver).grabValueByTemplateFromPage("(###) ###-####");

		assertThat(phone).isEqualTo("(555) 123-9876");
		verify(driver).grabValue("grab value of template \"(###) ###-####\"");
	}

	@Test
	public void queries_grabValueOfAttribute_sends_expected_phrase() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value of attribute \"href\" from \"element\"")).thenReturn("https://testrigor.com");

		String value = TestRigorQueries.queries(driver).grabValueOfAttribute("href", "element");

		assertThat(value).isEqualTo("https://testrigor.com");
		verify(driver).grabValue("grab value of attribute \"href\" from \"element\"");
	}

	@Test
	public void queries_grabValueOfCssProperty_sends_expected_phrase() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value of css property \"width\" from \"element\"")).thenReturn("100px");

		String value = TestRigorQueries.queries(driver).grabValueOfCssProperty("width", "element");

		assertThat(value).isEqualTo("100px");
		verify(driver).grabValue("grab value of css property \"width\" from \"element\"");
	}

	@Test
	public void queries_grabValuesFromTable_first_column_parses_list() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab values from table \"my-table\" at first column")).thenReturn("[r1c1, r2c1, r3c1]");

		List<String> values = TestRigorQueries.queries(driver).grabValuesFromTableAtFirstColumn("my-table");

		assertThat(values).containsExactly("r1c1", "r2c1", "r3c1");
		verify(driver).grabValue("grab values from table \"my-table\" at first column");
	}

	@Test
	public void queries_grabValuesFromTable_first_row_parses_list() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab values from table \"my-table\" at first row")).thenReturn("c1\nc2\nc3");

		List<String> values = TestRigorQueries.queries(driver).grabValuesFromTableAtFirstRow("my-table");

		assertThat(values).containsExactly("c1", "c2", "c3");
		verify(driver).grabValue("grab values from table \"my-table\" at first row");
	}

	@Test
	public void queries_contextualGrab_regex_right_of_supports_reference_context() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value of regex \"[0-2][0-9]:[0-5][0-9]\" from element to the right of \"other element\""))
			.thenReturn("23:15");

		String value = TestRigorQueries.queries(driver)
			.grabValue()
			.rightOf("other element")
			.byRegex("[0-2][0-9]:[0-5][0-9]");

		assertThat(value).isEqualTo("23:15");
		verify(driver).grabValue("grab value of regex \"[0-2][0-9]:[0-5][0-9]\" from element to the right of \"other element\"");
	}

	@Test
	public void queries_contextualGrab_regex_below_supports_reference_context() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value of regex \"[0-2][0-9]:[0-5][0-9]\" from element below \"other element\""))
			.thenReturn("07:05");

		String value = TestRigorQueries.queries(driver)
			.grabValue()
			.below("other element")
			.byRegex("[0-2][0-9]:[0-5][0-9]");

		assertThat(value).isEqualTo("07:05");
		verify(driver).grabValue("grab value of regex \"[0-2][0-9]:[0-5][0-9]\" from element below \"other element\"");
	}

	@Test
	public void queries_contextualGrab_regex_right_of_uses_full_step_for_dolar_mep() {
		TestrigorDriver driver = mock(TestrigorDriver.class);
		when(driver.grabValue("grab value of regex \"\\\\$\\\\d+(?:[.,]\\\\d{2})?\" from element to the right of \"Dólar MEP\""))
			.thenReturn("$1423,84");

		String value = TestRigorQueries.queries(driver)
			.grabValue()
			.rightOf("Dólar MEP")
			.byRegex("\\$\\d+(?:[.,]\\d{2})?");

		assertThat(value).isEqualTo("$1423,84");
		verify(driver).grabValue("grab value of regex \"\\\\$\\\\d+(?:[.,]\\\\d{2})?\" from element to the right of \"Dólar MEP\"");
	}

}
