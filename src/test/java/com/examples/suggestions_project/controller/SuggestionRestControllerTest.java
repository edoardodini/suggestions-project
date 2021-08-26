package com.examples.suggestions_project.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.services.SuggestionService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SuggestionRestController.class)
@Import(TestSecurityConfig.class)
public class SuggestionRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private SuggestionService suggestionService;

	@Test
	public void testAllSuggestionsEmpty() throws Exception {
		this.mvc.perform(get("/api/suggestions").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().json("[]"));
		// the above checks that the content is an empty JSON list
	}

	@Test
	public void testAllSuggestionsNotEmpty() throws Exception {
		when(suggestionService.getAllByVisible(true))
				.thenReturn(asList(new Suggestion(1L, "first", true), new Suggestion(2L, "second", true)));
		this.mvc.perform(get("/api/suggestions").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(1))).andExpect(jsonPath("$[0].suggestionText", is("first")))
				.andExpect(jsonPath("$[0].visible", is(true))).andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].suggestionText", is("second"))).andExpect(jsonPath("$[1].visible", is(true)));
	}

	@Test
	public void testOneSuggestionsEmpty() throws Exception {
		this.mvc.perform(get("/api/suggestions/1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string(""));
	}

	@Test
	public void testOneSuggestionsNotEmpty() throws Exception {
		when(suggestionService.getSuggestionByIdAndVisible(1, true)).thenReturn(new Suggestion(1L, "first", true));
		this.mvc.perform(get("/api/suggestions/1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1))).andExpect(jsonPath("$.suggestionText", is("first")))
				.andExpect(jsonPath("$.visible", is(true)));
	}
}
