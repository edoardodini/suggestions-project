package com.examples.suggestions_project.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

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

}
