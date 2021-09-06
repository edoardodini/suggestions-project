package com.examples.suggestions_project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.repository.SuggestionRepository;

/**
 * Some examples of tests for the web controller when running in a real web
 * container, manually using the {@link SuggestionRepository}.
 * 
 * The web server is started on a random port, which can be retrieved by
 * injecting in the test a {@link LocalServerPort}.
 * 
 * In tests you can't rely on fixed identifiers: use the ones returned by the
 * repository after saving (automatically generated)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SuggestionWebControllerIT {

	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";

	@Autowired
	private SuggestionRepository suggestionRepository;

	@LocalServerPort
	private int port;

	private WebDriver driver;

	private String baseUrl;
	private String suggestionsUrl;
	private String newSuggestionUrl;
	private String editSuggestionUrl;
	private String hideSuggestionUrl;
	private String deleteSuggestionUrl;
	private String loginUrl;
	private String errorUrl;

	@Before
	public void setup() {
		baseUrl = "http://localhost:" + port + "/";
		suggestionsUrl = baseUrl + "suggestions";
		newSuggestionUrl = suggestionsUrl + "/new";
		editSuggestionUrl = suggestionsUrl + "/edit/";
		hideSuggestionUrl = suggestionsUrl + "/hide/";
		deleteSuggestionUrl = suggestionsUrl + "/delete/";
		loginUrl = baseUrl + "login";
		errorUrl = baseUrl + "errorPage";
		driver = new HtmlUnitDriver();
		// always start with an empty database
		suggestionRepository.deleteAll();
		suggestionRepository.flush();
	}

	@After
	public void teardown() {
		driver.quit();
	}

	@Test
	public void testHomeToSuggestionsPageLinks() {
		// go to "/"
		driver.get(baseUrl);
		// go to "/login"
		driver.findElements(By.linkText("here")).get(1).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(suggestionsUrl);
		// go to "/"
		driver.findElement(By.linkText("Home")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl);
	}

	@Test
	public void testSuggestionsPageNotAdmin() {
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as generic user",
				"No suggestions");
		Suggestion testSuggestionVisible = suggestionRepository.save(new Suggestion(null, "test suggestion", true));
		Suggestion testSuggestionNotVisible = suggestionRepository.save(new Suggestion(null, "not visible", false));
		// go to "/suggestions", to refresh the page
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("Suggestions", "ID",
				"Suggestion");
		assertThat(driver.getPageSource()).doesNotContain("No suggestions", "No hidden suggestions");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains(
				testSuggestionVisible.getId().toString(), testSuggestionVisible.getSuggestionText(), "Comments");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).doesNotContain(
				testSuggestionNotVisible.getId().toString(), testSuggestionNotVisible.getSuggestionText());
		// the "Comments" link is present with href containing /suggestions/{id}
		driver.findElement(
				By.cssSelector("a[href*='/suggestions/" + testSuggestionVisible.getId() + "/comments" + "']"));
	}

	@Test
	public void testSuggestionsPageLinksNotAdmin() {
		Suggestion testSuggestionVisible = suggestionRepository.save(new Suggestion(null, "test suggestion", true));
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		// go to "/login"
		driver.findElement(By.linkText("Home")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl);
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		// go to "/suggestions/new"
		driver.findElement(By.linkText("New suggestion")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(newSuggestionUrl);
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		// go to "/suggestions/{id}/comments"
		driver.findElement(By.linkText("Comments")).click();
		assertThat(driver.getCurrentUrl())
				.isEqualTo(suggestionsUrl + "/" + testSuggestionVisible.getId() + "/comments");
	}

	@Test
	public void testSuggestionsPageAdmin() {
		// login as admin
		adminLogin();
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New suggestion", "Logged as Admin",
				"Logout", "No suggestions", "No hidden suggestions");
		Suggestion testSuggestionVisible = suggestionRepository.save(new Suggestion(null, "test suggestion", true));
		Suggestion testSuggestionNotVisible = suggestionRepository.save(new Suggestion(null, "not visible", false));
		// go to "/suggestions", to refresh the page
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New suggestion", "Logged as Admin",
				"Logout");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("Suggestions", "ID",
				"Suggestion");
		assertThat(driver.findElement(By.id("hiddenSuggestions_table")).getText()).contains("Hidden suggestions", "ID",
				"Suggestion");
		assertThat(driver.getPageSource()).doesNotContain("No suggestions");
		assertThat(driver.getPageSource()).doesNotContain("No hidden suggestions");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains(
				testSuggestionVisible.getId().toString(), testSuggestionVisible.getSuggestionText(), "Comments", "Edit",
				"Hide", "Delete");
		assertThat(driver.findElement(By.id("hiddenSuggestions_table")).getText()).contains(
				testSuggestionNotVisible.getId().toString(), testSuggestionNotVisible.getSuggestionText(), "Comments",
				"Edit", "Show", "Delete");
		// the "Comments" link is present with href containing /suggestions/{id}
		driver.findElement(
				By.cssSelector("a[href*='/suggestions/" + testSuggestionVisible.getId() + "/comments" + "']"));
		driver.findElement(By.cssSelector("a[href*='/suggestions/edit/" + testSuggestionVisible.getId() + "']"));
		driver.findElement(By.cssSelector("a[href*='/suggestions/hide/" + testSuggestionVisible.getId() + "']"));
		driver.findElement(By.cssSelector("a[href*='/suggestions/delete/" + testSuggestionVisible.getId() + "']"));
		driver.findElement(
				By.cssSelector("a[href*='/suggestions/" + testSuggestionNotVisible.getId() + "/comments" + "']"));
		driver.findElement(By.cssSelector("a[href*='/suggestions/edit/" + testSuggestionNotVisible.getId() + "']"));
		driver.findElement(By.cssSelector("a[href*='/suggestions/hide/" + testSuggestionNotVisible.getId() + "']"));
		driver.findElement(By.cssSelector("a[href*='/suggestions/delete/" + testSuggestionNotVisible.getId() + "']"));
	}

	@Test
	public void testSuggestionsPageLinksAdmin() {
		adminLogin();
		Suggestion testSuggestionVisible = suggestionRepository.save(new Suggestion(null, "test suggestion", true));
		Suggestion testSuggestionNotVisible = suggestionRepository.save(new Suggestion(null, "not visible", false));
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		// go to "/login"
		driver.findElement(By.linkText("Home")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl);
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		// go to "/suggestions/new"
		driver.findElement(By.linkText("New suggestion")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(newSuggestionUrl);
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		// go to "/suggestions/{id}/comments"
		driver.findElements(By.linkText("Comments")).get(0).click();
		assertThat(driver.getCurrentUrl())
				.isEqualTo(suggestionsUrl + "/" + testSuggestionVisible.getId() + "/comments");
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		// go to "/suggestions/edit/{id}"
		driver.findElements(By.linkText("Edit")).get(0).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(editSuggestionUrl + testSuggestionVisible.getId());
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		// go to "/suggestions/hide/{id}"
		driver.findElement(By.linkText("Hide")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(hideSuggestionUrl + testSuggestionVisible.getId());
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		// go to "/suggestions/delete/{id}"
		driver.findElements(By.linkText("Delete")).get(0).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(deleteSuggestionUrl + testSuggestionVisible.getId());
		// hiddensection
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		// go to "/suggestions/{id}/comments"
		driver.findElements(By.linkText("Comments")).get(1).click();
		assertThat(driver.getCurrentUrl())
				.isEqualTo(suggestionsUrl + "/" + testSuggestionNotVisible.getId() + "/comments");
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		// go to "/suggestions/edit/{id}"
		driver.findElements(By.linkText("Edit")).get(1).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(editSuggestionUrl + testSuggestionNotVisible.getId());
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		// go to "/suggestions/hide/{id}"
		driver.findElement(By.linkText("Show")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(hideSuggestionUrl + testSuggestionNotVisible.getId());
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		// go to "/suggestions/delete/{id}"
		driver.findElements(By.linkText("Delete")).get(1).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(deleteSuggestionUrl + testSuggestionNotVisible.getId());
	}

	@Test
	public void testSuggestionsPageLogoutAdmin() {
		// login as admin
		adminLogin();
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New suggestion", "Logged as Admin",
				"Logout", "No suggestions", "No hidden suggestions");
		driver.findElement(By.name("btn_logout")).click();
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as generic user",
				"No suggestions");
	}

	@Test
	public void testNewSuggestionPageLinks() {
		// go to "/suggestions/new"
		driver.get(newSuggestionUrl);
		// go to "/"
		driver.findElement(By.linkText("Home")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl);
	}

	@Test
	public void testNewSuggestionPage() {
		// go to "/suggestions" to check that no suggestion is present
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New suggestion", "Logged as generic user",
				"No suggestions");
		assertThat(driver.getPageSource()).doesNotContain("ID");
		// go to "/suggestions/new"
		driver.get(newSuggestionUrl);
		assertThat(driver.getPageSource()).contains("Home", "Edit suggestion", "Suggestion:", "Save");
		driver.findElement(By.name("suggestionText")).sendKeys("firstSuggestion1");
		driver.findElement(By.name("btn_submit")).click();
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as generic user");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("ID", "Suggestions", "Suggestion",
				"firstSuggestion1");
		assertThat(suggestionRepository.findAll().get(0).getSuggestionText()).isEqualTo("firstSuggestion1");
	}

	@Test
	public void testEditSuggestionPageAdmin() {
		adminLogin();
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestion", true));
		// go to "/suggestions" to check that old suggestion is present
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as Admin", "Logout");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("ID", "Suggestions",
				suggestion.getId().toString(), suggestion.getSuggestionText());
		// go to "/suggestions/edit/{id}"
		driver.findElement(By.linkText("Edit")).click();
		assertThat(driver.getPageSource()).contains("Home", "Edit suggestion", "Suggestion:", "Update");
		// fill the form
		driver.findElement(By.name("suggestionText")).clear();
		driver.findElement(By.name("suggestionText")).sendKeys("new");
		// submit the edit
		driver.findElement(By.name("btn_submit")).click();
		// go to "/suggestions" to check that old suggestion has been modified
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as Admin", "Logout");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("ID", "Suggestions",
				suggestion.getId().toString(), "new");
		assertThat(driver.findElement(By.id("suggestions_table")).getText())
				.doesNotContain(suggestion.getSuggestionText());
		assertThat(suggestionRepository.findById(suggestion.getId()).get().getSuggestionText()).isEqualTo("new");
	}

	@Test
	public void testShowHideSuggestionPageAdmin() {
		adminLogin();
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestionText", true));
		// go to "/suggestions" to check that the suggestion is present and visible
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as Admin", "Logout");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("ID", "Suggestions",
				suggestion.getId().toString(), suggestion.getSuggestionText(), "Comments", "Edit", "Hide", "Delete");
		// go to "/suggestions/hide/{id}"
		driver.findElement(By.linkText("Hide")).click();
		assertThat(driver.getPageSource()).contains("Home", "Hide/Show suggestion", "Want to hide?", "Yes");
		// hide by clicking the button
		driver.findElement(By.name("btn_submit")).click();
		// go to "/suggestions" to check that the suggestion has been hidden
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as Admin", "Logout");
		assertThat(driver.findElement(By.id("hiddenSuggestions_table")).getText()).contains("ID", "Hidden suggestions",
				"Suggestion", suggestion.getId().toString(), suggestion.getSuggestionText());
		assertThat(suggestionRepository.findById(suggestion.getId()).get().getVisible()).isFalse();
		// go to "/suggestions/hide/{id}"
		driver.findElement(By.linkText("Show")).click();
		assertThat(driver.getPageSource()).contains("Home", "Hide/Show suggestion", "Want to show?", "Yes");
		// hide by clicking the button
		driver.findElement(By.name("btn_submit")).click();
		// go to "/suggestions" to check that the suggestion has been showed
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as Admin", "Logout");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("ID", "Suggestions",
				suggestion.getId().toString(), suggestion.getSuggestionText());
		assertThat(suggestionRepository.findById(suggestion.getId()).get().getVisible()).isTrue();
	}

	@Test
	public void testDeleteSuggestionPageAdmin() {
		adminLogin();
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestionText", true));
		// go to "/suggestions" to check that old suggestion is present
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as Admin", "Logout");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("ID", "Suggestions",
				suggestion.getId().toString(), suggestion.getSuggestionText(), "Comments", "Edit", "Hide", "Delete");
		// go to "/suggestions/delete/{id}"
		driver.findElement(By.linkText("Delete")).click();
		assertThat(driver.getPageSource()).contains("Home", "Delete suggestion", "Want to delete?", "Yes");
		// delete by clicking the button
		assertThat(suggestionRepository.findAll().size()).isEqualTo(1);
		driver.findElement(By.name("btn_submit")).click();
		assertThat(suggestionRepository.findAll().size()).isZero();
		// go to "/suggestions" to check that old suggestion has been deleted
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New suggestion", "Logged as Admin",
				"Logout", "No suggestions", "No hidden suggestions");
	}

	@Test
	public void testAdminPageWithNotAdminUser() {
		// go to "/suggestions/edit/1"
		driver.get(editSuggestionUrl + "1");
		// go to "/"
		assertThat(driver.getCurrentUrl()).isEqualTo(loginUrl);
		// go to "/suggestions/hide/1"
		driver.get(hideSuggestionUrl + "1");
		// go to "/"
		assertThat(driver.getCurrentUrl()).isEqualTo(loginUrl);
		// go to "/suggestions/delete/1"
		driver.get(deleteSuggestionUrl + "1");
		// go to "/"
		assertThat(driver.getCurrentUrl()).isEqualTo(loginUrl);
	}

	@Test
	public void testEditSuggestionWithIdOfANotExistingSuggestion() {
		int intSuggestion = 1;
		adminLogin();
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as Admin", "No suggestions",
				"No hidden suggestions", "Logout");
		// go to "/suggestions/edit/{id}"
		driver.get(editSuggestionUrl + intSuggestion);
		assertThat(driver.getPageSource()).contains("Home", "Edit suggestion",
				"No suggestion found with id: " + intSuggestion);
	}

	@Test
	public void testHideShowSuggestionWithIdOfANotExistingSuggestion() {
		int intSuggestion = 1;
		adminLogin();
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as Admin", "No suggestions",
				"No hidden suggestions", "Logout");
		// go to "/suggestions/hide/{id}"
		driver.get(hideSuggestionUrl + intSuggestion);
		assertThat(driver.getPageSource()).contains("Home", "Hide/Show suggestion",
				"No suggestion found with id: " + intSuggestion);
	}

	@Test
	public void testDeleteSuggestionWithIdOfANotExistingSuggestion() {
		int intSuggestion = 1;
		adminLogin();
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as Admin", "No suggestions",
				"No hidden suggestions", "Logout");
		// go to "/suggestions/delete/{id}"
		driver.get(deleteSuggestionUrl + intSuggestion);
		assertThat(driver.getPageSource()).contains("Home", "Delete suggestion",
				"No suggestion found with id: " + intSuggestion);
	}

	@Test
	public void testEditSuggestionButNoMorePresent() {
		// creation of a second web driver to act as another user
		WebDriver driver2 = new HtmlUnitDriver();
		adminLogin();
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestion", true));
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as Admin", "Logout");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("ID", "Suggestions",
				suggestion.getId().toString(), suggestion.getSuggestionText());
		// go to "/suggestions/edit/{id}"
		driver.findElement(By.linkText("Edit")).click();
		// fill the form
		driver.findElement(By.name("suggestionText")).clear();
		driver.findElement(By.name("suggestionText")).sendKeys("new");

		// user number 2 log himself
		// go to login page
		driver2.get(loginUrl);
		// fill the form
		driver2.findElement(By.name("username")).sendKeys(USERNAME);
		driver2.findElement(By.name("password")).sendKeys(PASSWORD);
		// submit login
		driver2.findElement(By.name("btn_submit")).click();
		// go to suggestions page
		driver2.get(suggestionsUrl);
		// go to "/suggestions/delete/{id}"
		driver2.findElement(By.linkText("Delete")).click();
		// delete by clicking the button
		assertThat(suggestionRepository.findAll().size()).isEqualTo(1);
		driver2.findElement(By.name("btn_submit")).click();
		assertThat(suggestionRepository.findAll().size()).isZero();
		driver2.quit();

		// user 1 try to edit but the suggestion does not exist anymore
		// submit the edit
		driver.findElement(By.name("btn_submit")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(errorUrl);
		assertThat(driver.getPageSource()).contains("Error", "Home",
				"It is not possible to update a suggestion with the id: " + suggestion.getId());
	}

	@Test
	public void testHideShowSuggestionsButNoSuggestions() {
		// creation of a second web driver to act as another user
		WebDriver driver2 = new HtmlUnitDriver();
		adminLogin();
		Suggestion suggestionVisible = suggestionRepository.save(new Suggestion(null, "suggestionVisible", true));
		Suggestion suggestionHidden = suggestionRepository.save(new Suggestion(null, "suggestionHidden", false));
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as Admin", "Logout");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("ID", "Suggestions",
				suggestionVisible.getId().toString(), suggestionVisible.getSuggestionText());
		assertThat(driver.findElement(By.id("hiddenSuggestions_table")).getText()).contains("ID", "Hidden suggestions",
				"Suggestion", suggestionHidden.getId().toString(), suggestionHidden.getSuggestionText());
		// go to "/suggestions/hide/{id}"
		driver.findElement(By.linkText("Hide")).click();

		// user number 2 log himself
		// go to login page
		driver2.get(loginUrl);
		// fill the form
		driver2.findElement(By.name("username")).sendKeys(USERNAME);
		driver2.findElement(By.name("password")).sendKeys(PASSWORD);
		// submit login
		driver2.findElement(By.name("btn_submit")).click();
		// go to suggestions page
		driver2.get(suggestionsUrl);
		// go to "/suggestions/delete/{id}"
		driver2.findElements(By.linkText("Delete")).get(0).click();
		// delete by clicking the button
		assertThat(suggestionRepository.findAll().size()).isEqualTo(2);
		driver2.findElement(By.name("btn_submit")).click();
		assertThat(suggestionRepository.findAll().size()).isEqualTo(1);

		// user 1 try to hide but the suggestion does not exist anymore
		// submit the edit
		driver.findElement(By.name("btn_submit")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(errorUrl);
		assertThat(driver.getPageSource()).contains("Error", "Home",
				"It is not possible to update a suggestion with the id: " + suggestionVisible.getId());
		// go to suggestions
		driver.get(suggestionsUrl);
		// go to "/suggestions/hide/{id}"
		driver.findElement(By.linkText("Show")).click();

		// user number 2
		// go to login page
		driver2.get(suggestionsUrl);
		// go to "/suggestions/delete/{id}"
		driver2.findElement(By.linkText("Delete")).click();
		// delete by clicking the button
		assertThat(suggestionRepository.findAll().size()).isEqualTo(1);
		driver2.findElement(By.name("btn_submit")).click();
		assertThat(suggestionRepository.findAll().size()).isZero();
		driver2.quit();

		// user 1 try to show but the suggestion does not exist anymore
		// submit the edit
		driver.findElement(By.name("btn_submit")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(errorUrl);
		assertThat(driver.getPageSource()).contains("Error", "Home",
				"It is not possible to update a suggestion with the id: " + suggestionHidden.getId());
	}

	@Test
	public void testDeleteSuggestionButNoSuggestionToDelete() {
		// creation of a second web driver to act as another user
		WebDriver driver2 = new HtmlUnitDriver();
		adminLogin();
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestion", true));
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as Admin", "Logout");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("ID", "Suggestions",
				suggestion.getId().toString(), suggestion.getSuggestionText());
		// go to "/suggestions/delete/{id}"
		driver.findElement(By.linkText("Delete")).click();

		// user number 2 log himself
		// go to login page
		driver2.get(loginUrl);
		// fill the form
		driver2.findElement(By.name("username")).sendKeys(USERNAME);
		driver2.findElement(By.name("password")).sendKeys(PASSWORD);
		// submit login
		driver2.findElement(By.name("btn_submit")).click();
		// go to suggestions page
		driver2.get(suggestionsUrl);
		// go to "/suggestions/delete/{id}"
		driver2.findElement(By.linkText("Delete")).click();
		// delete by clicking the button
		assertThat(suggestionRepository.findAll().size()).isEqualTo(1);
		driver2.findElement(By.name("btn_submit")).click();
		assertThat(suggestionRepository.findAll().size()).isZero();
		driver2.quit();
		// user 1 try to delete but the suggestion does not exist anymore
		// submit the edit
		driver.findElement(By.name("btn_submit")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(errorUrl);
		assertThat(driver.getPageSource()).contains("Error", "Home",
				"It is not possible to delete a suggestion with the id: " + suggestion.getId());
	}

	@Test
	public void testEditSuggestionButNoMoreAdminSoLoginPage() {
		adminLogin();
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestionNumberOne", true));
		// go to "/suggestions/edit/{sugg.id}"
		driver.get(editSuggestionUrl + suggestion.getId());
		// the user delete all cookies so it is no more admin
		driver.manage().deleteAllCookies();
		// edit the suggestion but no more admin so login page
		driver.findElement(By.name("suggestionText")).clear();
		String modifiedSuggestion = "modified_suggestion";
		driver.findElement(By.name("suggestionText")).sendKeys(modifiedSuggestion);
		driver.findElement(By.name("btn_submit")).click();
		// the operation is not performed and the current page is "/login"
		assertThat(driver.getCurrentUrl()).isEqualTo(loginUrl);
		driver.get(suggestionsUrl);
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("Suggestions", "ID", "Suggestion",
				suggestion.getId().toString(), suggestion.getSuggestionText());
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).doesNotContain(modifiedSuggestion);
	}

	@Test
	public void testHideSuggestionButNoMoreAdminSoLoginPage() {
		adminLogin();
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestionNumberOne", true));
		// go to "/suggestions/hide/{id}"
		driver.get(hideSuggestionUrl + suggestion.getId());
		assertThat(driver.getPageSource()).contains("Want to hide?");
		// the user delete all cookies so it is no more admin
		driver.manage().deleteAllCookies();
		driver.findElement(By.name("btn_submit")).click();
		// the operation is not performed and the current page is "/login"
		assertThat(driver.getCurrentUrl()).isEqualTo(loginUrl);
		adminLogin();
		driver.get(suggestionsUrl);
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("Suggestions", "ID", "Suggestion",
				suggestion.getId().toString(), suggestion.getSuggestionText());
		assertThat(driver.getPageSource()).contains("No hidden suggestions");
		assertThatThrownBy(() -> {
			driver.findElement(By.id("hiddenSuggestions_table"));
		}).isInstanceOf(NoSuchElementException.class);
	}

	@Test
	public void testShowSuggestionButNoMoreAdminSoLoginPage() {
		adminLogin();
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestionNumberOne", true));
		// go to "/suggestions/hide/{id}"
		driver.get(hideSuggestionUrl + suggestion.getId());
		assertThat(driver.getPageSource()).contains("Want to hide?");
		// hide the suggestion
		driver.findElement(By.name("btn_submit")).click();
		// go to "/suggestions/hide/{id}" to show
		driver.get(hideSuggestionUrl + suggestion.getId());
		assertThat(driver.getPageSource()).contains("Want to show?");
		// the user delete all cookies so it is no more admin
		driver.manage().deleteAllCookies();
		driver.findElement(By.name("btn_submit")).click();
		// the operation is not performed and the current page is "/login"
		assertThat(driver.getCurrentUrl()).isEqualTo(loginUrl);
		adminLogin();
		driver.get(suggestionsUrl);
		assertThat(driver.findElement(By.id("hiddenSuggestions_table")).getText()).contains("Hidden suggestions", "ID",
				"Suggestion", suggestion.getId().toString(), suggestion.getSuggestionText());
		assertThat(driver.getPageSource()).contains("No suggestions");
		assertThatThrownBy(() -> {
			driver.findElement(By.id("suggestions_table"));
		}).isInstanceOf(NoSuchElementException.class);
	}

	@Test
	public void testDeleteSuggestionButNoMoreAdminSoLoginPage() {
		adminLogin();
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestionNumberOne", true));
		// go to "/suggestions/delete/{id}"
		driver.get(deleteSuggestionUrl + suggestion.getId());
		// the user delete all cookies so it is no more admin
		driver.manage().deleteAllCookies();
		// delete the comment but no more admin
		driver.findElement(By.name("btn_submit")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(loginUrl);
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Suggestions", "Suggestion", "ID", suggestion.getId().toString(),
				suggestion.getSuggestionText());
	}

	private void adminLogin() {
		// go to login page
		driver.get(loginUrl);
		// fill the form
		driver.findElement(By.name("username")).sendKeys(USERNAME);
		driver.findElement(By.name("password")).sendKeys(PASSWORD);
		// submit login
		driver.findElement(By.name("btn_submit")).click();
	}

}
