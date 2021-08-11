package com.examples.suggestions_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.services.SuggestionService;

@RestController
@RequestMapping("api/suggestions")
public class SuggestionRestController {
	
	@Autowired
	private SuggestionService suggestionService;
	
	@GetMapping
	public List<Suggestion> getAllSuggestions() {
		return suggestionService.getAllByVisible(true);
	}

}
