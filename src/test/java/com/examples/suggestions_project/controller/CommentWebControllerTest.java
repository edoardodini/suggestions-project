package com.examples.suggestions_project.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

	@Test
	public void testStatus200() throws Exception {
		mvc.perform(get("/suggestions/1/comments")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testReturnHomeView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/suggestions/1/comments")).andReturn().getModelAndView(),
				"commentView");
	}

	@Test
	public void testSuggestionViewShowsSuggestions() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestionText", true);
		Comment comment1 = new Comment(1L, "comment1", suggestion);
		Comment comment2 = new Comment(2L, "comment2", suggestion);
		List<Comment> comments = asList(comment1, comment2);
		Long suggestionId = 1L;
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);
		when(commentService.getCommentsBySuggestionId(suggestionId)).thenReturn(asList(comment1, comment2));
		mvc.perform(get("/suggestions/1/comments")).andExpect(view().name("commentView"))
				.andExpect(model().attribute("suggestion", suggestion)).andExpect(model().attribute("comments", comments));
	}
}