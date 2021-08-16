package com.examples.suggestions_project.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.examples.suggestions_project.services.AuthService;
import com.examples.suggestions_project.services.SuggestionService;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

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
}
