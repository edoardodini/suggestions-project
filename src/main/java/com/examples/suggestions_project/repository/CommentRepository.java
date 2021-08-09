package com.examples.suggestions_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.examples.suggestions_project.model.Comment;

@Repository
public class CommentRepository {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";
	
	public List<Comment> findBySuggestionId(Long suggestionId){
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Optional<Comment> findByCommentIdAndSuggestionId(Long CommentId, Long suggestionId){
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Optional<Comment> findById(long id) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
