package com.examples.suggestions_project.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;

import com.examples.suggestions_project.config.TestSecurityConfig;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ExceptionWebController.class)
@Import(TestSecurityConfig.class)
public class ExceptionWebControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void testStatus200() throws Exception {
		mvc.perform(get("/errorPage")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testReturnHomeView() throws Exception {
		ModelAndViewAssert.assertViewName(mvc.perform(get("/errorPage")).andReturn().getModelAndView(), "errorPage");
	}

}
