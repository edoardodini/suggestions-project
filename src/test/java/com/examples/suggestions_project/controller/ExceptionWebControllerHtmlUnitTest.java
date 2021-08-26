package com.examples.suggestions_project.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.examples.suggestions_project.config.TestSecurityConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ExceptionWebController.class)
@Import(TestSecurityConfig.class)
public class ExceptionWebControllerHtmlUnitTest {

	@Autowired
	private WebClient webClient;
	@Autowired
	private MockMvc mvc;

	@Test
	public void testErrorPageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/errorPage");
		assertThat(page.getTitleText()).isEqualTo("Error");
		assertThat(page.getAnchorByText("Home").getHrefAttribute()).isEqualTo("/");
	}

	@Test
	public void testErrorPageTexts() throws Exception {
		String stringToBeFound = "oddString_oddString_oddString_oddString";
		String result = this.mvc.perform(get("/errorPage").flashAttr("message", stringToBeFound)).andReturn()
				.getResponse().getContentAsString();
		assertThat(result).containsOnlyOnce("Home").containsOnlyOnce(stringToBeFound);
	}

}
