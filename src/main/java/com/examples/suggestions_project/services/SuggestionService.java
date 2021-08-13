package com.examples.suggestions_project.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.examples.suggestions_project.exception.ResourceNotFoundException;
import com.examples.suggestions_project.model.Suggestion;

@Service
public class SuggestionService {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public List<Suggestion> getAllByVisible(boolean visible) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Suggestion getSuggestionById(Long suggestionId) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Suggestion insertNewSuggestion(Suggestion suggestion) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Suggestion updateSuggestionById(Long suggestionId, Suggestion suggestion) throws ResourceNotFoundException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public void deleteById(Long suggestionId) throws ResourceNotFoundException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
