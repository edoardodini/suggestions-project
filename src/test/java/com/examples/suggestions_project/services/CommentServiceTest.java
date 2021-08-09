package com.examples.suggestions_project.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.examples.suggestions_project.exception.ResourceNotFoundException;
import com.examples.suggestions_project.model.Comment;
import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.repository.CommentRepository;

import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {

	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private CommentService commentService;

	@Test
	public void testGetCommentByIdWithAComment() {
		Long idToFind = 1L;
		Suggestion suggestion = new Suggestion(2L, "suggestion", true);
		Comment insideOfOptional = new Comment(idToFind, "", suggestion);
		Optional<Comment> comment = Optional.of(insideOfOptional);
		when(commentRepository.findById(1)).thenReturn(comment);
		assertThat(commentService.getCommentById(1)).isEqualTo(insideOfOptional);
		InOrder inOrder = inOrder(commentRepository);
		inOrder.verify(commentRepository).findById(idToFind);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void testGetCommentByIdWithAnEmptyOptional() {
		Long idToFind = 1L;
		Optional<Comment> comment = Optional.empty();
		when(commentRepository.findById(idToFind)).thenReturn(comment);
		assertThat(commentService.getCommentById(idToFind)).isNull();
		InOrder inOrder = inOrder(commentRepository);
		inOrder.verify(commentRepository).findById(idToFind);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void testInsertNewSuggestion() throws ResourceNotFoundException {
		Suggestion suggestionInsideComment = new Suggestion(1L, "suggestion", false);
		Comment toSave = spy(new Comment(10L, "", suggestionInsideComment));
		Comment saved = new Comment(1L, "saved", suggestionInsideComment);
		when(commentRepository.save(any(Comment.class))).thenReturn(saved);
		Comment result = commentService.insertNewComment(toSave);
		assertThat(result).isSameAs(saved);
		InOrder inOrder = inOrder(toSave, commentRepository);
		inOrder.verify(toSave).setCommentId(null);
		inOrder.verify(commentRepository).save(toSave);
	}

	@Test
	public void testInsertNewSuggestionThatThrowsException() {
		Suggestion suggestionInsideComment = new Suggestion(1L, "suggestion", false);
		Comment toSave = spy(new Comment(10L, "", suggestionInsideComment));
		when(commentRepository.save(any(Comment.class))).thenThrow(new DataIntegrityViolationException(""));
		assertThatThrownBy(() -> {
			commentService.insertNewComment(toSave);
		}).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("It is not possible to save a comment for suggestion with id: 1");
		InOrder inOrder = inOrder(toSave, commentRepository);
		inOrder.verify(toSave).setCommentId(null);
		inOrder.verify(commentRepository).save(toSave);
		inOrder.verify(toSave).getSuggestion();
		inOrder.verifyNoMoreInteractions();
		;
	}

	@Test
	public void testDeleteACommentWithSuccess() throws ResourceNotFoundException {
		Long idToDelete = 1L;
		doNothing().when(commentRepository).deleteById(idToDelete);
		commentService.deleteById(1L);
		InOrder inOrder = inOrder(commentRepository);
		inOrder.verify(commentRepository).deleteById(idToDelete);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void testDeleteASuggestionThatThrowsException() {
		Long idToDelete = 1L;
		doThrow(EmptyResultDataAccessException.class).when(commentRepository).deleteById(idToDelete);
		assertThatThrownBy(() -> {
			commentService.deleteById(idToDelete);
		}).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("It is not possible to delete a comment with id: 1");
		InOrder inOrder = inOrder(commentRepository);
		inOrder.verify(commentRepository).deleteById(idToDelete);
		inOrder.verifyNoMoreInteractions();
	}

}
