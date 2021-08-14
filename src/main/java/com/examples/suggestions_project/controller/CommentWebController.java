package com.examples.suggestions_project.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.examples.suggestions_project.model.Comment;
import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.services.AuthService;
import com.examples.suggestions_project.services.CommentService;
import com.examples.suggestions_project.services.SuggestionService;

@Controller
@RequestMapping("suggestions/{suggestionId}")
public class CommentWebController {

	private static final String SUGGESTION_ATTRIBUTE = "suggestion";
	private static final String COMMENTS_ATTRIBUTE = "comments";
	private static final String USER_ATTRIBUTE = "user";
	private static final String MESSAGE_ATTRIBUTE = "message";
	private static final String COMMENT_ATTRIBUTE = "comment";
	@Autowired
	private CommentService commentService;
	@Autowired
	private SuggestionService suggestionService;
	@Autowired
	private AuthService authService;

	@GetMapping("/comments")
	public String home(Model model, @PathVariable Long suggestionId) {
		List<Comment> commentsBySuggestionId = commentService.getCommentsBySuggestionId(suggestionId);
		Suggestion suggestionById = suggestionService.getSuggestionById(suggestionId);
		if (!authService.isAdmin() && suggestionById != null && Boolean.FALSE.equals(suggestionById.getVisible())) {
			suggestionById = null;
			commentsBySuggestionId = Collections.emptyList();
		}
		model.addAttribute(USER_ATTRIBUTE, authService.isAdmin() ? "admin" : "");
		model.addAttribute(COMMENTS_ATTRIBUTE, commentsBySuggestionId);
		model.addAttribute(SUGGESTION_ATTRIBUTE, suggestionById);

		if (suggestionById == null) {
			model.addAttribute(MESSAGE_ATTRIBUTE, "No suggestion found with suggestion id: " + suggestionId);
		} else {
			model.addAttribute(MESSAGE_ATTRIBUTE,
					commentsBySuggestionId.isEmpty() ? "No comment found with suggestion id: " + suggestionId : "");
		}
		return "commentView";
	}

	@GetMapping("/newComment")
	public String newSuggestion(Model model, @PathVariable long suggestionId) {
		Suggestion suggestion = suggestionService.getSuggestionById(suggestionId);
		if (suggestion != null) {
			Comment newComment = new Comment();
			newComment.setSuggestion(suggestion);
			model.addAttribute(COMMENT_ATTRIBUTE, newComment);
		}
		model.addAttribute(SUGGESTION_ATTRIBUTE, suggestion);
		model.addAttribute(MESSAGE_ATTRIBUTE,
				suggestion == null ? "No suggestion found with suggestion id:" + suggestionId : "");
		return "editComment";
	}
	
	@GetMapping("/delete/{commentId}")
	public String deleteSuggestion(@PathVariable long suggestionId, @PathVariable long commentId, Model model) {
		return "deleteComment";
	}
	
}
