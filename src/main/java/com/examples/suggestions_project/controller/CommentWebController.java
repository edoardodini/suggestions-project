package com.examples.suggestions_project.controller;

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
	private static final String COMMENTS_ATTRIBUTE= "comments";
	private static final String USER_ATTRIBUTE = "user";
	@Autowired
	private CommentService commentService;
	@Autowired
	private SuggestionService suggestionService;
	@Autowired
	private AuthService authService;

	@GetMapping("/comments")
	public String home(Model model, @PathVariable Long suggestionId) {
		List<Comment> commentsBySuggestionId = commentService.getCommentsBySuggestionId(suggestionId);
		Suggestion suggestionById;
		suggestionById = suggestionService.getSuggestionById(suggestionId);
		if (authService.isAdmin()) {
			model.addAttribute(USER_ATTRIBUTE, "admin");
		}else {
			model.addAttribute(USER_ATTRIBUTE, "");
		}model.addAttribute(COMMENTS_ATTRIBUTE, commentsBySuggestionId);
		model.addAttribute(SUGGESTION_ATTRIBUTE, suggestionById);
		return "commentView";
	}

}
