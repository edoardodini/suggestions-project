package com.examples.suggestions_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.examples.suggestions_project.model.Suggestion;

@Repository
public class SuggestionRepository {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public List<Suggestion> findByVisible(Boolean visible) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Optional<Suggestion> findById(long id) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public void deleteById(Long suggestionId) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Suggestion save(Suggestion suggestion) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
