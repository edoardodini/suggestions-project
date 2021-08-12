package com.examples.suggestions_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SuggestionWebController {

	@GetMapping("/")
	public String home(Model model) {
		return "home";
	}

	@GetMapping("/suggestions")
	public String index(Model model) {
		return "suggestionView";
	}
}
