package com.examples.suggestions_project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.examples.suggestions_project.model.Comment;
import com.examples.suggestions_project.repository.CommentRepository;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;
	
	public Comment getCommentById(long id) {
		return commentRepository.findById(id).orElse(null);
	}

}
