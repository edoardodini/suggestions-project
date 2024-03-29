package com.examples.suggestions_project.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;

import com.examples.suggestions_project.config.TestSecurityConfig;
import com.examples.suggestions_project.services.AuthService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = LoginWebController.class)
@Import(TestSecurityConfig.class)
public class LoginWebControllerTest {

	@Autowired
	private MockMvc mvc;
	@MockBean
	private AuthService authService;

	@Test
	public void testStatus200() throws Exception {
		mvc.perform(get("/login")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testLoginView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/login")).andReturn().getModelAndView(), "login");
	}

	@Test
	public void testLoginWithoutUserAttribute() throws Exception {
		when(authService.isAdmin()).thenReturn(false);
		mvc.perform(get("/login")).andExpect(view().name("login")).andExpect(model().attributeDoesNotExist("user"));
		verify(authService).isAdmin();
	}

	@Test
	public void testLoginWithUserAttribute() throws Exception {
		when(authService.isAdmin()).thenReturn(true);
		mvc.perform(get("/login")).andExpect(view().name("login")).andExpect(model().attribute("user", "admin"));
		verify(authService).isAdmin();
	}
}
