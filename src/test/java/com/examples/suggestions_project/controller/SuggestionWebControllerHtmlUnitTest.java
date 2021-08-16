package com.examples.suggestions_project.controller;

import static org.assertj.core.api.Assertions.assertThat;
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
	public void testSuggestionsPageWithSuggestionsNoAdmin() throws Exception {
		Suggestion suggestion1 = new Suggestion(1L, "suggestion1", true);
		Suggestion suggestion2 = new Suggestion(2L, "suggestion2", true);
		when(suggestionService.getAllByVisible(true)).thenReturn(asList(suggestion1, suggestion2));
		when(authService.isAdmin()).thenReturn(false);
		HtmlPage page = this.webClient.getPage("/suggestions");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("New suggestion")
				.containsOnlyOnce("Logged as generic user").doesNotContain("No suggestions");
		HtmlTable table = page.getHtmlElementById("suggestions_table");
		assertThat(table.asText())
				.isEqualTo(	"Suggestions\n" + 
							"ID	Suggestion\n" + 
							"1	suggestion1	Comments\n" + 
							"2	suggestion2	Comments");
		assertThat(page.getAnchors().get(0).getHrefAttribute()).isEqualTo("/");
		assertThat(page.getAnchors().get(1).getHrefAttribute()).isEqualTo("/suggestions/new");
		verify(suggestionService).getAllByVisible(true);
		verify(authService).isAdmin();
		verifyNoMoreInteractions(suggestionService);
		verifyNoMoreInteractions(authService);
	}

}
