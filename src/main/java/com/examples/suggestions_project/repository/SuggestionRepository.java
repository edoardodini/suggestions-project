package com.examples.suggestions_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import com.examples.suggestions_project.model.Suggestion;

@Repository
public class SuggestionRepository{

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";
	
	List<Suggestion> findByVisible(Boolean visible){
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	Optional<Suggestion> findByIdAndVisible(long id, boolean visible){
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}
	
	Optional<Suggestion> findById(long id){
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}
	
	void deleteById(Long suggestionId) throws EmptyResultDataAccessException{
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
