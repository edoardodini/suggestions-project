package com.examples.suggestions_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import com.examples.suggestions_project.model.Comment;

@Repository
public class CommentRepository {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";
	
	public List<Comment> findBySuggestionId(Long suggestionId){
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Optional<Comment> findByCommentIdAndSuggestionId(Long commentId, Long suggestionId){
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Optional<Comment> findById(long id) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Comment save(Comment any) throws DataIntegrityViolationException{
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public void deleteById(Long id) throws EmptyResultDataAccessException{
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
