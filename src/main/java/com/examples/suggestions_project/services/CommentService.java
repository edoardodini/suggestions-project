package com.examples.suggestions_project.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.examples.suggestions_project.model.Comment;

@Service
public class CommentService {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public List<Comment> getCommentsBySuggestionId(Long suggestionId) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}
	
}
