package com.examples.suggestions_project.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

import static java.util.Arrays.*;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;

import com.examples.suggestions_project.exception.ResourceNotFoundException;
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
		when(suggestionRepository.findById(1L)).thenReturn(suggestion);
		assertThat(suggestionService.getSuggestionById(1)).isEqualTo(insideOfOptional);
	}

	@Test
	public void testGetSuggestionByIdWithAEmptyOptional() {
		Optional<Suggestion> suggestion = Optional.empty();
		when(suggestionRepository.findById(1L)).thenReturn(suggestion);
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
		when(suggestionRepository.findById(1L)).thenReturn(suggestion);
		assertThat(suggestionService.getSuggestionByIdAndVisible(1, true)).isEqualTo(insideOfOptional);
		assertThat(suggestionService.getSuggestionByIdAndVisible(1, false)).isNull();
	}

	@Test
	public void testGetSuggestionByIdAndVisibleWithAEmptyOptional() {
		Optional<Suggestion> suggestion = Optional.empty();
		when(suggestionRepository.findById(1L)).thenReturn(suggestion);
		assertThat(suggestionService.getSuggestionByIdAndVisible(1, true)).isNull();
		assertThat(suggestionService.getSuggestionByIdAndVisible(1, false)).isNull();
	}

	@Test
	public void testUpdateExistingSuggestion() throws ResourceNotFoundException {
		String modifiedText = "modificed";
		Boolean modifiedVisible = false;
		Suggestion oldSuggestion = new Suggestion(1L, "old", true);
		Optional<Suggestion> oldSuggestionOptional = Optional.of(oldSuggestion);
		Suggestion modifiedSuggestion = spy(new Suggestion(10L, modifiedText, modifiedVisible));
		when(suggestionRepository.save(any(Suggestion.class))).then(returnsFirstArg());
		Long idToUpdate = 1L;
		when(suggestionRepository.findById(idToUpdate)).thenReturn(oldSuggestionOptional);
		Suggestion suggestionReceived = suggestionService.updateSuggestionById(idToUpdate, modifiedSuggestion);
		assertThat(suggestionReceived.getId()).isEqualTo(idToUpdate);
		assertThat(suggestionReceived.getVisible()).isEqualTo(modifiedVisible);
		assertThat(suggestionReceived.getSuggestionText()).isEqualTo(modifiedText);
		InOrder inOrder = inOrder(modifiedSuggestion, suggestionRepository);
		inOrder.verify(modifiedSuggestion).setId(idToUpdate);
		inOrder.verify(suggestionRepository).save(modifiedSuggestion);
	}

	@Test
	public void testUpdateExistingSuggestionThatThrowsException() {
		Suggestion modifiedSuggestion = spy(new Suggestion(10L, "suggestion", false));
		Long idToUpdate = 1L;
		when(suggestionRepository.findById(idToUpdate)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> {
			suggestionService.updateSuggestionById(idToUpdate, modifiedSuggestion);
		}).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("It is not possible to update a suggestion with the id: 1");
		InOrder inOrder = inOrder(modifiedSuggestion, suggestionRepository);
		inOrder.verify(suggestionRepository).findById(idToUpdate);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void testDeleteASuggestionWithSuccess() throws ResourceNotFoundException {
		Long idToDelete = 1L;
		doNothing().when(suggestionRepository).deleteById(idToDelete);
		suggestionService.deleteById(1L);
		InOrder inOrder = inOrder(suggestionRepository);
		inOrder.verify(suggestionRepository).deleteById(idToDelete);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void testDeleteASuggestionThatThrowsException() {
		Long idToDelete = 1L;
		doThrow(EmptyResultDataAccessException.class).when(suggestionRepository).deleteById(idToDelete);
		assertThatThrownBy(() -> {
			suggestionService.deleteById(idToDelete);
		}).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("It is not possible to delete a suggestion with the id: 1");
		InOrder inOrder = inOrder(suggestionRepository);
		inOrder.verify(suggestionRepository).deleteById(idToDelete);
		inOrder.verifyNoMoreInteractions();
	}

}
