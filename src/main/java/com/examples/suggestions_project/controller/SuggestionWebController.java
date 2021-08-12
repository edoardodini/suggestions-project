package com.examples.suggestions_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.examples.suggestions_project.services.SuggestionService;

@Controller
public class SuggestionWebController {
	
	private static final String SUGGESTIONS_ATTRIBUTE = "suggestions";

	@Autowired
	private SuggestionService suggestionService;

	@GetMapping("/")
	public String home(Model model) {
		return "home";
	}

	@GetMapping("/suggestions")
	public String index(Model model) {
		model.addAttribute(SUGGESTIONS_ATTRIBUTE, suggestionService.getAllByVisible(true));
		return "suggestionView";
	}
}
