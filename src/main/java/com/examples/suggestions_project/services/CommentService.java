package com.examples.suggestions_project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import java.util.List;

import com.examples.suggestions_project.exception.ResourceNotFoundException;
import com.examples.suggestions_project.model.Comment;
import com.examples.suggestions_project.repository.CommentRepository;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;

	public Comment getCommentById(long id) {
		return commentRepository.findById(id).orElse(null);
	}

	public Comment insertNewComment(Comment comment) throws ResourceNotFoundException {
		try {
			comment.setCommentId(null);
			return commentRepository.save(comment);
		} catch (InvalidDataAccessApiUsageException exception) {
			throw new ResourceNotFoundException(
					"It is not possible to save a comment for suggestion with id: " + comment.getSuggestion().getId());
		}
	}

	public void deleteById(Long id) throws ResourceNotFoundException {
		try {
			commentRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("It is not possible to delete a comment with id: " + id);
		}
	}

	public List<Comment> getCommentsBySuggestionId(Long suggestionId) {
		return commentRepository.findBySuggestionId(suggestionId);
	}
}
