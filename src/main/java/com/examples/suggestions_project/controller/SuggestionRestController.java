package com.examples.suggestions_project.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examples.suggestions_project.model.Suggestion;

@RestController
public class SuggestionRestController {
	
	@GetMapping
	public List<Suggestion> getAllSuggestions() {
		return Collections.emptyList();
	}

}
