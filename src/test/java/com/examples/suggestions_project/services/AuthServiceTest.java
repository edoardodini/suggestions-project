package com.examples.suggestions_project.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=AuthService.class)
public class AuthServiceTest {
	
	@Autowired
	private AuthService authenticationService;
	
	@Test
	@WithMockUser(username="admin",roles={"admin"})
	public void testIsAdminWhenAdmin() {
		assertThat(authenticationService.isAdmin()).isEqualTo(true);
	}
	
	@Test
	@WithMockUser(username="notAdmin",roles={"notAdmin"})
	public void testIsAdminWhenNotAdmin() {
		assertThat(authenticationService.isAdmin()).isEqualTo(false);
	}

}
