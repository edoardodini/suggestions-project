package com.examples.suggestions_project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static java.util.Arrays.asList;

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

	@Test
	public void testCommentServiceCannotInsertACommentToASuggestionNotInDatabase() {
		Suggestion notInDatabaseSuggestion = new Suggestion();
		assertThatThrownBy(() -> {
			commentService.insertNewComment(new Comment(null, "comment", notInDatabaseSuggestion));
		}).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	public void testSuggestionServiceCanSearchByIdAndVisible() throws ResourceNotFoundException {
		Suggestion suggestionVisible = suggestionService
				.insertNewSuggestion(new Suggestion(null, "suggestion visible", false));
		Suggestion suggestionNotVisible = suggestionService
				.insertNewSuggestion(new Suggestion(null, "suggestion not visible", false));
		suggestionNotVisible.setVisible(false);
		// Update suggestionNotVisible because when inserted is visible by default
		suggestionService.updateSuggestionById(suggestionNotVisible.getId(), suggestionNotVisible);

		assertThat(suggestionService.getSuggestionByIdAndVisible(suggestionVisible.getId(), true))
				.isEqualTo(suggestionVisible);
		assertThat(suggestionService.getSuggestionByIdAndVisible(suggestionVisible.getId(), false)).isNull();
		assertThat(suggestionService.getSuggestionByIdAndVisible(suggestionNotVisible.getId(), true)).isNull();
		assertThat(suggestionService.getSuggestionByIdAndVisible(suggestionNotVisible.getId(), false))
				.isEqualTo(suggestionNotVisible);
	}

	@Test
	public void testSuggestionServiceCanUpdateSuggestion() throws ResourceNotFoundException {
		String newSuggestion = "newSuggestion";
		boolean newBoolean = false;
		Suggestion suggestionToUpdate = suggestionService
				.insertNewSuggestion(new Suggestion(null, "oldSuggestion", true));
		Long id = suggestionToUpdate.getId();
		Suggestion suggestionUpdated = suggestionService.updateSuggestionById(id,
				new Suggestion(null, newSuggestion, newBoolean));
		assertThat(suggestionUpdated).isEqualTo(new Suggestion(id, newSuggestion, newBoolean));
	}

	@Test
	public void testSuggestionServiceCanSearchByVisible() throws ResourceNotFoundException {
		Suggestion suggestionVisible1 = suggestionService.insertNewSuggestion(new Suggestion(null, "visible1", true));
		Suggestion suggestionVisible2 = suggestionService.insertNewSuggestion(new Suggestion(null, "visible2", true));
		Suggestion suggestionNotVisible1 = suggestionService
				.insertNewSuggestion(new Suggestion(null, "notVisible1", true));
		Suggestion suggestionNotVisible2 = suggestionService
				.insertNewSuggestion(new Suggestion(null, "notVisible2", true));
		suggestionNotVisible1.setVisible(false);
		;
		suggestionNotVisible2.setVisible(false);
		suggestionService.updateSuggestionById(suggestionNotVisible1.getId(), suggestionNotVisible1);
		suggestionService.updateSuggestionById(suggestionNotVisible2.getId(), suggestionNotVisible2);
		assertThat(suggestionService.getAllByVisible(true)).isEqualTo(asList(suggestionVisible1, suggestionVisible2));
		assertThat(suggestionService.getAllByVisible(false))
				.isEqualTo(asList(suggestionNotVisible1, suggestionNotVisible2));
	}
}