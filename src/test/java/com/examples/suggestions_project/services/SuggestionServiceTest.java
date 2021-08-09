package com.examples.suggestions_project.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import static java.util.Arrays.*;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.repository.SuggestionRepository;

import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@RunWith(MockitoJUnitRunner.class)
public class SuggestionServiceTest {

	@Mock
	private SuggestionRepository suggestionRepository;

	@InjectMocks
	private SuggestionService suggestionService;

	@Test
	public void testGetSuggestionByIdWithASuggestion() {
		Suggestion insideOfOptional = new Suggestion(1L, "", true);
		Optional<Suggestion> suggestion = Optional.of(insideOfOptional);
		when(suggestionRepository.findById(1)).thenReturn(suggestion);
		assertThat(suggestionService.getSuggestionById(1)).isEqualTo(insideOfOptional);
	}

	@Test
	public void testGetSuggestionByIdWithAEmptyOptional() {
		Optional<Suggestion> suggestion = Optional.empty();
		when(suggestionRepository.findById(1)).thenReturn(suggestion);
		assertThat(suggestionService.getSuggestionById(1)).isNull();
	}

	@Test
	public void testInsertNewSuggestion() {
		Suggestion toSave = spy(new Suggestion(10L, "", false));
		Suggestion saved = new Suggestion(1L, "saved", true);
		when(suggestionRepository.save(any(Suggestion.class))).thenReturn(saved);
		Suggestion result = suggestionService.insertNewSuggestion(toSave);
		assertThat(result).isSameAs(saved);
		InOrder inOrder = inOrder(toSave, suggestionRepository);
		inOrder.verify(toSave).setId(null);
		inOrder.verify(toSave).setVisible(true);
		inOrder.verify(suggestionRepository).save(toSave);
	}

	@Test
	public void TestGetAllByVisible() {
		Suggestion suggestion1 = new Suggestion(1L, "suggestion1", false);
		Suggestion suggestion2 = new Suggestion(2L, "suggestion2", false);
		Suggestion suggestion3 = new Suggestion(1L, "suggestion1", false);
		Suggestion suggestion4 = new Suggestion(2L, "suggestion2", false);
		when(suggestionRepository.findByVisible(false)).thenReturn(asList(suggestion1, suggestion2));
		assertThat(suggestionService.getAllByVisible(false)).containsExactly(suggestion1, suggestion2);
		InOrder inOrder = inOrder(suggestionRepository);
		inOrder.verify(suggestionRepository).findByVisible(false);
		when(suggestionRepository.findByVisible(true)).thenReturn(asList(suggestion3, suggestion4));
		assertThat(suggestionService.getAllByVisible(true)).containsExactly(suggestion3, suggestion4);
		inOrder.verify(suggestionRepository).findByVisible(true);
	}
	
	@Test
	public void testGetSuggestionByIdAndVisibleWithASuggestion() {
		Suggestion insideOfOptional = new Suggestion(1L, "", true);
		Optional<Suggestion> suggestion = Optional.of(insideOfOptional);
		when(suggestionRepository.findById(1)).thenReturn(suggestion);
		assertThat(suggestionService.getSuggestionByIdAndVisible(1,true)).isEqualTo(insideOfOptional);
		assertThat(suggestionService.getSuggestionByIdAndVisible(1,false)).isNull();
	}

	@Test
	public void testGetSuggestionByIdAndVisibleWithAEmptyOptional() {
		Optional<Suggestion> suggestion = Optional.empty();
		when(suggestionRepository.findById(1)).thenReturn(suggestion);
		assertThat(suggestionService.getSuggestionByIdAndVisible(1,true)).isNull();
		assertThat(suggestionService.getSuggestionByIdAndVisible(1,false)).isNull();
	}

}
