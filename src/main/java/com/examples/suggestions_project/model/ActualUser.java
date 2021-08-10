package com.examples.suggestions_project.model;

import org.springframework.security.core.userdetails.UserDetails;

public class ActualUser{

	private Object userObject;

	public ActualUser(Object userObject) {
		this.userObject = userObject;
	}
	
	public String getUsername() {
		String username;
		if (userObject instanceof UserDetails) {
			username = ((UserDetails) userObject).getUsername();
		} else {
			username = userObject.toString();
		}
		return username;
	}

}
