package com.examples.suggestions_project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.examples.suggestions_project.exception.ResourceNotFoundException;
import com.examples.suggestions_project.model.Comment;
import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.repository.CommentRepository;
import com.examples.suggestions_project.repository.SuggestionRepository;
import com.examples.suggestions_project.services.CommentService;
import com.examples.suggestions_project.services.SuggestionService;

/**
 * A possible integration test verifying that the service and repository
 * interact correctly.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Import({ SuggestionService.class, CommentService.class })
public class SuggestionServiceRepositoryIT {

	@Autowired
	private SuggestionService suggestionService;

	@Autowired
	private SuggestionRepository suggestionRepository;

	@Autowired
	private CommentService commentService;

	@Autowired
	private CommentRepository commentRepository;

	@Before
	public void init() {
		commentRepository.deleteAll();
		suggestionRepository.deleteAll();
	}

	@Test
	public void testServiceCanInsertIntoRepository() throws ResourceNotFoundException {
		Suggestion savedSuggestion = suggestionService.insertNewSuggestion(new Suggestion(null, "suggestion", true));
		assertThat(suggestionRepository.findById(savedSuggestion.getId())).isPresent();

		Comment savedComment = commentService.insertNewComment(new Comment(null, "comment", savedSuggestion));
		assertThat(commentRepository.findById(savedComment.getCommentId())).isPresent();
	}

	@Test
	public void testServiceCanRemoveFromRepository() throws ResourceNotFoundException {
		Suggestion savedSuggestion = suggestionService.insertNewSuggestion(new Suggestion(null, "suggestion", true));
		assertThat(suggestionRepository.findById(savedSuggestion.getId())).isPresent();
		suggestionRepository.deleteById(savedSuggestion.getId());
		assertThat(suggestionRepository.findById(savedSuggestion.getId())).isNotPresent();

		Suggestion temp = suggestionService.insertNewSuggestion(new Suggestion(null, "xxx", true));
		Comment savedComment = commentService.insertNewComment(new Comment(null, "comment", temp));
		assertThat(commentRepository.findById(savedComment.getCommentId())).isPresent();
		commentRepository.deleteById(savedComment.getCommentId());
		assertThat(commentRepository.findById(savedComment.getCommentId())).isNotPresent();
	}

	@Test
	public void testServiceCanRemoveFromRepositoryOnCascade() throws ResourceNotFoundException {
		Suggestion savedSuggestion = suggestionService.insertNewSuggestion(new Suggestion(null, "suggestion", true));
		Comment savedComment = commentService.insertNewComment(new Comment(null, "comment", savedSuggestion));
		// the suggestion and the related comment are both present
		assertThat(suggestionRepository.findById(savedSuggestion.getId())).isPresent();
		assertThat(commentRepository.findById(savedComment.getCommentId())).isPresent();
		suggestionService.deleteById(savedSuggestion.getId());
		// deleting the suggestion deleted the related comment too
		assertThat(suggestionRepository.findById(savedSuggestion.getId())).isNotPresent();
		assertThat(commentRepository.findById(savedComment.getCommentId())).isNotPresent();
	}

}