package com.examples.suggestions_project.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static java.util.Arrays.*;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.services.AuthService;
import com.examples.suggestions_project.services.SuggestionService;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SuggestionWebController.class)
public class SuggestionWebControllerHtmlUnitTest {

	@Autowired
	private WebClient webClient;
	@MockBean
	private SuggestionService suggestionService;
	@MockBean
	private AuthService authService;

	@Test
	public void testHomePageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/");
		assertThat(page.getTitleText()).isEqualTo("Suggestions home");
	}

	@Test
	public void testHomePageTexts() throws Exception {
		HtmlPage page = this.webClient.getPage("/");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Admin section")
				.containsOnlyOnce("Click here to login as admin.").containsOnlyOnce("Click here to login as admin.")
				.containsOnlyOnce("User section").containsOnlyOnce("Click here to go in suggestions section");
		assertThat(page.getAnchors().get(0).getHrefAttribute()).isEqualTo("/login");
		assertThat(page.getAnchors().get(1).getHrefAttribute()).isEqualTo("/suggestions");
	}

	@Test
	public void testSuggestionsPageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/suggestions");
		assertThat(page.getTitleText()).isEqualTo("Suggestions");
	}

	@Test
	public void testSuggestionsPageWithNoSuggestionsNoAdmin() throws Exception {
		when(suggestionService.getAllByVisible(true)).thenReturn(Collections.emptyList());
		when(authService.isAdmin()).thenReturn(false);
		HtmlPage page = this.webClient.getPage("/suggestions");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("New suggestion")
				.containsOnlyOnce("Logged as generic user").containsOnlyOnce("No suggestions");
		assertThat(page.getAnchors().get(0).getHrefAttribute()).isEqualTo("/");
		assertThat(page.getAnchors().get(1).getHrefAttribute()).isEqualTo("/suggestions/new");
		verify(suggestionService).getAllByVisible(true);
		verify(authService).isAdmin();
		verifyNoMoreInteractions(suggestionService);
		verifyNoMoreInteractions(authService);
	}

	@Test
	public void testSuggestionsPageWithNoSuggestionsAdmin() throws Exception {
		when(suggestionService.getAllByVisible(true)).thenReturn(Collections.emptyList());
		when(suggestionService.getAllByVisible(false)).thenReturn(Collections.emptyList());
		when(authService.isAdmin()).thenReturn(true);
		HtmlPage page = this.webClient.getPage("/suggestions");
		HtmlForm form = page.getFormByName("logout_form");
		// this button is not testable here in a unit test, this test check if the
		// button exists and has the correct text
		assertThat(form.getButtonByName("btn_logout").getTextContent()).isEqualTo("Logout");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("New suggestion")
				.containsOnlyOnce("Logged as Admin").containsOnlyOnce("Logout").containsOnlyOnce("No suggestions")
				.containsOnlyOnce("No hidden suggestions");
		assertThat(page.getAnchors().get(0).getHrefAttribute()).isEqualTo("/");
		assertThat(page.getAnchors().get(1).getHrefAttribute()).isEqualTo("/suggestions/new");
		verify(suggestionService).getAllByVisible(true);
		verify(suggestionService).getAllByVisible(false);
		verify(authService).isAdmin();
		verifyNoMoreInteractions(suggestionService);
		verifyNoMoreInteractions(authService);
	}

	@Test
	public void testSuggestionsPageWithSuggestionsNoAdmin() throws Exception {
		Suggestion suggestion1 = new Suggestion(1L, "suggestion1", true);
		Suggestion suggestion2 = new Suggestion(2L, "suggestion2", true);
		when(suggestionService.getAllByVisible(true)).thenReturn(asList(suggestion1, suggestion2));
		when(authService.isAdmin()).thenReturn(false);
		HtmlPage page = this.webClient.getPage("/suggestions");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("New suggestion")
				.containsOnlyOnce("Logged as generic user").doesNotContain("No suggestions");
		HtmlTable table = page.getHtmlElementById("suggestions_table");
		assertThat(removeWindowsCR(table.asText())).isEqualTo(
				"Suggestions\n" + "ID	Suggestion\n" + "1	suggestion1	Comments\n" + "2	suggestion2	Comments");
		assertThat(page.getAnchors().get(0).getHrefAttribute()).isEqualTo("/");
		assertThat(page.getAnchors().get(1).getHrefAttribute()).isEqualTo("/suggestions/new");
		verify(suggestionService).getAllByVisible(true);
		verify(authService).isAdmin();
		verifyNoMoreInteractions(suggestionService);
		verifyNoMoreInteractions(authService);
	}

	@Test
	public void testSuggestionsPageWithSuggestionsNoHiddenAdmin() throws Exception {
		Suggestion suggestion1 = new Suggestion(1L, "suggestion1", true);
		Suggestion suggestion2 = new Suggestion(2L, "suggestion2", true);
		when(suggestionService.getAllByVisible(true)).thenReturn(asList(suggestion1, suggestion2));
		when(suggestionService.getAllByVisible(false)).thenReturn(Collections.emptyList());
		when(authService.isAdmin()).thenReturn(true);
		HtmlPage page = this.webClient.getPage("/suggestions");
		HtmlForm form = page.getFormByName("logout_form");
		// this button is not testable here in a unit test, this test check if the
		// button exists and has the correct text
		assertThat(form.getButtonByName("btn_logout").getTextContent()).isEqualTo("Logout");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("New suggestion")
				.containsOnlyOnce("Logged as Admin").containsOnlyOnce("Logout").doesNotContain("No suggestions")
				.containsOnlyOnce("No hidden suggestions");
		assertThat(page.getAnchors().get(0).getHrefAttribute()).isEqualTo("/");
		assertThat(page.getAnchors().get(1).getHrefAttribute()).isEqualTo("/suggestions/new");
		HtmlTable table = page.getHtmlElementById("suggestions_table");
		assertThat(removeWindowsCR(table.asText())).isEqualTo(
				"Suggestions\n" + "ID	Suggestion\n" + "1	suggestion1	Comments	Edit	Hide	Delete\n"
						+ "2	suggestion2	Comments	Edit	Hide	Delete");
		assertThat(page.getAnchors().get(0).getHrefAttribute()).isEqualTo("/");
		assertThat(page.getAnchors().get(1).getHrefAttribute()).isEqualTo("/suggestions/new");
		verify(suggestionService).getAllByVisible(true);
		verify(suggestionService).getAllByVisible(false);
		verify(authService).isAdmin();
		verifyNoMoreInteractions(suggestionService);
		verifyNoMoreInteractions(authService);
	}

	@Test
	public void testSuggestionsPageWithNoSuggestionsHiddenAdmin() throws Exception {
		Suggestion suggestion1 = new Suggestion(1L, "suggestion1", true);
		Suggestion suggestion2 = new Suggestion(2L, "suggestion2", true);
		when(suggestionService.getAllByVisible(true)).thenReturn(Collections.emptyList());
		when(suggestionService.getAllByVisible(false)).thenReturn(asList(suggestion1, suggestion2));
		when(authService.isAdmin()).thenReturn(true);
		HtmlPage page = this.webClient.getPage("/suggestions");
		HtmlForm form = page.getFormByName("logout_form");
		// this button is not testable here in a unit test, this test check if the
		// button exists and has the correct text
		assertThat(form.getButtonByName("btn_logout").getTextContent()).isEqualTo("Logout");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("New suggestion")
				.containsOnlyOnce("Logged as Admin").containsOnlyOnce("Logout").doesNotContain("No hidden suggestions")
				.containsOnlyOnce("No suggestions");
		assertThat(page.getAnchors().get(0).getHrefAttribute()).isEqualTo("/");
		assertThat(page.getAnchors().get(1).getHrefAttribute()).isEqualTo("/suggestions/new");
		HtmlTable table = page.getHtmlElementById("hiddenSuggestions_table");
		assertThat(removeWindowsCR(table.asText())).isEqualTo(
				"Hidden suggestions\n" + "ID	Suggestion\n" + "1	suggestion1	Comments	Edit	Show	Delete\n"
						+ "2	suggestion2	Comments	Edit	Show	Delete");
		assertThat(page.getAnchors().get(0).getHrefAttribute()).isEqualTo("/");
		assertThat(page.getAnchors().get(1).getHrefAttribute()).isEqualTo("/suggestions/new");
		verify(suggestionService).getAllByVisible(true);
		verify(suggestionService).getAllByVisible(false);
		verify(authService).isAdmin();
		verifyNoMoreInteractions(suggestionService);
		verifyNoMoreInteractions(authService);
	}

	@Test
	public void testSuggestionsPageWithSuggestionsAndHiddenAdmin() throws Exception {
		Suggestion suggestion1 = new Suggestion(1L, "suggestion1", true);
		Suggestion suggestion2 = new Suggestion(2L, "suggestion2", true);
		Suggestion suggestion3 = new Suggestion(3L, "suggestion3", false);
		Suggestion suggestion4 = new Suggestion(4L, "suggestion4", false);
		when(suggestionService.getAllByVisible(true)).thenReturn(asList(suggestion1, suggestion2));
		when(suggestionService.getAllByVisible(false)).thenReturn(asList(suggestion3, suggestion4));
		when(authService.isAdmin()).thenReturn(true);
		HtmlPage page = this.webClient.getPage("/suggestions");
		HtmlForm form = page.getFormByName("logout_form");
		// this button is not testable here in a unit test, this test check if the
		// button exists and has the correct text
		assertThat(form.getButtonByName("btn_logout").getTextContent()).isEqualTo("Logout");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("New suggestion")
				.containsOnlyOnce("Logged as Admin").containsOnlyOnce("Logout").doesNotContain("No suggestions")
				.doesNotContain("No hidden suggestions");
		assertThat(page.getAnchors().get(0).getHrefAttribute()).isEqualTo("/");
		assertThat(page.getAnchors().get(1).getHrefAttribute()).isEqualTo("/suggestions/new");
		HtmlTable table = page.getHtmlElementById("suggestions_table");
		assertThat(removeWindowsCR(table.asText())).isEqualTo(
				"Suggestions\n" + "ID	Suggestion\n" + "1	suggestion1	Comments	Edit	Hide	Delete\n"
						+ "2	suggestion2	Comments	Edit	Hide	Delete");
		HtmlTable hiddenTable = page.getHtmlElementById("hiddenSuggestions_table");
		assertThat(removeWindowsCR(hiddenTable.asText())).isEqualTo(
				"Hidden suggestions\n" + "ID	Suggestion\n" + "3	suggestion3	Comments	Edit	Show	Delete\n"
						+ "4	suggestion4	Comments	Edit	Show	Delete");
		assertThat(page.getAnchors().get(0).getHrefAttribute()).isEqualTo("/");
		assertThat(page.getAnchors().get(1).getHrefAttribute()).isEqualTo("/suggestions/new");
		verify(suggestionService).getAllByVisible(true);
		verify(suggestionService).getAllByVisible(false);
		verify(authService).isAdmin();
		verifyNoMoreInteractions(suggestionService);
		verifyNoMoreInteractions(authService);
	}

	@Test
	public void testHidePageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/suggestions/hide/1");
		assertThat(page.getTitleText()).isEqualTo("Hide/Show suggestion");
	}

	@Test
	public void testHidePageWhenNoSuggestionWithSelectedId() throws Exception {
		HtmlPage page = this.webClient.getPage("/suggestions/hide/1");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("Hide/Show suggestion")
				.containsOnlyOnce("No suggestion found with id: 1");
		assertThat(page.getAnchorByText("Home").getHrefAttribute()).isEqualTo("/");
	}

	@Test
	public void testHidePageWhenSuggestionWithSelectedIdToHide() throws Exception {
		Suggestion suggestion = spy(new Suggestion(1L, "suggestion1", true));
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		HtmlPage page = this.webClient.getPage("/suggestions/hide/1");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("Hide/Show suggestion")
				.containsOnlyOnce("Want to hide?").containsOnlyOnce("Yes");
		assertThat(page.getAnchorByText("Home").getHrefAttribute()).isEqualTo("/");
		HtmlForm form = page.getFormByName("hideshow_form");
		assertThat(form.getButtonByName("btn_submit").getTextContent()).isEqualTo("Yes");
		verify(suggestionService).getSuggestionById(1L);
		verifyNoMoreInteractions(suggestionService);
		verify(suggestion).setVisible(false);
	}

	@Test
	public void testHidePageWhenSuggestionWithSelectedIdToShow() throws Exception {
		Suggestion suggestion = spy(new Suggestion(1L, "suggestion1", false));
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestion);
		HtmlPage page = this.webClient.getPage("/suggestions/hide/1");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("Hide/Show suggestion")
				.containsOnlyOnce("Want to show?").containsOnlyOnce("Yes");
		assertThat(page.getAnchorByText("Home").getHrefAttribute()).isEqualTo("/");
		HtmlForm form = page.getFormByName("hideshow_form");
		assertThat(form.getButtonByName("btn_submit").getTextContent()).isEqualTo("Yes");
		verify(suggestionService).getSuggestionById(1L);
		verifyNoMoreInteractions(suggestionService);
		verify(suggestion).setVisible(true);
	}

	@Test
	public void testHideSuggestion() throws Exception {
		when(suggestionService.getSuggestionById(1L)).thenReturn(new Suggestion(1L, "suggestionToHide", true));
		HtmlPage page = this.webClient.getPage("/suggestions/hide/1");
		final HtmlForm form = page.getFormByName("hideshow_form");
		form.getButtonByName("btn_submit").click();
		verify(suggestionService).updateSuggestionById(1L, new Suggestion(1L, "suggestionToHide", false));
	}

	@Test
	public void testShowSuggestion() throws Exception {
		when(suggestionService.getSuggestionById(1L)).thenReturn(new Suggestion(1L, "suggestionToShow", false));
		HtmlPage page = this.webClient.getPage("/suggestions/hide/1");
		final HtmlForm form = page.getFormByName("hideshow_form");
		form.getButtonByName("btn_submit").click();
		verify(suggestionService).updateSuggestionById(1L, new Suggestion(1L, "suggestionToShow", true));
	}

	@Test
	public void testEditPageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/suggestions/edit/1");
		assertThat(page.getTitleText()).isEqualTo("Edit suggestion");
	}

	@Test
	public void testEditPageWhenNoSuggestionWithSelectedId() throws Exception {
		HtmlPage page = this.webClient.getPage("/suggestions/edit/1");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("Edit suggestion")
				.containsOnlyOnce("No suggestion found with id: 1");
		assertThat(page.getAnchorByText("Home").getHrefAttribute()).isEqualTo("/");
	}

	@Test
	public void testEditPageWhenSuggestionWithSelectedIdExists() throws Exception {
		String oldSuggestion = "old suggestion";
		Suggestion suggestionToEdit = new Suggestion(1L, oldSuggestion, true);
		when(suggestionService.getSuggestionById(1L)).thenReturn(suggestionToEdit);
		HtmlPage page = this.webClient.getPage("/suggestions/edit/1");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("Edit suggestion")
				.containsOnlyOnce("Suggestion: ").containsOnlyOnce("Update");
		assertThat(page.getAnchorByText("Home").getHrefAttribute()).isEqualTo("/");
		HtmlForm form = page.getFormByName("update_form");
		assertThat(form.getButtonByName("btn_submit").getTextContent()).isEqualTo("Update");
		assertThat(form.getInputByValue(oldSuggestion).asText()).isEqualTo(oldSuggestion);
		verify(suggestionService).getSuggestionById(1L);
		verifyNoMoreInteractions(suggestionService);
	}
	
	@Test
	public void testEditSuggestion() throws Exception {
		String oldSuggestion = "old suggestion";
		when(suggestionService.getSuggestionById(1L)).thenReturn(new Suggestion(1L, oldSuggestion, true));
		HtmlPage page = this.webClient.getPage("/suggestions/edit/1");
		final HtmlForm form = page.getFormByName("update_form");
		form.getInputByValue(oldSuggestion).setValueAttribute("modified suggestion");
		form.getButtonByName("btn_submit").click();
		verify(suggestionService).updateSuggestionById(1L, new Suggestion(1L, "modified suggestion", true));
	}

	private String removeWindowsCR(String s) {
		return s.replace("\r", "");
	}

}
