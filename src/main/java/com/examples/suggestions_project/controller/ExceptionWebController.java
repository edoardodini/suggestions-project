package com.examples.suggestions_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExceptionWebController {

	@GetMapping("/errorPage")
	public String error(Model model) {
		return "errorPage";
	}

}
