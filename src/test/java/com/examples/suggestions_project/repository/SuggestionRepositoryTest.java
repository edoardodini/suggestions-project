package com.examples.suggestions_project.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.examples.suggestions_project.model.Suggestion;

@DataJpaTest
@RunWith(SpringRunner.class)
public class SuggestionRepositoryTest {

	@Autowired
	private SuggestionRepository repository;

	@Test
	public void firstLearningTest() {
		Suggestion suggestion = new Suggestion(null, "test", true);
		Suggestion saved = repository.save(suggestion);
		Collection<Suggestion> suggestions = repository.findAll();
		assertThat(suggestions).containsExactly(saved);
	}

	@Test
	public void findByVisibleTest() {
		Suggestion suggestionVisible1 = repository.save(new Suggestion(null, "visible1", true));
		Suggestion suggestionVisible2 = repository.save(new Suggestion(null, "visible2", true));
		Suggestion suggestionNotVisible1 = repository.save(new Suggestion(null, "notVisible1", false));
		Suggestion suggestionNotVisible2 = repository.save(new Suggestion(null, "notVisible2", false));
		Collection<Suggestion> suggestionsVisible = repository.findByVisible(true);
		Collection<Suggestion> suggestionsNotVisible = repository.findByVisible(false);
		assertThat(suggestionsVisible).containsExactly(suggestionVisible1, suggestionVisible2);
		assertThat(suggestionsNotVisible).containsExactly(suggestionNotVisible1, suggestionNotVisible2);
	}
}
