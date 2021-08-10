package com.examples.suggestions_project.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;

public class ActualUserTest {
	
	UserDetails userDetails;
	
	@Test
	public void testWithObject() {
		Object object= new Object();
		ActualUser actualUser= new ActualUser(object);
		assertThat(actualUser.getUsername().equals(object.toString()));
	}

	@Test
	public void testWithString() {
		String objectString = "obcjectString";
		ActualUser actualUser= new ActualUser(objectString);
		assertThat(actualUser.getUsername().equals(objectString));
	}
	
	@Test
	public void testWithUserDetailsOfAdmin() {
		String expectedUsername = "admin";
		userDetails = mock(UserDetails.class);
		when(userDetails.getUsername()).thenReturn(expectedUsername);
		ActualUser actualUser= new ActualUser(userDetails);
		assertThat(actualUser.getUsername().equals(expectedUsername));
	}
	
	@Test
	public void testWithUserDetailsOfNotAdmin() {
		String expectedUsername = "notAdmin";
		userDetails = mock(UserDetails.class);
		when(userDetails.getUsername()).thenReturn(expectedUsername);
		ActualUser actualUser= new ActualUser(userDetails);
		assertThat(actualUser.getUsername().equals(expectedUsername));
	}
}
