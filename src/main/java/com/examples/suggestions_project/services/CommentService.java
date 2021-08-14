package com.examples.suggestions_project.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.examples.suggestions_project.exception.ResourceNotFoundException;
import com.examples.suggestions_project.model.Comment;

@Service
public class CommentService {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public List<Comment> getCommentsBySuggestionId(Long suggestionId) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Comment getCommentById(long id) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}
	
	public Comment insertNewComment(Comment comment) throws ResourceNotFoundException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public void deleteById(Long commentId) throws ResourceNotFoundException{
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}
}
