package com.examples.suggestions_project.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.examples.suggestions_project.model.Comment;

@RestController
@RequestMapping("api/suggestions/{suggestionId}/comments")
public class CommentRestController {

	@GetMapping
	public List<Comment> getAllComments(@PathVariable Long suggestionId) {
		return Collections.emptyList();
	}
}