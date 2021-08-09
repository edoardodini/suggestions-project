package com.examples.suggestions_project.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.repository.SuggestionRepository;

import org.mockito.InjectMocks;
import org.mockito.Mock;

@RunWith(MockitoJUnitRunner.class)
public class SuggestionServiceTest {

	@Mock
	private SuggestionRepository suggestionRepository;
	
	@InjectMocks
	private SuggestionService suggestionService;
	
	@Test
	public void testGetSuggestionById() {
		
	}
}
