package com.examples.suggestions_project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.examples.suggestions_project.model.Comment;
import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.repository.CommentRepository;
import com.examples.suggestions_project.repository.SuggestionRepository;

/**
 * Some examples of tests for the web controller when running in a real web
 * container, manually using the {@link SuggestionRepository} and
 * {@link CommentRepository}.
 * 
 * The web server is started on a random port, which can be retrieved by
 * injecting in the test a {@link LocalServerPort}.
 * 
 * In tests you can't rely on fixed identifiers: use the ones returned by the
 * repository after saving (automatically generated)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CommentWebControllerIT {

	private static final String NO_COMMENT_FOUND_WITH_SUGGESTION_ID = "No comment found with suggestion id: ";
	private static final String NO_SUGGESTION_FOUND_WITH_ID = "No suggestion found with suggestion id: ";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	private static final String NO_COMMENT_FOUND_WITH_COMMENT_ID = "No comment found with comment id: ";

	@Autowired
	private SuggestionRepository suggestionRepository;
	@Autowired
	private CommentRepository commentRepository;

	@LocalServerPort
	private int port;

	private WebDriver driver;

	private String baseUrl;
	private String suggestionsUrl;
	private String loginUrl;
	private String errorUrl;
	private String suggestionsUrlSlash;

	@Before
	public void setup() {
		baseUrl = "http://localhost:" + port + "/";
		suggestionsUrl = baseUrl + "suggestions";
		suggestionsUrlSlash = suggestionsUrl + "/";
		loginUrl = baseUrl + "login";
		errorUrl = baseUrl + "errorPage";
		driver = new HtmlUnitDriver();
		// always start with an empty database
		suggestionRepository.deleteAll();
		suggestionRepository.flush();
		commentRepository.deleteAll();
		commentRepository.flush();
	}

	@After
	public void teardown() {
		driver.quit();
	}

	@Test
	public void testCommentsPageLinks() {
		// go to "/suggestions/{id}/comments": the comments of a suggestion that does
		// not exist
		driver.get(suggestionsUrlSlash + 1 + "/comments");
		// go to "/"
		driver.findElement(By.linkText("Home")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl);
		// go to "/suggestions/{id}/comments": the comments of a suggestion that does
		// not exist
		driver.get(suggestionsUrlSlash + 1 + "/comments");
		// go to "/suggestions"
		driver.findElement(By.linkText("Suggestions")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(suggestionsUrl);
		Suggestion sugg = suggestionRepository.save(new Suggestion(null, "suggestion1", true));
		// go to "/suggestions/{id}/comments": the comments of a suggestion that exists
		driver.get(suggestionsUrlSlash + sugg.getId() + "/comments");
		// go to "/suggestion/{id}/newComment"
		driver.findElement(By.linkText("New comment")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(suggestionsUrlSlash + sugg.getId() + "/newComment");
		Comment comm = commentRepository.save(new Comment(null, "comment1", sugg));
		// perform the login
		adminLogin();
		// go to "/suggestions/{id}/comments": the comments of a suggestion that exists
		driver.get(suggestionsUrlSlash + sugg.getId() + "/comments");
		// go to "/suggestions/{sugg.id}/delete/{comm.id}"
		driver.findElement(By.linkText("Delete")).click();
		assertThat(driver.getCurrentUrl())
				.isEqualTo(suggestionsUrlSlash + sugg.getId() + "/delete/" + comm.getCommentId());
	}

	@Test
	public void testCommentsPageLogoutAdmin() {
		// login as admin
		adminLogin();
		// go to "/suggestions/{id}/comments"
		driver.get(suggestionsUrlSlash + 1 + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Comments", "Logged as Admin", "Logout");
		driver.findElement(By.name("btn_logout")).click();
		// go to "/suggestions/{id}/comments"
		driver.get(suggestionsUrlSlash + 1 + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Comments", "Logged as generic user");
	}

	@Test
	public void testCommentsPageNotAdmin() {
		int suggestionId = 1;
		// go to "/suggestions/1/comments" the suggestion 1 does not exist
		driver.get(suggestionsUrlSlash + suggestionId + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "Logged as generic user",
				NO_SUGGESTION_FOUND_WITH_ID + suggestionId);
		assertThat(driver.getPageSource()).doesNotContain("New comment", "Delete");
		// created two suggestions: one visible, one hidden
		Suggestion testSuggestionVisible = suggestionRepository.save(new Suggestion(null, "test suggestion", true));
		Suggestion testSuggestionNotVisible = suggestionRepository
				.save(new Suggestion(null, "not_visible_suggestion", false));
		// go to "/suggestions/{testSuggestionVisible.id}/comments"
		driver.get(suggestionsUrlSlash + testSuggestionVisible.getId() + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New comment", "Logged as generic user",
				NO_COMMENT_FOUND_WITH_SUGGESTION_ID + testSuggestionVisible.getId());
		// the "Comments" link is present with href containing /suggestions/{id}
		driver.findElement(
				By.cssSelector("a[href*='/suggestions/" + testSuggestionVisible.getId() + "/newComment" + "']"));
		assertThat(driver.getPageSource()).doesNotContain("Delete", testSuggestionNotVisible.getSuggestionText());
		// created a comment to the visible suggestion
		Comment comm = commentRepository.save(new Comment(null, "comment1", testSuggestionVisible));
		// go to "/suggestions/{testSuggestionVisible.id}/comments"
		driver.get(suggestionsUrlSlash + testSuggestionVisible.getId() + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New comment", "Logged as generic user",
				"Suggestion:", testSuggestionVisible.getSuggestionText());
		assertThat(driver.findElement(By.id("comments_table")).getText()).contains("Comments", "ID", "Comment",
				comm.getCommentId().toString(), comm.getCommentText());
		assertThat(driver.getPageSource()).doesNotContain("Delete", testSuggestionNotVisible.getSuggestionText());
		// go to "/suggestions/{testSuggestionVisible.id}/comments"
		driver.get(suggestionsUrlSlash + testSuggestionNotVisible.getId() + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "Logged as generic user",
				NO_SUGGESTION_FOUND_WITH_ID + testSuggestionNotVisible.getId());
		assertThat(driver.getPageSource()).doesNotContain("New comment", "Delete",
				testSuggestionNotVisible.getSuggestionText());
	}

	@Test
	public void testCommentsPageAdmin() {
		// login as admin
		adminLogin();
		// go to "/suggestions/{id}/comments"
		int suggestionId = 1;
		driver.get(suggestionsUrlSlash + suggestionId + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "Logged as Admin", "Logout",
				NO_SUGGESTION_FOUND_WITH_ID + suggestionId);
		Suggestion testSuggestionVisible = suggestionRepository.save(new Suggestion(null, "test suggestion", true));
		Suggestion testSuggestionNotVisible = suggestionRepository.save(new Suggestion(null, "not visible", false));
		// go to "/suggestions/{testSuggestionVisible.id}/comments"
		driver.get(suggestionsUrlSlash + testSuggestionVisible.getId() + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New comment", "Logged as Admin", "Logout",
				NO_COMMENT_FOUND_WITH_SUGGESTION_ID + testSuggestionVisible.getId());
		// the "Comments" link is present with href containing /suggestions/{id}
		driver.findElement(
				By.cssSelector("a[href*='/suggestions/" + testSuggestionVisible.getId() + "/newComment" + "']"));
		assertThat(driver.getPageSource()).doesNotContain("Delete", testSuggestionNotVisible.getSuggestionText());
		// go to "/suggestions/{testSuggestionNotVisible.id}/comments"
		driver.get(suggestionsUrlSlash + testSuggestionNotVisible.getId() + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New comment", "Logged as Admin", "Logout",
				NO_COMMENT_FOUND_WITH_SUGGESTION_ID + testSuggestionNotVisible.getId());
		// the "Comments" link is present with href containing /suggestions/{id}
		driver.findElement(
				By.cssSelector("a[href*='/suggestions/" + testSuggestionNotVisible.getId() + "/newComment" + "']"));
		assertThat(driver.getPageSource()).doesNotContain("Delete", testSuggestionNotVisible.getSuggestionText());

		// created a comment to the visible suggestion and to the not visible suggestion
		Comment commVisible = commentRepository.save(new Comment(null, "comment1", testSuggestionVisible));
		Comment commNotVisible = commentRepository.save(new Comment(null, "comment2", testSuggestionNotVisible));
		// go to "/suggestions/{testSuggestionVisible.id}/comments"
		driver.get(suggestionsUrlSlash + testSuggestionVisible.getId() + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New comment", "Logged as Admin",
				"Suggestion:", testSuggestionVisible.getSuggestionText());
		assertThat(driver.findElement(By.id("comments_table")).getText()).contains("Comments", "ID", "Comment",
				commVisible.getCommentId().toString(), commVisible.getCommentText(), "Delete");
		assertThat(driver.getPageSource()).doesNotContain(commNotVisible.getCommentText());
		driver.findElement(
				By.cssSelector("a[href*='/suggestions/" + testSuggestionVisible.getId() + "/newComment" + "']"));
		driver.findElement(By.cssSelector("a[href*='/suggestions/" + testSuggestionVisible.getId() + "/delete/"
				+ commVisible.getCommentId() + "']"));
		// go to "/suggestions/{testSuggestionNotVisible.id}/comments"
		driver.get(suggestionsUrlSlash + testSuggestionNotVisible.getId() + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New comment", "Logged as Admin",
				"Suggestion: ", testSuggestionNotVisible.getSuggestionText());
		assertThat(driver.findElement(By.id("comments_table")).getText()).contains("Comments", "ID", "Comment",
				commNotVisible.getCommentId().toString(), commNotVisible.getCommentText(), "Delete");
		assertThat(driver.getPageSource()).doesNotContain(commVisible.getCommentText());
		driver.findElement(
				By.cssSelector("a[href*='/suggestions/" + testSuggestionNotVisible.getId() + "/newComment" + "']"));
		driver.findElement(By.cssSelector("a[href*='/suggestions/" + testSuggestionNotVisible.getId() + "/delete/"
				+ commNotVisible.getCommentId() + "']"));
	}

	@Test
	public void testNewCommentsPageLinks() {
		// go to "/suggestions/{id}/newComment"
		driver.get(suggestionsUrlSlash + 1 + "/newComment");
		// go to "/"
		driver.findElement(By.linkText("Home")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl);
	}

	@Test
	public void testNewCommentPageNotAdmin() {
		int suggestionId = 1;
		// go to "/suggestions/{id}/newComment"
		driver.get(suggestionsUrlSlash + suggestionId + "/newComment");
		assertThat(driver.getPageSource()).contains("Home", "Edit comment", NO_SUGGESTION_FOUND_WITH_ID + 1);
		assertThat(driver.getPageSource()).doesNotContain("Comment:", "Save");
		Suggestion suggestionNotVisible = suggestionRepository
				.save(new Suggestion(null, "suggestion not visible", false));
		// go to "/suggestions/{id}/newComment"
		driver.get(suggestionsUrlSlash + suggestionNotVisible.getId() + "/newComment");
		assertThat(driver.getPageSource()).contains("Home", "Edit comment",
				NO_SUGGESTION_FOUND_WITH_ID + suggestionNotVisible.getId());
		assertThat(driver.getPageSource()).doesNotContain("Comment:", "Save");
		Suggestion suggestionVisible = suggestionRepository.save(new Suggestion(null, "suggestion visible", true));
		// go to "/suggestions/{id}/newComment"
		driver.get(suggestionsUrlSlash + suggestionVisible.getId() + "/newComment");
		assertThat(driver.getPageSource()).contains("Home", "Edit comment", "Comment:", "Save");
		driver.findElement(By.name("commentText")).sendKeys("firstComment1");
		driver.findElement(By.name("btn_save")).click();
		// go to "/suggestions/{id}/comments"
		driver.get(suggestionsUrlSlash + suggestionVisible.getId() + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New comment", "Logged as generic user",
				"Suggestion:", suggestionVisible.getSuggestionText());
		assertThat(driver.findElement(By.id("comments_table")).getText()).contains("ID", "Comments", "Comment",
				"firstComment1");
		assertThat(commentRepository.findAll().get(0).getCommentText()).isEqualTo("firstComment1");
	}

	@Test
	public void testNewCommentPageAdmin() {
		int suggestionId = 1;
		// go to "/suggestions/{id}/newComment"
		driver.get(suggestionsUrlSlash + suggestionId + "/newComment");
		assertThat(driver.getPageSource()).contains("Home", "Edit comment", NO_SUGGESTION_FOUND_WITH_ID + 1);
		assertThat(driver.getPageSource()).doesNotContain("Comment:", "Save");
		Suggestion suggestionNotVisible = suggestionRepository
				.save(new Suggestion(null, "suggestion not visible", false));
		adminLogin();
		// go to "/suggestions/{id}/newComment"
		driver.get(suggestionsUrlSlash + suggestionNotVisible.getId() + "/newComment");
		assertThat(driver.getPageSource()).contains("Home", "Edit comment", "Comment:", "Save");
		driver.findElement(By.name("commentText")).sendKeys("firstComment1");
		driver.findElement(By.name("btn_save")).click();
		// go to "/suggestions/{id}/comments"
		driver.get(suggestionsUrlSlash + suggestionNotVisible.getId() + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New comment", "Logged as Admin", "Logout",
				"Suggestion: ", suggestionNotVisible.getSuggestionText());
		assertThat(driver.findElement(By.id("comments_table")).getText()).contains("ID", "Comments", "Comment",
				"firstComment1");
		assertThat(commentRepository.findAll().get(0).getCommentText()).isEqualTo("firstComment1");
	}

	@Test
	public void testDeleteCommentsPageLinks() {
		// go to "/suggestions/{sugg.id}/delete/{comm.id}"
		driver.get(suggestionsUrlSlash + 1 + "/delete/" + 1);
		// go to "/"
		driver.findElement(By.linkText("Home")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl);
	}

	@Test
	public void testDeleteCommentPageAdmin() {
		adminLogin();
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestionText", true));
		Comment comment = commentRepository.save(new Comment(null, "commentText", suggestion));
		// go to "/suggestions/{sugg.id}/comments" to check that the suggestion is
		// present
		driver.get(suggestionsUrlSlash + suggestion.getId() + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New comment", "Logged as Admin", "Logout",
				"Suggestion:", suggestion.getSuggestionText());
		assertThat(driver.findElement(By.id("comments_table")).getText()).contains("ID", "Comments", "Comment",
				comment.getCommentId().toString(), comment.getCommentText(), "Comments", "Delete");
		// go to "/suggestions/{sugg.id}/delete/{comm.id}"
		driver.findElement(By.linkText("Delete")).click();
		assertThat(driver.getPageSource()).contains("Home", "Delete comment", "Want to delete?", "Yes");
		// delete by clicking the button
		assertThat(commentRepository.findAll().size()).isEqualTo(1);
		driver.findElement(By.name("btn_submit")).click();
		assertThat(commentRepository.findAll().size()).isZero();
		// go to "/suggestions/{sugg.id}/comments" to check that the suggestion has been
		// deleted
		driver.get(suggestionsUrlSlash + suggestion.getId() + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New comment", "Logged as Admin", "Logout",
				NO_COMMENT_FOUND_WITH_SUGGESTION_ID);
	}

	@Test
	public void testAdminPageWithNotAdminUser() {
		// go to "/suggestions/{sugg.id}/delete/{comm.id}"
		driver.get(suggestionsUrlSlash + 1 + "/delete/" + 1);
		// go to "/"
		assertThat(driver.getCurrentUrl()).isEqualTo(loginUrl);
	}

	@Test
	public void testDeleteSuggestionWithIdOfANotExistingSuggestion() {
		int idSuggestion = 1;
		int idComment = 1;
		adminLogin();
		// go to "/suggestions/{sugg.id}/comments"
		driver.get(suggestionsUrlSlash + idSuggestion + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "Logged as Admin",
				NO_SUGGESTION_FOUND_WITH_ID + idSuggestion, "Logout");
		// go to "/suggestions/{sugg.id}/delete/{comm.id}"
		driver.get(suggestionsUrlSlash + idSuggestion + "/delete/" + idComment);
		assertThat(driver.getPageSource()).contains("Home", "Delete comment",
				NO_SUGGESTION_FOUND_WITH_ID + idSuggestion);
		// creation of a suggestion
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestion", true));
		// go to "/suggestions/{sugg.id}/comments"
		driver.get(suggestionsUrlSlash + suggestion.getId() + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New comment", "Logged as Admin",
				suggestion.getId().toString(), suggestion.getSuggestionText(), "Logout");
		// go to "/suggestions/{sugg.id}/delete/{comm.id}"
		driver.get(suggestionsUrlSlash + suggestion.getId() + "/delete/" + idComment);
		assertThat(driver.getPageSource()).contains("Home", "Delete comment",
				NO_COMMENT_FOUND_WITH_COMMENT_ID + idComment);
	}

	@Test
	public void testNewCommentButSuggestionNoMorePresent() {
		// creation of a second web driver to act as another user
		WebDriver driver2 = new HtmlUnitDriver();
		adminLogin();
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestion", true));
		// go to "/suggestions"
		driver.get(suggestionsUrl);
		assertThat(driver.getPageSource()).contains("Home", "New suggestion", "Logged as Admin", "Logout");
		assertThat(driver.findElement(By.id("suggestions_table")).getText()).contains("ID", "Suggestions",
				suggestion.getId().toString(), suggestion.getSuggestionText());
		// go to "/suggestions/{sugg.id}/newComment"
		driver.get(suggestionsUrlSlash + suggestion.getId() + "/newComment");
		// fill the form
		driver.findElement(By.name("commentText")).sendKeys("new");

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

		// user 1 try to edit the comment but the suggestion does not exist anymore
		// submit the edit
		driver.findElement(By.name("btn_save")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(errorUrl);
		assertThat(driver.getPageSource()).contains("Error", "Home",
				"It is not possible to save a comment for suggestion with id:", suggestion.getId().toString());
	}

	@Test
	public void testDeleteCommentButNoSuggestionNeitherCommentToDelete() {
		// creation of a second web driver to act as another user
		WebDriver driver2 = new HtmlUnitDriver();
		adminLogin();
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestion", true));
		Comment comment = commentRepository.save(new Comment(null, "comment", suggestion));
		// go to "/suggestions/{id}/comments"
		driver.get(suggestionsUrlSlash + suggestion.getId() + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New comment", "Logged as Admin",
				"Suggestion:", suggestion.getSuggestionText(), "Logout");
		assertThat(driver.findElement(By.id("comments_table")).getText()).contains("ID", "Comments", "Comment",
				comment.getCommentId().toString(), comment.getCommentText());
		// go to "/suggestions/{sugg.id}/delete/{comm.id}"
		driver.get(suggestionsUrlSlash + suggestion.getId() + "/delete/" + comment.getCommentId());

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
		assertThat(commentRepository.findAll().size()).isEqualTo(1);
		driver2.findElement(By.name("btn_submit")).click();
		assertThat(suggestionRepository.findAll().size()).isZero();
		assertThat(commentRepository.findAll().size()).isZero();
		driver2.quit();

		// user 1 try to delete the comment but the suggestion does not exist anymore
		// submit the edit
		driver.findElement(By.name("btn_submit")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(errorUrl);
		assertThat(driver.getPageSource()).contains("Error", "Home", "It is not possible to delete a comment with id:",
				comment.getCommentId().toString());
	}

	@Test
	public void testDeleteCommentButNoCommentToDelete() {
		// creation of a second web driver to act as another user
		WebDriver driver2 = new HtmlUnitDriver();
		adminLogin();
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestion", true));
		Comment comment = commentRepository.save(new Comment(null, "comment", suggestion));
		// go to "/suggestions/{id}/comments"
		driver.get(suggestionsUrlSlash + suggestion.getId() + "/comments");
		assertThat(driver.getPageSource()).contains("Home", "Suggestions", "New comment", "Logged as Admin",
				"Suggestion:", suggestion.getSuggestionText(), "Logout");
		assertThat(driver.findElement(By.id("comments_table")).getText()).contains("ID", "Comments", "Comment",
				comment.getCommentId().toString(), comment.getCommentText());
		// go to "/suggestions/{sugg.id}/delete/{comm.id}"
		driver.get(suggestionsUrlSlash + suggestion.getId() + "/delete/" + comment.getCommentId());

		// user number 2 log himself
		// go to login page
		driver2.get(loginUrl);
		// fill the form
		driver2.findElement(By.name("username")).sendKeys(USERNAME);
		driver2.findElement(By.name("password")).sendKeys(PASSWORD);
		// submit login
		driver2.findElement(By.name("btn_submit")).click();
		// go to "suggestions/{sugg.id}/comments"
		driver2.get(suggestionsUrlSlash + suggestion.getId() + "/comments");
		// go to "suggestions/{sugg.id}/delete/{comm.id}"
		driver2.findElement(By.linkText("Delete")).click();
		// delete by clicking the button
		assertThat(suggestionRepository.findAll().size()).isEqualTo(1);
		assertThat(commentRepository.findAll().size()).isEqualTo(1);
		driver2.findElement(By.name("btn_submit")).click();
		assertThat(suggestionRepository.findAll().size()).isEqualTo(1);
		assertThat(commentRepository.findAll().size()).isZero();
		driver2.quit();

		// user 1 try to delete the comment but the suggestion does not exist anymore
		// submit the edit
		driver.findElement(By.name("btn_submit")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(errorUrl);
		assertThat(driver.getPageSource()).contains("Error", "Home", "It is not possible to delete a comment with id:",
				comment.getCommentId().toString());
	}
	
	@Test
	public void testDeleteCommentButNoMoreAdminSoLoginPage() {
		adminLogin();
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "suggestion", true));
		Comment comment = commentRepository.save(new Comment(null, "comment", suggestion));
		// go to "/suggestions/{sugg.id}/delete/{comm.id}"
		driver.get(suggestionsUrlSlash + suggestion.getId() + "/delete/" + comment.getCommentId());
		// the user delete all cookies so it is no more admin
		driver.manage().deleteAllCookies();
		// delete the comment but no more admin so action not performed and go to login
		driver.findElement(By.name("btn_submit")).click();
		assertThat(driver.getCurrentUrl()).isEqualTo(loginUrl);
		driver.get(suggestionsUrlSlash + suggestion.getId() + "/comments");
		// the comment has not been deleted
		assertThat(driver.findElement(By.id("comments_table")).getText()).contains("ID", "Comments", "Comment",
				comment.getCommentId().toString(), comment.getCommentText());
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
