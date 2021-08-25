package com.examples.suggestions_project;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static java.util.Collections.emptyList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import com.examples.suggestions_project.model.Comment;
import com.examples.suggestions_project.model.Suggestion;
import com.examples.suggestions_project.repository.CommentRepository;
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
public class CommentRestControllerIT {

	@Autowired
	private SuggestionRepository suggestionRepository;

	@Autowired
	private CommentRepository commentRepository;

	@LocalServerPort
	private int port;

	@Before
	public void setup() {
		RestAssured.port = port;
		// always start with an empty database
		suggestionRepository.deleteAll();
		suggestionRepository.flush();
		commentRepository.deleteAll();
		commentRepository.flush();
	}

	@Test
	public void testShowEmptyComments() throws Exception {
		// read comments with a get, but no suggestion or comments
		given().contentType(MediaType.APPLICATION_JSON_VALUE).when().get("/api/suggestions/1/comments").then()
				.statusCode(200).body("", equalTo(emptyList()));
		Suggestion suggestionVisible = suggestionRepository.save(new Suggestion(null, "visible suggestion", true));
		// read comments with a get, but no comments
		given().contentType(MediaType.APPLICATION_JSON_VALUE).when()
				.get("/api/suggestions/" + suggestionVisible.getId() + "/comments").then().statusCode(200)
				.body("", equalTo(emptyList()));
		Suggestion suggestionNotVisible = suggestionRepository
				.save(new Suggestion(null, "visible not suggestion", false));
		commentRepository.save(new Comment(null, "comment", suggestionNotVisible));
		// read comments with a get, but suggestion is not visible
		given().contentType(MediaType.APPLICATION_JSON_VALUE).when()
				.get("/api/suggestions/" + suggestionVisible.getId() + "/comments").then().statusCode(200)
				.body("", equalTo(emptyList()));
	}

	@Test
	public void testShowComments() throws Exception {
		Suggestion suggestion = suggestionRepository.save(new Suggestion(null, "first", true));
		Comment firstComment = commentRepository.save(new Comment(null, "comment1", suggestion));
		Comment secondComment = commentRepository.save(new Comment(null, "comment2", suggestion));
		// read comments with a get
		Comment[] comments = given().contentType(MediaType.APPLICATION_JSON_VALUE).when()
				.get("/api/suggestions/" + suggestion.getId() + "/comments").as(Comment[].class);
		assertThat(comments).containsExactly(firstComment,secondComment);
	}
}
