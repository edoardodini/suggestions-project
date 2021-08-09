package com.examples.suggestions_project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.repository.SuggestionRepository;

@Service
public class SuggestionService {

	@Autowired
	private SuggestionRepository suggestionRep;

	public Suggestion getSuggestionById(long id) {
		return suggestionRep.findById(id).orElse(null);
	}

	public Suggestion insertNewSuggestion(Suggestion suggestion) {
		suggestion.setId(null);
		suggestion.setVisible(true);
		return suggestionRep.save(suggestion);
	}

	public List<Suggestion> getAllByVisible(boolean visible) {
		return suggestionRep.findByVisible(visible);
	}

	public Suggestion getSuggestionByIdAndVisible(long id, boolean visible) {
		return suggestionRep.findByIdAndVisible(id, visible).orElse(null);
	}
}
