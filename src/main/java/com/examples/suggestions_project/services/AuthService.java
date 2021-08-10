package com.examples.suggestions_project.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.examples.suggestions_project.model.ActualUser;

@Service
public class AuthService {

	private ActualUser actualUser;
	
	public boolean isAdmin() {
		actualUser = new ActualUser(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		return actualUser.getUsername().equals("admin");
	}
}
