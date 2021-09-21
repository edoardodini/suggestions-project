package com.examples.suggestions_project.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.examples.suggestions_project.model.ActualUser;

@Service
public class AuthService {

	@Value("${spring.security.user.name}")
	private String adminName;

	public boolean isAdmin() {
		ActualUser actualUser = new ActualUser(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		return actualUser.getUsername().equals(adminName);
	}
}
