package com.examples.suggestions_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.examples.suggestions_project.services.AuthService;

@Controller
public class LoginWebController {

	private static final String USER_ATTRIBUTE = "user";

	@Autowired
	private AuthService authService;
	
	@GetMapping("/login")
	public String login(Model model) {
		if(authService.isAdmin()) {
			model.addAttribute(USER_ATTRIBUTE, "admin");
		}
		return "login";
	}
}
