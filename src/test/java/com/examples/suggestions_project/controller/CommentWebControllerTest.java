package com.examples.suggestions_project.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;

import com.examples.suggestions_project.config.TestSecurityConfig;
import com.examples.suggestions_project.exception.ResourceNotFoundException;
import com.examples.suggestions_project.model.Comment;
import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.services.AuthService;
import com.examples.suggestions_project.services.CommentService;
import com.examples.suggestions_project.services.SuggestionService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = CommentWebController.class)
@Import(TestSecurityConfig.class)
public class CommentWebControllerTest {

	@Autowired
	private MockMvc mvc;
	@MockBean
	private SuggestionService suggestionService;
	@MockBean
	private CommentService commentService;
	@MockBean
	private AuthService authService;

	@Test
	public void testStatus200() throws Exception {
		mvc.perform(get("/suggestions/1/comments")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testReturnCommentView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/suggestions/1/comments")).andReturn().getModelAndView(),
				"commentView");
	}

	@Test
	public void testCommentViewShowsComments() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestionText", true);
		Comment comment1 = new Comment(1L, "comment1", suggestion);
		Comment comment2 = new Comment(2L, "comment2", suggestion);
		List<Comment> comments = asList(comment1, comment2);
		Long suggestionId = 1L;
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);
		when(commentService.getCommentsBySuggestionId(suggestionId)).thenReturn(asList(comment1, comment2));
		// admin
		when(authService.isAdmin()).thenReturn(true);
		mvc.perform(get("/suggestions/1/comments")).andExpect(view().name("commentView"))
				.andExpect(model().attribute("suggestion", suggestion))
				.andExpect(model().attribute("comments", comments)).andExpect(model().attribute("user", "admin"))
				.andExpect(model().attribute("message", ""));
		// generic user
		when(authService.isAdmin()).thenReturn(false);
		mvc.perform(get("/suggestions/1/comments")).andExpect(view().name("commentView"))
				.andExpect(model().attribute("suggestion", suggestion))
				.andExpect(model().attribute("comments", comments)).andExpect(model().attribute("user", ""))
				.andExpect(model().attribute("message", ""));
	}

	@Test
	public void testCommentViewNotShowsCommentsToNotAdminBecauseSuggestionNotVisible() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestionText", false);
		Suggestion suggestionToBeReceived = null;
		Comment comment1 = new Comment(1L, "comment1", suggestion);
		Comment comment2 = new Comment(2L, "comment2", suggestion);
		List<Comment> comments = asList(comment1, comment2);
		Long suggestionId = 1L;
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);
		when(commentService.getCommentsBySuggestionId(suggestionId)).thenReturn(comments);
		when(authService.isAdmin()).thenReturn(false);
		mvc.perform(get("/suggestions/1/comments")).andExpect(view().name("commentView"))
				.andExpect(model().attribute("suggestion", suggestionToBeReceived))
				.andExpect(model().attribute("comments", Collections.emptyList()))
				.andExpect(model().attribute("user", ""))
				.andExpect(model().attribute("message", "No suggestion found with suggestion id: " + suggestionId));
	}

	@Test
	public void testCommentsViewNotShowsCommentsBecauseSuggestionNotPresent() throws Exception {
		Suggestion suggestion = null;
		List<Comment> comments = Collections.emptyList();
		Long suggestionId = 1L;
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);
		when(commentService.getCommentsBySuggestionId(suggestionId)).thenReturn(comments);
		// generic user
		when(authService.isAdmin()).thenReturn(false);
		mvc.perform(get("/suggestions/1/comments")).andExpect(view().name("commentView"))
				.andExpect(model().attribute("suggestion", suggestion))
				.andExpect(model().attribute("comments", comments)).andExpect(model().attribute("user", ""))
				.andExpect(model().attribute("message", "No suggestion found with suggestion id: " + suggestionId));
		// admin
		when(authService.isAdmin()).thenReturn(true);
		mvc.perform(get("/suggestions/1/comments")).andExpect(view().name("commentView"))
				.andExpect(model().attribute("suggestion", suggestion))
				.andExpect(model().attribute("comments", comments)).andExpect(model().attribute("user", "admin"))
				.andExpect(model().attribute("message", "No suggestion found with suggestion id: " + suggestionId));

	}

	@Test
	public void testCommentsViewNotShowsCommentsBecauseCommentsNotPresent() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestionText", true);
		List<Comment> comments = Collections.emptyList();
		Long suggestionId = 1L;
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);
		when(commentService.getCommentsBySuggestionId(suggestionId)).thenReturn(comments);
		// generic user
		when(authService.isAdmin()).thenReturn(false);
		mvc.perform(get("/suggestions/1/comments")).andExpect(view().name("commentView"))
				.andExpect(model().attribute("suggestion", suggestion))
				.andExpect(model().attribute("comments", comments)).andExpect(model().attribute("user", ""))
				.andExpect(model().attribute("message", "No comment found with suggestion id: " + suggestionId));
		// admin
		when(authService.isAdmin()).thenReturn(true);
		mvc.perform(get("/suggestions/1/comments")).andExpect(view().name("commentView"))
				.andExpect(model().attribute("suggestion", suggestion))
				.andExpect(model().attribute("comments", comments)).andExpect(model().attribute("user", "admin"))
				.andExpect(model().attribute("message", "No comment found with suggestion id: " + suggestionId));
	}

	@Test
	public void testStatus200NewComment() throws Exception {
		mvc.perform(get("/suggestions/1/newComment")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testReturnEditCommentView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/suggestions/1/newComment")).andReturn().getModelAndView(),
				"editComment");
	}

	@Test
	public void testNewCommentViewWhenSuggestionExist() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestionText", true);
		Comment newComment = new Comment();
		newComment.setSuggestion(suggestion);
		Long suggestionId = 1L;
		// when admin
		when(authService.isAdmin()).thenReturn(true);
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);
		mvc.perform(get("/suggestions/1/newComment")).andExpect(view().name("editComment"))
				.andExpect(model().attribute("suggestion", suggestion))
				.andExpect(model().attribute("comment", newComment)).andExpect(model().attribute("message", ""));
		// when not admin
		when(authService.isAdmin()).thenReturn(false);
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);
		mvc.perform(get("/suggestions/1/newComment")).andExpect(view().name("editComment"))
				.andExpect(model().attribute("suggestion", suggestion))
				.andExpect(model().attribute("comment", newComment)).andExpect(model().attribute("message", ""));
	}

	@Test
	public void testNewCommentViewWhenSuggestionNotExist() throws Exception {
		Suggestion notExistingSuggestion = null;
		Long suggestionId = 1L;
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(notExistingSuggestion);
		mvc.perform(get("/suggestions/1/newComment")).andExpect(view().name("editComment"))
				.andExpect(model().attribute("suggestion", notExistingSuggestion))
				.andExpect(model().attributeDoesNotExist("comment"))
				.andExpect(model().attribute("message", "No suggestion found with suggestion id: " + suggestionId));
	}

	@Test
	public void testNewCommentViewWhenSuggestionHidden() throws Exception {
		Suggestion notVisibleSuggestion = new Suggestion(1L, "hiddenSuggestion", false);
		Suggestion notExistingSuggestion = null;
		Long suggestionId = 1L;
		// when not admin
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(notVisibleSuggestion);
		when(authService.isAdmin()).thenReturn(false);
		mvc.perform(get("/suggestions/1/newComment")).andExpect(view().name("editComment"))
				.andExpect(model().attribute("suggestion", notExistingSuggestion))
				.andExpect(model().attributeDoesNotExist("comment"))
				.andExpect(model().attribute("message", "No suggestion found with suggestion id: " + suggestionId));
		// when admin
		Comment newComment = new Comment();
		newComment.setSuggestion(notVisibleSuggestion);
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(notVisibleSuggestion);
		when(authService.isAdmin()).thenReturn(true);
		mvc.perform(get("/suggestions/1/newComment")).andExpect(view().name("editComment"))
				.andExpect(model().attribute("suggestion", notVisibleSuggestion))
				.andExpect(model().attribute("comment", newComment)).andExpect(model().attribute("message", ""));
	}

	@Test
	public void testStatus200DeleteComment() throws Exception {
		mvc.perform(get("/suggestions/1/delete/1")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testReturnDeleteCommentView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/suggestions/1/delete/1")).andReturn().getModelAndView(),
				"deleteComment");
	}

	@Test
	public void testDeleteCommentViewWhenSuggestionAndCommentExist() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestionText", true);
		Comment comment = new Comment(2L, "comment", suggestion);
		Long suggestionId = 1L;
		Long commentId = 2L;
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);
		when(commentService.getCommentById(commentId)).thenReturn(comment);
		mvc.perform(get("/suggestions/1/delete/2")).andExpect(view().name("deleteComment"))
				.andExpect(model().attribute("suggestion", suggestion)).andExpect(model().attribute("comment", comment))
				.andExpect(model().attribute("message", ""));
	}

	@Test
	public void testDeleteCommentViewWhenSuggestionAndCommentExistButAreNotRelated() throws Exception {
		Long suggestionId = 1L;
		Long commentId = 2L;
		Suggestion suggestion = new Suggestion(suggestionId, "suggestionText", true);
		Suggestion suggestionNotRelated = new Suggestion(2L, "notRelatedSuggestion", true);
		Comment commentNotRelated = new Comment(commentId, "comment", suggestionNotRelated);
		Comment commentRelated = null;

		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);
		when(commentService.getCommentById(commentId)).thenReturn(commentNotRelated);
		mvc.perform(get("/suggestions/1/delete/2")).andExpect(view().name("deleteComment"))
				.andExpect(model().attribute("suggestion", suggestion))
				.andExpect(model().attribute("comment", commentRelated))
				.andExpect(model().attribute("message", "No comment found with comment id: " + commentId));
	}

	@Test
	public void testDeleteCommentViewWhenSuggestionExistAndCommentNot() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestionText", true);
		Comment notExistingComment = null;
		Long suggestionId = 1L;
		Long commentId = 2L;
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);
		when(commentService.getCommentById(commentId)).thenReturn(notExistingComment);
		mvc.perform(get("/suggestions/1/delete/2")).andExpect(view().name("deleteComment"))
				.andExpect(model().attribute("suggestion", suggestion))
				.andExpect(model().attribute("comment", notExistingComment))
				.andExpect(model().attribute("message", "No comment found with comment id: " + commentId));
	}

	@Test
	public void testDeleteCommentViewWhenSuggestionAndCommentNotExist() throws Exception {
		Suggestion notExistingSuggestion = null;
		Comment notExistingComment = null;
		Long suggestionId = 1L;
		Long commentId = 2L;
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(notExistingSuggestion);
		when(commentService.getCommentById(commentId)).thenReturn(notExistingComment);
		mvc.perform(get("/suggestions/1/delete/2")).andExpect(view().name("deleteComment"))
				.andExpect(model().attribute("suggestion", notExistingSuggestion))
				.andExpect(model().attribute("comment", notExistingComment))
				.andExpect(model().attribute("message", "No suggestion found with suggestion id: " + suggestionId));
	}

	@Test
	public void testPostSaveShouldSaveExistingComment() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestion", true);
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		mvc.perform(post("/suggestions/1/save").param("commentText", "comment"))
				.andExpect(view().name("redirect:/suggestions/1/comments")).andExpect(status().is3xxRedirection());
		verify(suggestionService).getSuggestionById(1L);
		verify(commentService).insertNewComment(new Comment(null, "comment", suggestion));
		verifyNoMoreInteractions(commentService);
		verifyNoMoreInteractions(suggestionService);
		// suggestion not visible but admin
		suggestion = new Suggestion(1L, "suggestion", false);
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		when(authService.isAdmin()).thenReturn(true);
		mvc.perform(post("/suggestions/1/save").param("commentText", "comment"))
				.andExpect(view().name("redirect:/suggestions/1/comments")).andExpect(status().is3xxRedirection());
		verify(suggestionService, times(2)).getSuggestionById(1L);
		verify(commentService).insertNewComment(new Comment(null, "comment", suggestion));
		verifyNoMoreInteractions(commentService);
		verifyNoMoreInteractions(suggestionService);
	}

	@Test
	public void testPostDeleteShouldDeleteExistingComment() throws Exception {
		mvc.perform(post("/suggestions/1/removeComment").param("commentId", "2"))
				.andExpect(view().name("redirect:/suggestions/1/comments")).andExpect(status().is3xxRedirection());
		verify(commentService).deleteById(2L);
		verifyNoMoreInteractions(commentService);
	}

	@Test
	public void testPostSaveThrowsExceptionBecauseSuggestionNotExisting() throws Exception {
		String exceptionMessage = "It is not possible to save a comment for suggestion with id: 1";
		Suggestion suggestionNotExisting = null;
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestionNotExisting);
		mvc.perform(post("/suggestions/1/save").param("commentText", "comment"))
				.andExpect(view().name("redirect:/errorPage"))// go to errorPage
				.andExpect(flash().attribute("message", exceptionMessage)).andExpect(status().is3xxRedirection());
		verify(suggestionService).getSuggestionById(1L);
		verifyNoMoreInteractions(commentService);
	}

	@Test
	public void testPostSaveThrowsExceptionBecauseSuggestionNotVisibleAndNotAdmin() throws Exception {
		when(authService.isAdmin()).thenReturn(false);
		when(suggestionService.getSuggestionById(1L)).thenReturn(new Suggestion(1L, "notVisibleSuggestion", false));
		mvc.perform(post("/suggestions/1/save").param("commentText", "comment"))
				.andExpect(view().name("redirect:/errorPage"))// go to errorPage
				.andExpect(
						flash().attribute("message", "It is not possible to save a comment for suggestion with id: 1"))
				.andExpect(status().is3xxRedirection());
		verifyNoMoreInteractions(commentService);
	}

	@Test
	public void testPostDeleteThrowsException() throws Exception {
		Long commentId = 2L;
		String exceptionMessage = "message";
		doThrow(new ResourceNotFoundException(exceptionMessage)).when(commentService).deleteById(commentId);

		mvc.perform(post("/suggestions/1/removeComment").param("commentId", "2"))
				.andExpect(view().name("redirect:/errorPage")).andExpect(flash().attribute("message", exceptionMessage))
				.andExpect(status().is3xxRedirection());
		verify(commentService).deleteById(commentId);
	}

}