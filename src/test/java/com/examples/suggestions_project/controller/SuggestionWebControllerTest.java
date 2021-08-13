package com.examples.suggestions_project.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.examples.suggestions_project.services.AuthService;
import com.examples.suggestions_project.services.SuggestionService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SuggestionWebController.class)
public class SuggestionWebControllerTest {

	@Autowired
	private MockMvc mvc;
	@MockBean
	private SuggestionService suggestionService;
	@MockBean
	private AuthService authService;

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

		when(suggestionService.getAllByVisible(true)).thenReturn(suggestions);

		mvc.perform(get("/suggestions")).andExpect(view().name("suggestionView"))
				.andExpect(model().attribute("suggestions", suggestions));
	}

	@Test
	public void testSuggestionViewShowsSuggestionsVisibleAndHiddenWhenAdmin() throws Exception {
		List<Suggestion> suggestions = asList(new Suggestion(1L, "suggestionText", true));
		List<Suggestion> hiddenSuggestions = asList(new Suggestion(2L, "hiddenSuggestionText", false));

		when(suggestionService.getAllByVisible(true)).thenReturn(suggestions);
		when(suggestionService.getAllByVisible(false)).thenReturn(hiddenSuggestions);
		when(authService.isAdmin()).thenReturn(true);

		mvc.perform(get("/suggestions")).andExpect(view().name("suggestionView"))
				.andExpect(model().attribute("suggestions", suggestions))
				.andExpect(model().attribute("hiddensuggestions", hiddenSuggestions))
				.andExpect(model().attribute("user", "admin"));
	}

	@Test
	public void testStatus200Hide() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestionText", true);
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		mvc.perform(get("/suggestions/hide/1")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testReturnHideView() throws Exception {
		Suggestion suggestion = new Suggestion(1L, "suggestionText", true);
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		ModelAndViewAssert.assertViewName(mvc.perform(get("/suggestions/hide/1")).andReturn().getModelAndView(),
				"hide");
	}

	@Test
	public void testHideViewWithSuggestionToHide() throws Exception {
		Long suggestionId = 1L;
		Suggestion suggestion = spy(new Suggestion(suggestionId, "suggestionText", true));
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);

		mvc.perform(get("/suggestions/hide/" + suggestionId)).andExpect(view().name("hide"))
				.andExpect(model().attribute("suggestion", suggestion)).andExpect(model().attribute("message", ""));
		verify(suggestion).setVisible(false);
	}

	@Test
	public void testHideViewWithSuggestionsToShow() throws Exception {
		Long suggestionId = 1L;
		Suggestion suggestion = spy(new Suggestion(suggestionId, "suggestionText", false));
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);

		mvc.perform(get("/suggestions/hide/" + suggestionId)).andExpect(view().name("hide"))
				.andExpect(model().attribute("suggestion", suggestion)).andExpect(model().attribute("message", ""));
		verify(suggestion).setVisible(true);
	}

	//Really similar to other tests, if modified check also the similar ones
	@Test
	public void testHideViewWithoutSuggestion() throws Exception {
		Long suggestionId = 1L;
		Suggestion suggestion = null;
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);

		mvc.perform(get("/suggestions/hide/" + suggestionId)).andExpect(view().name("hide"))
				.andExpect(model().attribute("suggestion", suggestion))
				.andExpect(model().attribute("message", "No suggestion found with id: " + suggestionId));
	}

	@Test
	public void testStatus200Edit() throws Exception {
		mvc.perform(get("/suggestions/edit/1")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testReturnEditView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/suggestions/edit/1")).andReturn().getModelAndView(),
				"edit");
	}

	@Test
	public void testEditViewWithSuggestionsToEdit() throws Exception {
		Long suggestionId = 1L;
		Suggestion suggestion = new Suggestion(suggestionId, "suggestionText", false);
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);

		mvc.perform(get("/suggestions/edit/" + suggestionId)).andExpect(view().name("edit"))
				.andExpect(model().attribute("suggestion", suggestion))
				.andExpect(model().attribute("operation", "update")).andExpect(model().attribute("message", ""));
	}

	//Really similar to other tests, if modified check also the similar ones
	@Test
	public void testEditViewWithoutSuggestionToEdit() throws Exception {
		Long suggestionId = 1L;
		Suggestion suggestion = null;
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);

		mvc.perform(get("/suggestions/edit/" + suggestionId)).andExpect(view().name("edit"))
				.andExpect(model().attribute("suggestion", suggestion))
				.andExpect(model().attribute("message", "No suggestion found with id: " + suggestionId));
	}

	@Test
	public void testStatus200New() throws Exception {
		mvc.perform(get("/suggestions/new")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testReturnNewView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/suggestions/new")).andReturn().getModelAndView(), "edit");
	}

	@Test
	public void testNewViewWithSuggestionsToEdit() throws Exception {
		mvc.perform(get("/suggestions/new")).andExpect(view().name("edit"))
				.andExpect(model().attribute("suggestion", new Suggestion()))
				.andExpect(model().attribute("operation", "new")).andExpect(model().attribute("message", ""));
	}

	@Test
	public void testStatus200Delete() throws Exception {
		mvc.perform(get("/suggestions/delete/1")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testReturnDeleteView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/suggestions/delete/1")).andReturn().getModelAndView(),
				"delete");
	}

	@Test
	public void testDeleteViewWithSuggestionsToDelete() throws Exception {
		Long suggestionId = 1L;
		Suggestion suggestion = new Suggestion(suggestionId, "suggestionText", false);
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);
		mvc.perform(get("/suggestions/delete/" + suggestionId)).andExpect(view().name("delete"))
				.andExpect(model().attribute("suggestion", suggestion)).andExpect(model().attribute("message", ""));
	}

	//Really similar to other tests, if modified check also the similar ones
	@Test
	public void testDeleteViewWithoutSuggestionToDelete() throws Exception {
		Long suggestionId = 1L;
		Suggestion suggestion = null;
		when(suggestionService.getSuggestionById(suggestionId)).thenReturn(suggestion);

		mvc.perform(get("/suggestions/delete/" + suggestionId)).andExpect(view().name("delete"))
				.andExpect(model().attribute("suggestion", suggestion))
				.andExpect(model().attribute("message", "No suggestion found with id: " + suggestionId));
	}

	@Test
	public void testPostSaveSuggestionShouldInsertNewEmployee() throws Exception {
		mvc.perform(post("/suggestions/save").param("suggestionText", "suggestion"))
				.andExpect(view().name("redirect:/suggestions")); // go back to the suggestions page
		Suggestion suggestionToSave=new Suggestion();
		suggestionToSave.setSuggestionText("suggestion");
		verify(suggestionService).insertNewSuggestion(suggestionToSave);
	}

	@Test
	public void testPostUpdateShouldUpdateExistingEmployee() throws Exception {
		mvc.perform(post("/suggestions/update").param("id", "2").param("suggestionText", "suggestion").param("visible", "true"))
				.andExpect(view().name("redirect:/suggestions")); // go back to the suggestions page
		verify(suggestionService).updateSuggestionById(2L, new Suggestion(2L, "suggestion", true));
	}
	
	@Test
	public void testPostDeleteSuggestionShouldInsertNewEmployee() throws Exception {
		mvc.perform(post("/suggestions/remove").param("id", "1"))
				.andExpect(view().name("redirect:/suggestions")); // go back to the suggestions page
		verify(suggestionService).deleteById(1L);
	}
}