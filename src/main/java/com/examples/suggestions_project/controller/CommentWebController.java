package com.examples.suggestions_project.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.examples.suggestions_project.exception.ResourceNotFoundException;
import com.examples.suggestions_project.model.Comment;
import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.services.AuthService;
import com.examples.suggestions_project.services.CommentService;
import com.examples.suggestions_project.services.SuggestionService;

@Controller
@RequestMapping("suggestions/{suggestionId}")
public class CommentWebController {

	private static final String NO_COMMENT_FOUND_WITH_SUGGESTION_ID = "No comment found with suggestion id: ";
	private static final String NO_SUGGESTION_FOUND_WITH_SUGGESTION_ID = "No suggestion found with suggestion id: ";
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
			model.addAttribute(MESSAGE_ATTRIBUTE, NO_SUGGESTION_FOUND_WITH_SUGGESTION_ID + suggestionId);
		} else {
			model.addAttribute(MESSAGE_ATTRIBUTE,
					commentsBySuggestionId.isEmpty() ? NO_COMMENT_FOUND_WITH_SUGGESTION_ID + suggestionId : "");
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
				suggestion == null ? NO_SUGGESTION_FOUND_WITH_SUGGESTION_ID + suggestionId : "");
		return "editComment";
	}

	@GetMapping("/delete/{commentId}")
	public String deleteSuggestion(@PathVariable long suggestionId, @PathVariable long commentId, Model model) {
		Suggestion suggestionById = suggestionService.getSuggestionById(suggestionId);
		Comment comment = commentService.getCommentById(commentId);
		model.addAttribute(SUGGESTION_ATTRIBUTE, suggestionById);
		if (suggestionById == null) {
			model.addAttribute(MESSAGE_ATTRIBUTE, NO_SUGGESTION_FOUND_WITH_SUGGESTION_ID + suggestionId);
		} else {
			if(comment!=null&&!comment.getSuggestion().getId().equals(suggestionById.getId())) {
				comment=null;
			}
			model.addAttribute(MESSAGE_ATTRIBUTE,
					comment == null ? "No comment found with comment id: " + commentId : "");
		}
		model.addAttribute(COMMENT_ATTRIBUTE, comment);
		return "deleteComment";
	}

	@PostMapping("/save")
	public String saveComment(Comment comment, @PathVariable long suggestionId) throws ResourceNotFoundException {
		commentService.insertNewComment(comment);
		return "redirect:/suggestions/" + suggestionId + "/comments";
	}

	@PostMapping("/removeComment")
	public String deleteSuggestion(Comment comment, @PathVariable long suggestionId) throws ResourceNotFoundException {
		commentService.deleteById(comment.getCommentId());
		return "redirect:/suggestions/" + suggestionId + "/comments";
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public String myRuntimeException(ResourceNotFoundException e, RedirectAttributes redirectAttrs) {
		redirectAttrs.addFlashAttribute(MESSAGE_ATTRIBUTE, e.getMessage());
		return "redirect:/errorPage";
	}

}
