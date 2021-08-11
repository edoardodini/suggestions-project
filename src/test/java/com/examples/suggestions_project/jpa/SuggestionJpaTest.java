package com.examples.suggestions_project.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.examples.suggestions_project.model.Suggestion;

@DataJpaTest
@RunWith(SpringRunner.class)
public class SuggestionJpaTest {

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testJpaMapping() {
		Suggestion saved = entityManager.persistFlushFind(new Suggestion(null, "test", true));
		assertThat(saved.getSuggestionText()).isEqualTo("test");
		assertThat(saved.getVisible()).isEqualTo(true);
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getId()).isGreaterThan(0);
	}

}