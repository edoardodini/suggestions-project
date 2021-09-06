package com.examples.suggestions_project;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Executes the tests against a running Spring Boot application; if you run this
 * as test from Eclipse, make sure you first manually run the Spring Boot
 * application.
 * 
 * No Spring specific testing mechanism is used here: this is a plain JUnit
 * test.
 */
public class SuggestionsWebAppE2E { // NOSONAR not a standard testcase name

	private static final String USERNAME = System.getProperty("spring.security.user.name", "admin");
	private static final String PASSWORD = System.getProperty("spring.security.user.password", "admin");
	private static int port = Integer.parseInt(System.getProperty("server.port", "8080"));

	private String baseUrl = "http://localhost:" + port;
	private String suggestionsUrl = baseUrl + "/suggestions";
	private String newSuggestionUrl = suggestionsUrl + "/new";
	private String suggestionsUrlSlash = suggestionsUrl + "/";

	private WebDriver driver;

	@BeforeClass
	public static void setupClass() {
		// setup Chrome Driver
		WebDriverManager.chromedriver().setup();
	}

	@Before
	public void setup() {
		driver = new ChromeDriver();
	}

	@After
	public void teardown() {
		driver.quit();
	}

	@Test
	public void testGenericUserCreateSomeSuggestionsAndComments() {
		driver.get(baseUrl);
		// the "login" link is present with href containing /login
		driver.findElement(By.cssSelector("a[href*='/login"));
		// the "suggestions" link is present with href containing /suggestions
		driver.findElement(By.cssSelector("a[href*='/suggestions"));
		// adding the first suggestion
		String firstSuggestionText = "first_suggestion";
		Integer firstSuggestionId = addSuggestionAndGoToSuggestionPage(firstSuggestionText);
		// adding the second suggestion
		String secondSuggestionText = "second_suggestion";
		Integer secondSuggestionId = addSuggestionAndGoToSuggestionPage(secondSuggestionText);
		// adding the third suggestion
		String thirdSuggestionText = "third_suggestion";
		Integer thirdSuggestionId = addSuggestionAndGoToSuggestionPage(thirdSuggestionText);
		// checking the suggestion page contains the three suggestions saved
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("Suggestions", "ID", "Suggestion",
				firstSuggestionId.toString(), firstSuggestionText, secondSuggestionId.toString(), secondSuggestionText,
				thirdSuggestionId.toString(), thirdSuggestionText);

		// the first suggestion will not have comment
		// the second suggestion will have one
		String firstCommentToSecondSuggestionText = "first_comment_to_second_suggestion";
		Integer firstCommentToSecondSuggestionId = addCommentAndGoToSuggestionIdCommentPage(secondSuggestionId,
				firstCommentToSecondSuggestionText);

		// checking the "suggestions/{sugg.id}/comments" page contains the saved comment
		assertThat(driver.getCurrentUrl()).isEqualTo(suggestionsUrlSlash + secondSuggestionId + "/comments");
		assertThat(driver.findElement(By.id("comments_table")).getText()).contains("Comments", "ID", "Comment",
				firstCommentToSecondSuggestionId.toString(), firstCommentToSecondSuggestionText);

		// the third suggestion will have two suggestions
		// first comment to third suggestion
		String firstCommentToThirdSuggestionText = "first_comment_to_third_suggestion";
		Integer firstCommentToThirdSuggestionId = addCommentAndGoToSuggestionIdCommentPage(thirdSuggestionId,
				firstCommentToThirdSuggestionText);
		// second comment to third suggestion
		String secondCommentToThirdSuggestionText = "second_comment_to_third_suggestion";
		Integer secondCommentToThirdSuggestionId = addCommentAndGoToSuggestionIdCommentPage(thirdSuggestionId,
				secondCommentToThirdSuggestionText);
		// check the "suggestions/{sugg.id}/comments" page contains the saved comments
		assertThat(driver.getCurrentUrl()).isEqualTo(suggestionsUrlSlash + thirdSuggestionId + "/comments");
		assertThat(driver.findElement(By.id("comments_table")).getText()).contains("Comments", "ID", "Comment",
				firstCommentToThirdSuggestionId.toString(), firstCommentToThirdSuggestionText,
				secondCommentToThirdSuggestionId.toString(), secondCommentToThirdSuggestionText);
	}

