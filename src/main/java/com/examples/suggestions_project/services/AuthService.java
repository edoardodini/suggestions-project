package com.examples.suggestions_project.services;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public boolean isAdmin() {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}
}