package com.examples.suggestions_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.services.AuthService;
import com.examples.suggestions_project.services.SuggestionService;

@Controller
public class SuggestionWebController {

	private static final String SUGGESTIONS_ATTRIBUTE = "suggestions";
	private static final String HIDDEN_SUGGESTIONS_ATTRIBUTE = "hiddensuggestions";
	private static final String SUGGESTION_ATTRIBUTE = "suggestion";
	private static final String MESSAGE_ATTRIBUTE = "message";
	private static final String OPERATION_ATTRIBUTE = "operation";
	private static final String USER_ATTRIBUTE = "user";

	@Autowired
	private SuggestionService suggestionService;
	@Autowired
	private AuthService authService;

	@GetMapping("/")
	public String home(Model model) {
		return "home";
	}

	@GetMapping("/suggestions")
	public String index(Model model) {
		if (authService.isAdmin()) {
			model.addAttribute(USER_ATTRIBUTE, "admin");
			model.addAttribute(HIDDEN_SUGGESTIONS_ATTRIBUTE, suggestionService.getAllByVisible(false));
		}
		model.addAttribute(SUGGESTIONS_ATTRIBUTE, suggestionService.getAllByVisible(true));
		return "suggestionView";
	}

	@GetMapping("/suggestions/hide/{id}")
	public String hideSuggestion(@PathVariable long id, Model model) {
		Suggestion suggestionById = suggestionService.getSuggestionById(id);
		if (suggestionById != null) {
			suggestionById.setVisible(!(suggestionById.getVisible()));
		}
		model.addAttribute(SUGGESTION_ATTRIBUTE, suggestionById);
		model.addAttribute(MESSAGE_ATTRIBUTE, suggestionById == null ? "No suggestion found with id: " + id : "");
		return "hide";
	}

	@GetMapping("/suggestions/edit/{id}")
	public String editSuggestion(@PathVariable long id, Model model) {
		Suggestion suggestionById = suggestionService.getSuggestionById(id);
		model.addAttribute(SUGGESTION_ATTRIBUTE, suggestionById);
		model.addAttribute(OPERATION_ATTRIBUTE, "update");
		model.addAttribute(MESSAGE_ATTRIBUTE, suggestionById == null ? "No suggestion found with id: " + id : "");
		return "edit";
	}

	@GetMapping("/suggestions/new")
	public String newSuggestion(Model model) {
		model.addAttribute(SUGGESTION_ATTRIBUTE, new Suggestion());
		model.addAttribute(MESSAGE_ATTRIBUTE, "");
		model.addAttribute(OPERATION_ATTRIBUTE, "new");
		return "edit";
	}
}
