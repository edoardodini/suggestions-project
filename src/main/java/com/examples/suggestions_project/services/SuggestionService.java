package com.examples.suggestions_project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.examples.suggestions_project.exception.ResourceNotFoundException;
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
		Suggestion suggestionToReturn = suggestionRep.findById(id).orElse(null);
		if (suggestionToReturn == null) {
			return null;
		} else {
			if (!suggestionToReturn.getVisible().equals(visible)) {
				return null;
			} else {
				return suggestionToReturn;
			}
		}
	}

	public Suggestion updateSuggestionById(long id, Suggestion replacementSuggestion) throws ResourceNotFoundException {
		if (!suggestionRep.findById(id).isPresent()) {
			throw new ResourceNotFoundException("It is not possible to update a suggestion with the id: " + id);
		} else {
			replacementSuggestion.setId(id);
			return suggestionRep.save(replacementSuggestion);
		}
	}

	public void deleteById(Long suggestionId) throws ResourceNotFoundException {
		try {
			suggestionRep.deleteById(suggestionId);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(
					"It is not possible to delete a suggestion with the id: " + suggestionId);
		}
	}
}
