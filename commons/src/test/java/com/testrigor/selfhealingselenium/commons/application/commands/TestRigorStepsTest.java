package com.testrigor.seleniumextension.commons.application.commands;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

/**
 * Unit tests for {@link TestRigorSteps} built strings matching testRigor language syntax.
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class TestRigorStepsTest {

	@Test
	public void build_click_enter_into_check_produces_expected_lines() {
		String prompt = TestRigorSteps.create()
			.click("Login")
			.enter("user@example.com").into("Email")
			.enter("secret").into("Password")
			.click("Log In")
			.checkPageContains("Welcome")
			.build();

		String[] lines = prompt.split("\n");
		assertThat(lines).hasSize(5);
		assertThat(lines[0]).isEqualTo("click \"Login\"");
		assertThat(lines[1]).isEqualTo("enter \"user@example.com\" into \"Email\"");
		assertThat(lines[2]).isEqualTo("enter \"secret\" into \"Password\"");
		assertThat(lines[3]).isEqualTo("click \"Log In\"");
		assertThat(lines[4]).isEqualTo("check that page contains \"Welcome\"");
	}

	@Test
	public void click_nth_produces_ordinal_syntax() {
		String prompt = TestRigorSteps.create()
			.click(3, "hello")
			.build();
		assertThat(prompt).isEqualTo("click on the 3rd \"hello\"");
	}

	@Test
	public void escaping_quotes_in_user_strings() {
		String prompt = TestRigorSteps.create()
			.click("Say \"Hi\"")
			.build();
		assertThat(prompt).isEqualTo("click \"Say \\\"Hi\\\"\"");
	}

	@Test
	public void enterStoredValue_into_produces_expected_line() {
		String prompt = TestRigorSteps.create()
			.enterStoredValue("actionNotes").into("Notes")
			.build();
		assertThat(prompt).isEqualTo("enter stored value \"actionNotes\" into \"Notes\"");
	}

	@Test
	public void check_button_disabled_enabled() {
		String prompt = TestRigorSteps.create()
			.checkButtonDisabled("Add to Cart")
			.checkButtonEnabled("Submit")
			.build();
		String[] lines = prompt.split("\n");
		assertThat(lines[0]).isEqualTo("check that button \"Add to Cart\" is disabled");
		assertThat(lines[1]).isEqualTo("check that button \"Submit\" is enabled");
	}

	@Test
	public void navigation_and_wait() {
		String prompt = TestRigorSteps.create()
			.openUrl("https://example.com")
			.waitSec(3)
			.goBack()
			.reload()
			.build();
		String[] lines = prompt.split("\n");
		assertThat(lines[0]).isEqualTo("open url \"https://example.com\"");
		assertThat(lines[1]).isEqualTo("wait 3 sec");
		assertThat(lines[2]).isEqualTo("go back");
		assertThat(lines[3]).isEqualTo("reload");
	}

	@Test
	public void keyboard_key_and_combination_steps_are_unquoted_and_parser_friendly() {
		String prompt = TestRigorSteps.create()
			.typeKey("win+r")
			.pressKey("ctrl+a")
			.enterKey("shift+ctrl+alt+f1").into("Editor")
			.build();

		String[] lines = prompt.split("\n");
		assertThat(lines).hasSize(3);
		assertThat(lines[0]).isEqualTo("type win+r");
		assertThat(lines[1]).isEqualTo("press ctrl+a");
		assertThat(lines[2]).isEqualTo("enter shift+ctrl+alt+f1 into \"Editor\"");
	}

	@Test
	public void enter_family_examples_match_requested_parser_valid_lines() {
		String prompt = TestRigorSteps.create()
			// Valid step lines for ENTER combinations
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
			// Valid targeted ENTER combinations
			.enterKey("enter").into("Search")
			.enterKey("tab").into("Search")
			.enterKey("backspace").into("Search")
			.enterKey("ctrl+a").into("Editor")
			.enterKey("win+r").into("Input")
			.typeKeyIfPageContains("enter", "Welcome")
			.pressKeyIfPageContains("ctrl+a", "text field")
			// Valid select/choose/insert/type/enter variants
			.enter("john").into("Username")
			.typeInto("john", "Username")
			.insert("john", "Username")
			.select("john", "User dropdown")
			.choose("john", "User dropdown")
			.selectNthOption(1, "Country")
			.selectOption(10, "Country")
			.build();

		String expected = String.join("\n",
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
			"select option 10 from \"Country\"");

		assertThat(prompt).isEqualTo(expected);
	}

	@Test
	public void click_family_examples_match_requested_parser_valid_lines() {
		String prompt = TestRigorSteps.create()
			// Verb aliases
			.tap("Primary Button")
			.press("Secondary Button")
			.push("CTA")
			.follow("Checkout")
			// Click types and counted clicks
			.tripleClick("Menu")
			.middleClick("Menu")
			.wheelClick("Menu")
			.clickTimes("Delete", 5)
			.doubleClickTimes("Delete", 2)
			.tripleClickTimes("Delete", 3)
			.rightClickTimes("Delete", 4)
			.middleClickTimes("Delete", 2)
			.wheelClickTimes("Delete", 2)
			// if exists and waiting combinations
			.clickIfExists("Delete")
			.clickIfExistsWithWaiting("Delete")
			.clickIfExistsWithoutWaiting("Delete")
			// new tab combinations
			.clickAndSwitchToNewTab("Delete")
			.clickIfExistsAndSwitchToNewTab("Delete")
			.clickIfExistsWithWaitingAndSwitchToNewTab("Delete")
			// inline if conditions
			.clickIfPageContains("Delete", "Ready")
			.pressIfPageContains("Delete", "Ready")
			.build();

		String expected = String.join("\n",
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
			"press \"Delete\" if page contains \"Ready\"");

		assertThat(prompt).isEqualTo(expected);
	}

	@Test
	public void conditional_if_until_examples_match_parser_valid_lines() {
		String prompt = TestRigorSteps.create()
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
			.build();

		String expected = String.join("\n",
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
			"enter stored value \"username\" into \"username\" until page contains stored value \"name\"");

		assertThat(prompt).isEqualTo(expected);
	}

	@Test
	public void navigation_utility_family_examples_match_requested_parser_valid_lines() {
		String prompt = TestRigorSteps.create()
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
			.build();

		String expected = String.join("\n",
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
			"set geo location stored value \"latlong\"");

		assertThat(prompt).isEqualTo(expected);
	}

	@Test
	public void validate_family_examples_match_requested_parser_valid_lines() {
		String prompt = TestRigorSteps.create()
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
			.build();

		String expected = String.join("\n",
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
			"check that stored value \"a\" itself is less than or equal to \"10\"");

		assertThat(prompt).isEqualTo(expected);
	}

	@Test
	public void login_step() {
		String prompt = TestRigorSteps.create()
			.login()
			.build();
		assertThat(prompt).isEqualTo("login");
	}

	@Test
	public void contextual_click_in_table_row_and_column() {
		String prompt = TestRigorSteps.create()
			.clickOn("Delete")
			.withinTable("actions")
			.rowContaining("id1")
			.column("Actions")
			.add()
			.build();

		assertThat(prompt).isEqualTo(
			"click on \"Delete\" within the context of table \"actions\" at row containing \"id1\" and column \"Actions\"");
	}

	@Test
	public void contextual_click_in_context_below_and_right() {
		String prompt = TestRigorSteps.create()
			.clickOn("Delete")
			.inContext("sectionTwo")
			.below("Actions")
			.rightOf("rowName")
			.add()
			.build();

		assertThat(prompt).isEqualTo(
			"click on \"Delete\" within the context of \"sectionTwo\" below \"Actions\" and to the right of \"rowName\"");
	}

	@Test
	public void contextual_click_in_context_below_title() {
		String prompt = TestRigorSteps.create()
			.clickOn("Button")
			.inContext("sectionOne")
			.below("Title")
			.add()
			.build();

		assertThat(prompt).isEqualTo(
			"click on \"Button\" within the context of \"sectionOne\" below \"Title\"");
	}

	@Test
	public void contextual_click_below_and_right_in_context() {
		String prompt = TestRigorSteps.create()
			.clickOn("Button")
			.inContext("sectionTwo")
			.below("Title")
			.rightOf("leftHeader")
			.add()
			.build();

		assertThat(prompt).isEqualTo(
			"click on \"Button\" within the context of \"sectionTwo\" below \"Title\" and to the right of \"leftHeader\"");
	}

	@Test
	public void contextual_click_nested_context_with_two_anchors() {
		String prompt = TestRigorSteps.create()
			.clickOn("Button")
			.inContext("sectionOne")
			.below("Subtitle")
			.rightOf("leftHeader")
			.add()
			.build();

		assertThat(prompt).isEqualTo(
			"click on \"Button\" within the context of \"sectionOne\" below \"Subtitle\" and to the right of \"leftHeader\"");
	}

	@Test
	public void contextual_enter_roughly_below_and_right() {
		String prompt = TestRigorSteps.create()
			.enterFluent("Peter")
			.into("Section")
			.roughlyBelow("Type")
			.rightOf("Description")
			.add()
			.build();

		assertThat(prompt).isEqualTo(
			"enter \"Peter\" into \"Section\" roughly below \"Type\" and to the right of \"Description\"");
	}

	@Test
	public void contextual_enter_completely_below_and_right() {
		String prompt = TestRigorSteps.create()
			.enterFluent("Peter")
			.into("Section")
			.completelyBelow("Type")
			.rightOf("Description")
			.add()
			.build();

		assertThat(prompt).isEqualTo(
			"enter \"Peter\" into \"Section\" completely below \"Type\" and to the right of \"Description\"");
	}

	@Test
	public void and_allows_readable_between_commands_chaining() {
		String prompt = TestRigorSteps.create()
			.click("A")
			.and()
			.checkPageContains("B")
			.build();

		assertThat(prompt).isEqualTo("click \"A\"\ncheck that page contains \"B\"");
	}

	@Test
	public void and_allows_readable_contextual_chaining() {
		String prompt = TestRigorSteps.create()
			.clickOn("Delete")
			.inContext("sectionTwo")
			.below("Actions")
			.and()
			.rightOf("rowName")
			.add()
			.build();

		assertThat(prompt).isEqualTo(
			"click on \"Delete\" within the context of \"sectionTwo\" below \"Actions\" and to the right of \"rowName\"");
	}
}
