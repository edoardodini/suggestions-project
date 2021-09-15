package com.examples.suggestions_project.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;

import com.examples.suggestions_project.model.Comment;
import com.examples.suggestions_project.model.Suggestion;

@DataJpaTest
@RunWith(SpringRunner.class)
public class CommentRepositoryTest {
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	SuggestionRepository suggestionRepository;

	@Test
	public void firstLearningTest() {
		Suggestion savedSuggestion = suggestionRepository.save(new Suggestion(null, "suggestion1", true));
		Comment savedComment = commentRepository.save(new Comment(null, "comment1", savedSuggestion));
		Collection<Comment> comments = commentRepository.findAll();
		assertThat(comments).containsExactly(savedComment);
	}

	@Test
	public void findBySuggestionIdTest() {
		Suggestion suggestion1 = suggestionRepository.save(new Suggestion(null, "visible1", true));
		Suggestion suggestion2 = suggestionRepository.save(new Suggestion(null, "visible2", true));
		Long firstId = suggestion1.getId();
		Long secondId = suggestion2.getId();
		Comment commentToFirstSuggestion1 = commentRepository.save(new Comment(null, "comment1", suggestion1));
		Comment commentToFirstSuggestion2 = commentRepository.save(new Comment(null, "comment2", suggestion1));
		Comment commentToSecondSuggestion1 = commentRepository.save(new Comment(null, "comment3", suggestion2));
		Comment commentToSecondSuggestion2 = commentRepository.save(new Comment(null, "comment4", suggestion2));
		Collection<Comment> commentsOfFirstSuggestion = commentRepository.findBySuggestionId(firstId);
		Collection<Comment> commentsOfSecondSuggestion = commentRepository.findBySuggestionId(secondId);
		assertThat(commentsOfFirstSuggestion).containsExactly(commentToFirstSuggestion1, commentToFirstSuggestion2);
		assertThat(commentsOfSecondSuggestion).containsExactly(commentToSecondSuggestion1, commentToSecondSuggestion2);
	}

	@Test
	public void saveRaiseException() {
		Comment comment = new Comment(null, "comment", new Suggestion(null, "suggestion", true));
		assertThatThrownBy(() -> commentRepository.save(comment))
				.isInstanceOf(InvalidDataAccessApiUsageException.class);
	}

}
