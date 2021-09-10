package com.examples.suggestions_project;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.repository.SuggestionRepository;

import io.restassured.RestAssured;

/**
 * Some examples of tests for the rest controller when running in a real web
 * container, manually using the {@link EmployeeRepository}.
 * 
 * The web server is started on a random port, which can be retrieved by
 * injecting in the test a {@link LocalServerPort}.
 * 
 * In tests you can't rely on fixed identifiers: use the ones returned by the
 * repository after saving (automatically generated)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SuggestionRestControllerIT {

	@Autowired
	private SuggestionRepository suggestionRepository;

	@LocalServerPort
	private int port;

	@Before
	public void setup() {
		RestAssured.port = port;
		// always start with an empty database
		suggestionRepository.deleteAll();
		suggestionRepository.flush();
	}

	@Test
	public void testShowEmptySuggestion() throws Exception {
		// read suggestions with a get
		given().contentType(MediaType.APPLICATION_JSON_VALUE).when().get("/api/suggestions").then().statusCode(200)
				.body("", equalTo(emptyList()));
	}

	@Test
	public void testShowSuggestions() throws Exception {
		Suggestion firstSuggestion = suggestionRepository.save(new Suggestion(null, "first", true));
		// the not visible suggestion should not be visible using the rest endpoint
		suggestionRepository.save(new Suggestion(null, "second", false));
		Suggestion thirdSuggestion = suggestionRepository.save(new Suggestion(null, "third", true));
		// read suggestions with a get
		Suggestion[] suggestions = given().contentType(MediaType.APPLICATION_JSON_VALUE).when().get("/api/suggestions")
				.as(Suggestion[].class);
		assertThat(suggestions).containsExactly(firstSuggestion, thirdSuggestion);
	}

	@Test
	public void testShowSuggestion() throws Exception {
		Suggestion firstSuggestion = suggestionRepository.save(new Suggestion(null, "first", true));
		// the not visible suggestion should not be visible using the rest endpoint
		Suggestion secondSuggestion = suggestionRepository.save(new Suggestion(null, "second", false));
		// read suggestion with a get
		Suggestion suggestion1 = given().contentType(MediaType.APPLICATION_JSON_VALUE).when()
				.get("/api/suggestions/" + firstSuggestion.getId()).as(Suggestion.class);
		assertThat(suggestion1).isEqualTo(firstSuggestion);
		given().contentType(MediaType.APPLICATION_JSON_VALUE).when().get("/api/suggestions/" + secondSuggestion.getId())
				.then().statusCode(200).contentType(is(emptyOrNullString()));
	}
}
