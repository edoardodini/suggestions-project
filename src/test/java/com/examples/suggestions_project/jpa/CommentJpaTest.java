package com.examples.suggestions_project.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.examples.suggestions_project.model.Comment;
import com.examples.suggestions_project.model.Suggestion;

@DataJpaTest
@RunWith(SpringRunner.class)
public class CommentJpaTest {

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testJpaMapping() {
		Suggestion savedSuggestion = entityManager.persistFlushFind(new Suggestion(null, "test", true));
		Comment saved = entityManager.persistFlushFind(new Comment(null, "commentText", savedSuggestion));
		assertThat(saved.getCommentText()).isEqualTo("commentText");
		assertThat(saved.getSuggestion()).isEqualTo(savedSuggestion);
		assertThat(saved.getCommentId()).isNotNull();
		assertThat(saved.getCommentId()).isGreaterThan(0);
	}

}