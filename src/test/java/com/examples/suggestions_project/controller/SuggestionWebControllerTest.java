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

import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.services.SuggestionService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SuggestionWebController.class)
public class SuggestionWebControllerTest {

	@Autowired
	private MockMvc mvc;
	@MockBean
	SuggestionService suggestionService;

	@Test
	public void testStatus200() throws Exception {
		mvc.perform(get("/")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testReturnHomeView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/")).andReturn().getModelAndView(), "home");
	}

	@Test
	public void testStatus200Suggestions() throws Exception {
		mvc.perform(get("/suggestions")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testReturnSuggestionView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/suggestions")).andReturn().getModelAndView(),
				"suggestionView");
	}

	@Test
	public void testSuggestionViewShowsSuggestions() throws Exception {
		List<Suggestion> suggestions = asList(new Suggestion(1L, "suggestionText", true));

		when(suggestionService.getAllSuggestionsByVisible(true)).thenReturn(suggestions);

		mvc.perform(get("/suggestions")).andExpect(view().name("index"))
				.andExpect(model().attribute("employees", suggestions));
	}
}