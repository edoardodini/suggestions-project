package com.examples.suggestions_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("suggestions/{id}")
public class CommentWebController {

	@GetMapping("/comments")
	public String home(Model model) {
		return "commentView";
	}

}
