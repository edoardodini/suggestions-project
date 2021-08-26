package com.examples.suggestions_project.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static java.util.Arrays.asList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.examples.suggestions_project.config.TestSecurityConfig;
import com.examples.suggestions_project.model.Comment;
import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.services.CommentService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = CommentRestController.class)
@Import(TestSecurityConfig.class)
public class CommentRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private CommentService commentService;

	@Test
	public void testAllCommentsEmpty() throws Exception {
		this.mvc.perform(get("/api/suggestions/1/comments").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().json("[]"));
		// the above checks that the content is an empty JSON list
	}

	@Test
	public void testAllSuggestionsNotEmpty() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestionVisible", true);
		when(commentService.getCommentsBySuggestionId(1L))
				.thenReturn(asList(new Comment(1L, "first", suggestion), new Comment(2L, "second", suggestion)));
		this.mvc.perform(get("/api/suggestions/1/comments").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].commentId", is(1)))
				.andExpect(jsonPath("$[0].commentText", is("first"))).andExpect(jsonPath("$[0].suggestion.id", is(1)))
				.andExpect(jsonPath("$[0].suggestion.suggestionText", is("suggestionVisible")))
				.andExpect(jsonPath("$[0].suggestion.visible", is(true))).andExpect(jsonPath("$[1].commentId", is(2)))
				.andExpect(jsonPath("$[1].commentText", is("second"))).andExpect(jsonPath("$[1].suggestion.id", is(1)))
				.andExpect(jsonPath("$[1].suggestion.suggestionText", is("suggestionVisible")))
				.andExpect(jsonPath("$[1].suggestion.visible", is(true)));
	}

	@Test
	public void testAllSuggestionsNotEmptyButSuggestionNotVisible() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestionVisible", false);
		when(commentService.getCommentsBySuggestionId(1L))
				.thenReturn(asList(new Comment(1L, "first", suggestion), new Comment(2L, "second", suggestion)));
		this.mvc.perform(get("/api/suggestions/1/comments").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().json("[]"));
	}
}
