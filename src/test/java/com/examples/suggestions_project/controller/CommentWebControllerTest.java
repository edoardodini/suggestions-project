package com.examples.suggestions_project.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;

import com.examples.suggestions_project.model.Comment;
import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.services.AuthService;
import com.examples.suggestions_project.services.CommentService;
import com.examples.suggestions_project.services.SuggestionService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = CommentWebController.class)
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
	
	
	
	
	
	
}