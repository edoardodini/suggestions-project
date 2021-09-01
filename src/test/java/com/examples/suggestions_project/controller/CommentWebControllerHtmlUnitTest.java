package com.examples.suggestions_project.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.examples.suggestions_project.config.TestSecurityConfig;
import com.examples.suggestions_project.model.Comment;
import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.services.AuthService;
import com.examples.suggestions_project.services.CommentService;
import com.examples.suggestions_project.services.SuggestionService;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = CommentWebController.class)
@Import(TestSecurityConfig.class)
public class CommentWebControllerHtmlUnitTest {

	@Autowired
	private WebClient webClient;
	@MockBean
	private SuggestionService suggestionService;
	@MockBean
	private CommentService commentService;
	@MockBean
	private AuthService authService;

	@Test
	public void testCommentsPageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/suggestions/1/comments");
		assertThat(page.getTitleText()).isEqualTo("Comments");
	}

	@Test
	public void testCommentsPageLinksWhenNoSuggestion() throws Exception {
		HtmlPage page = this.webClient.getPage("/suggestions/1/comments");
		assertThat(page.getAnchorByText("Home").getHrefAttribute()).isEqualTo("/");
		assertThat(page.getAnchorByText("Suggestions").getHrefAttribute()).isEqualTo("/suggestions");
	}

	@Test
	public void testCommentsPageLinksWhenSuggestion() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestion", true);
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		HtmlPage page = this.webClient.getPage("/suggestions/1/comments");
		assertThat(page.getAnchorByText("Home").getHrefAttribute()).isEqualTo("/");
		assertThat(page.getAnchorByText("Suggestions").getHrefAttribute()).isEqualTo("/suggestions");
		assertThat(page.getAnchorByText("New comment").getHrefAttribute()).isEqualTo("/suggestions/1/newComment");
	}

	@Test
	public void testCommentsPageUserTextWhenNoAdmin() throws Exception {
		HtmlPage page = this.webClient.getPage("/suggestions/1/comments");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("Suggestions")
				.containsOnlyOnce("Logged as generic user");
	}

	@Test
	public void testCommentsPageUserTextWhenAdmin() throws Exception {
		when(authService.isAdmin()).thenReturn(true);
		HtmlPage page = this.webClient.getPage("/suggestions/1/comments");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("Suggestions")
				.containsOnlyOnce("Logged as Admin");
		assertThat(page.getFormByName("logout_form").getButtonByName("btn_logout").asText()).isEqualTo("Logout");
	}

	@Test
	public void testCommentsPageTextWhenNoSuggestion() throws Exception {
		Suggestion suggestion = null;
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		HtmlPage page = this.webClient.getPage("/suggestions/1/comments");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("No suggestion found with suggestion id: 1");
	}

	@Test
	public void testCommentsPageTextWhenSuggestionButNoComments() throws Exception {
		String suggestionTextNotToBeFound = "suggestion text not to be found";
		Suggestion suggestion = new Suggestion(1l, suggestionTextNotToBeFound, true);
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		when(commentService.getCommentsBySuggestionId(1L)).thenReturn(emptyList());
		HtmlPage page = this.webClient.getPage("/suggestions/1/comments");
		assertThat(page.getBody().getTextContent()).doesNotContain(suggestionTextNotToBeFound)
				.containsOnlyOnce("No comment found with suggestion id: 1");
	}

	@Test
	public void testCommentsPageTextWhenGenericUserWithSuggestionAndComments() throws Exception {
		String suggestionTextToBeFound = "suggestion text to be found";
		Suggestion suggestion = new Suggestion(1l, suggestionTextToBeFound, true);
		Comment comment1 = new Comment(1L, "comment1", suggestion);
		Comment comment2 = new Comment(2L, "comment2", suggestion);
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		when(commentService.getCommentsBySuggestionId(1L)).thenReturn(asList(comment1, comment2));
		HtmlPage page = this.webClient.getPage("/suggestions/1/comments");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Suggestion: " + suggestionTextToBeFound);
		HtmlTable table = page.getHtmlElementById("comments_table");
		assertThat(removeWindowsCR(table.asText()))
				.isEqualTo("Comments\n" + "ID	Comment\n" + "1	comment1\n" + "2	comment2");
	}

	@Test
	public void testCommentsPageTextWhenAdminWithSuggestionAndComments() throws Exception {
		String suggestionTextToBeFound = "suggestion text to be found";
		Suggestion suggestion = new Suggestion(1l, suggestionTextToBeFound, true);
		Comment comment1 = new Comment(1L, "comment1", suggestion);
		Comment comment2 = new Comment(2L, "comment2", suggestion);
		when(authService.isAdmin()).thenReturn(true);
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		when(commentService.getCommentsBySuggestionId(1L)).thenReturn(asList(comment1, comment2));
		HtmlPage page = this.webClient.getPage("/suggestions/1/comments");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Suggestion: " + suggestionTextToBeFound);
		HtmlTable table = page.getHtmlElementById("comments_table");
		assertThat(removeWindowsCR(table.asText()))
				.isEqualTo("Comments\n" + "ID	Comment\n" + "1	comment1	Delete\n" + "2	comment2	Delete");
	}

	@Test
	public void testNewCommentPageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/suggestions/1/newComment");
		assertThat(page.getTitleText()).isEqualTo("Edit comment");
	}

	@Test
	public void testNewCommentsPageLinkPresent() throws Exception {
		HtmlPage page = this.webClient.getPage("/suggestions/1/newComment");
		assertThat(page.getAnchorByText("Home").getHrefAttribute()).isEqualTo("/");
	}

	@Test
	public void testNewCommentPageWhenNoSuggestion() throws Exception {
		Suggestion noSuggestion = null;
		when(suggestionService.getSuggestionById(1L)).thenReturn(noSuggestion);
		HtmlPage page = this.webClient.getPage("/suggestions/1/newComment");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("Edit comment")
				.containsOnlyOnce("No suggestion found with suggestion id: 1");
	}

	@Test
	public void testNewCommentPage() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestion", true);
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		HtmlPage page = this.webClient.getPage("/suggestions/1/newComment");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("Edit comment")
				.containsOnlyOnce("Comment: ").containsOnlyOnce("Save");
		HtmlForm form = page.getFormByName("newComment_form");
		assertThat(form.getButtonByName("btn_save").getTextContent()).isEqualTo("Save");
		assertThat(form.getInputByName("commentText").asText()).isEmpty();
	}

	@Test
	public void testCreateComment() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestion", true);
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		HtmlPage page = this.webClient.getPage("/suggestions/1/newComment");
		final HtmlForm form = page.getFormByName("newComment_form");
		form.getInputByName("commentText").setValueAttribute("comment");
		Comment commentCreated = new Comment();
		commentCreated.setCommentText("comment");
		commentCreated.setSuggestion(suggestion);
		form.getButtonByName("btn_save").click();
		verify(commentService).insertNewComment(commentCreated);
	}

	@Test
	public void testDeleteCommentPageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/suggestions/1/delete/1");
		assertThat(page.getTitleText()).isEqualTo("Delete comment");
	}

	@Test
	public void testDeleteCommentsPageLinkPresent() throws Exception {
		HtmlPage page = this.webClient.getPage("/suggestions/1/delete/1");
		assertThat(page.getAnchorByText("Home").getHrefAttribute()).isEqualTo("/");
	}

	@Test
	public void testDeleteCommentPageWhenSomethingWrong() throws Exception {
		Suggestion noSuggestion = null;
		when(suggestionService.getSuggestionById(1L)).thenReturn(noSuggestion);
		HtmlPage page1 = this.webClient.getPage("/suggestions/1/delete/1");
		assertThat(page1.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("Delete comment")
				.containsOnlyOnce("No suggestion found with suggestion id: 1");
	}

	@Test
	public void testDeleteCommentPageWhenSuggestionAndCommentExistsAndAreRelated() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestion", true);
		Comment comment = new Comment(1L, "comment", suggestion);
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		when(commentService.getCommentById(1L)).thenReturn(comment);
		HtmlPage page = this.webClient.getPage("/suggestions/1/delete/1");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("Delete comment")
				.containsOnlyOnce("Want to delete?").containsOnlyOnce("Yes");
		assertThat(page.getFormByName("delete_form").getButtonByName("btn_submit").asText()).isEqualTo("Yes");
	}

	@Test
	public void testDeleteComment() throws Exception {
		Long commentIdToDelete = 1L;
		Suggestion suggestion = new Suggestion(1L, "suggestion", true);
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		Comment comment = new Comment(commentIdToDelete, "comment", suggestion);
		when(commentService.getCommentById(1L)).thenReturn(comment);
		HtmlPage page = this.webClient.getPage("/suggestions/1/delete/1");
		final HtmlForm form = page.getFormByName("delete_form");
		form.getButtonByName("btn_submit").click();
		verify(commentService).deleteById(commentIdToDelete);
	}

	private String removeWindowsCR(String s) {
		return s.replace("\r", "");
	}

}