	@Test
	public void testAdminManageSomeSuggestionsAndComments() {
		Random random = new Random();
		// creating a "random" prefix for suggestions and comments in order
		// to have different suggestions and comments between two runs of the test
		int randomPrefix = random.nextInt(1000) + 100;
		// a generic user create some suggestions and comments
		// first suggestion
		String firstSuggestionText = randomPrefix + "suggestionOne";
		Integer firstSuggestionId = addSuggestionAndGoToSuggestionPage(firstSuggestionText);
		// adding the second suggestion
		String secondSuggestionText = randomPrefix + "suggestionTwo";
		Integer secondSuggestionId = addSuggestionAndGoToSuggestionPage(secondSuggestionText);
		// adding the third suggestion
		String thirdSuggestionText = randomPrefix + "suggestionThree";
		Integer thirdSuggestionId = addSuggestionAndGoToSuggestionPage(thirdSuggestionText);

		// the third suggestion will have two suggestions
		// first comment to third suggestion
		String firstCommentToThirdSuggestionText = randomPrefix + "commentOneToThird";
		Integer firstCommentToThirdSuggestionId = addCommentAndGoToSuggestionIdCommentPage(thirdSuggestionId,
				firstCommentToThirdSuggestionText);
		// second comment to third suggestion
		String secondCommentToThirdSuggestionText = randomPrefix + "commentTwoToThird";
		Integer secondCommentToThirdSuggestionId = addCommentAndGoToSuggestionIdCommentPage(thirdSuggestionId,
				secondCommentToThirdSuggestionText);

		// now the admin of the system is using the web app
		driver.get(baseUrl);
		// the admin click the "login" link to go to login page
		driver.findElement(By.cssSelector("a[href*='/login")).click();
		driver.findElement(By.name("username")).sendKeys(USERNAME);
		driver.findElement(By.name("password")).sendKeys(PASSWORD);
		// submit login
		driver.findElement(By.name("btn_submit")).click();
		// go to "/suggestions"
		driver.findElement(By.cssSelector("a[href*='/suggestions")).click();
		// check that login worked
		assertThat(driver.getPageSource()).contains("Logged as Admin");
		// check that in the suggestions table there are the suggestions written by the
		// generic user
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains(firstSuggestionId.toString(),
				firstSuggestionText, secondSuggestionId.toString(), secondSuggestionText, thirdSuggestionId.toString(),
				thirdSuggestionText);
		// the first suggestion should be hidden so the link to hide is clicked
		driver.findElement(By.cssSelector("a[href*='/suggestions/hide/" + firstSuggestionId)).click();
		// submit hidden by clicking the button
		driver.findElement(By.name("btn_submit")).click();
		// check that the first suggestion has been moved to the hidden table
		assertThat(driver.findElement(By.id("hiddenSuggestions_table")).getText())
				.contains(firstSuggestionId.toString(), firstSuggestionText);
		// the second suggestion should be deleted so the link to delete is clicked
		driver.findElement(By.cssSelector("a[href*='/suggestions/delete/" + secondSuggestionId)).click();
		// submit deletion by clicking the button
		driver.findElement(By.name("btn_submit")).click();
		// check that the second suggestion has been removed from suggestion table and
		// is not present in the hidden table too
		assertThat(driver.findElement(By.id("suggestions_table")).getText())
				.doesNotContain(secondSuggestionId.toString(), secondSuggestionText);
		assertThat(driver.findElement(By.id("hiddenSuggestions_table")).getText())
				.doesNotContain(secondSuggestionId.toString(), secondSuggestionText);
		driver.findElement(By.cssSelector("a[href*='/suggestions/" + thirdSuggestionId + "/comments")).click();
		// check that the comments table contains the two comments
		assertThat(driver.findElement(By.id("comments_table")).getText()).contains(
				firstCommentToThirdSuggestionId.toString(), firstCommentToThirdSuggestionText,
				secondCommentToThirdSuggestionId.toString(), secondCommentToThirdSuggestionText);
		// the first comment should be deleted
		driver.findElement(By.cssSelector(
				"a[href*='/suggestions/" + thirdSuggestionId + "/delete/" + firstCommentToThirdSuggestionId)).click();
		// click the delete button
		driver.findElement(By.name("btn_submit")).click();
		// check that the comments table contains only the second comment
		assertThat(driver.findElement(By.id("comments_table")).getText())
				.contains(secondCommentToThirdSuggestionId.toString(), secondCommentToThirdSuggestionText);
		// come back to suggestions page
		driver.findElement(By.cssSelector("a[href*='/suggestions")).click();
		// go edit the hidden suggestion (first suggestion)
		driver.findElement(By.cssSelector("a[href*='/suggestions/edit/" + firstSuggestionId)).click();
		// fill the edit field
		String firstSuggestionModifiedSuggestionText = randomPrefix + "modifiedSuggestion";
		driver.findElement(By.name("suggestionText")).clear();
		driver.findElement(By.name("suggestionText")).sendKeys(firstSuggestionModifiedSuggestionText);
		// submit the edit
		driver.findElement(By.name("btn_submit")).click();
		// check that the suggestion state is still correct
		// the second suggestion is still not present in both the tables
		assertThat(driver.findElement(By.id("suggestions_table")).getText())
				.doesNotContain(secondSuggestionId.toString(), secondSuggestionText);
		assertThat(driver.findElement(By.id("hiddenSuggestions_table")).getText())
				.doesNotContain(secondSuggestionId.toString(), secondSuggestionText);
		// the first suggestion is hidden and edited
		assertThat(driver.findElement(By.id("hiddenSuggestions_table")).getText())
				.contains(firstSuggestionId.toString(), firstSuggestionModifiedSuggestionText);
		// the third suggestion is as it was
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains(thirdSuggestionId.toString(),
				thirdSuggestionText);
		// go show the hidden suggestion (first suggestion)
		driver.findElement(By.cssSelector("a[href*='/suggestions/hide/" + firstSuggestionId)).click();
		// submit "show suggestion"
		driver.findElement(By.name("btn_submit")).click();
		// check that the suggestion state is still correct
		// the second suggestion is still not present in the suggestion table
		assertThat(driver.findElement(By.id("suggestions_table")).getText())
				.doesNotContain(secondSuggestionId.toString(), secondSuggestionText);
		// the first suggestion is showed and edited, the third is as it was
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains(firstSuggestionId.toString(),
				firstSuggestionModifiedSuggestionText, thirdSuggestionId.toString(), thirdSuggestionText);
	}

