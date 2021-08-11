package com.examples.suggestions_project.jpa;

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
	}

}