package com.examples.suggestions_project.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.examples.suggestions_project.config.TestSecurityConfig;
import com.examples.suggestions_project.services.AuthService;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = LoginWebController.class)
@Import(TestSecurityConfig.class)
public class LoginWebControllerHtmlUnitTest {

	@Autowired
	private WebClient webClient;
	@MockBean
	AuthService authService;

	@Test
	public void testLoginPageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/login");
		assertThat(page.getTitleText()).isEqualTo("Suggestions login");
	}

	@Test
	public void testLoginPageTextsWhenNotLogged() throws Exception {
		when(authService.isAdmin()).thenReturn(false);
		HtmlPage page = webClient.getPage("/login");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home").containsOnlyOnce("User Name: ")
				.containsOnlyOnce("Password: ").containsOnlyOnce("Login");
		assertThat(page.getAnchorByText("Home").getHrefAttribute()).isEqualTo("/");
		verify(authService).isAdmin();
		verifyNoMoreInteractions(authService);
	}

	@Test
	public void testLoginPageTextsWhenNotLoggedWrongUsernameOrPassword() throws Exception {
		when(authService.isAdmin()).thenReturn(false);
		HtmlPage page = webClient.getPage("/login?error");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home")
				.containsOnlyOnce("Invalid username and password.").containsOnlyOnce("User Name: ")
				.containsOnlyOnce("Password: ").containsOnlyOnce("Login");
		assertThat(page.getAnchorByText("Home").getHrefAttribute()).isEqualTo("/");
		verify(authService).isAdmin();
		verifyNoMoreInteractions(authService);
	}

	@Test
	public void testLoginPageTextsWhenLogout() throws Exception {
		when(authService.isAdmin()).thenReturn(false);
		HtmlPage page = webClient.getPage("/login?logout");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home")
				.containsOnlyOnce("You have been logged out.");
		assertThat(page.getAnchorByText("Home").getHrefAttribute()).isEqualTo("/");
		verify(authService).isAdmin();
		verifyNoMoreInteractions(authService);
	}

	@Test
	public void testLoginPageTextsWhenLoged() throws Exception {
		when(authService.isAdmin()).thenReturn(true);
		HtmlPage page = webClient.getPage("/login");
		assertThat(page.getBody().getTextContent()).containsOnlyOnce("Home")
				.containsOnlyOnce(" Already logged as admin ");
		assertThat(page.getAnchorByText("Home").getHrefAttribute()).isEqualTo("/");
		verify(authService).isAdmin();
		verifyNoMoreInteractions(authService);
	}

}