	private int addSuggestionAndGoToSuggestionPage(String newSuggestion) {
		// going to the "/suggestions/new" page
		driver.get(newSuggestionUrl);

		// fill the form and click the save button
		driver.findElement(By.name("suggestionText")).sendKeys(newSuggestion);
		driver.findElement(By.name("btn_submit")).click();

		// go to "/suggestions" page
		driver.get(suggestionsUrl);

		// find the new suggestion id in order to return it
		String suggestionTableText = driver.findElement(By.id("suggestions_table")).getText();
		suggestionTableText = suggestionTableText.replace(newSuggestion, "");
		LinkedList<Integer> ids = new LinkedList<Integer>();
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(suggestionTableText);
		while (m.find()) {
			int newInt = Integer.parseInt(m.group());
			ids.add(newInt);
		}

		// returning the id of the new suggestion saved
		return ids.getLast();
	}

	private int addCommentAndGoToSuggestionIdCommentPage(int suggestionId, String commentText) {
		// going to "/suggestions/{id}/comments/newComment"
		driver.get(suggestionsUrlSlash + suggestionId + "/newComment");

		// fill the form and click save
		driver.findElement(By.name("commentText")).sendKeys(commentText);
		driver.findElement(By.name("btn_save")).click();

		// going to "/suggestions/{id}/comments"
		driver.get(suggestionsUrlSlash + suggestionId + "/comments");

		// find the new comment id in order to return it
		String commentTableText = driver.findElement(By.id("comments_table")).getText();
		commentTableText = commentTableText.replace(commentText, "");
		LinkedList<Integer> ids = new LinkedList<Integer>();
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(commentTableText);
		while (m.find()) {
			int newInt = Integer.parseInt(m.group());
			ids.add(newInt);
		}
		return ids.getLast();
	}

}