package com.examples.suggestions_project;

import static org.junit.Assert.*;

import org.junit.Test;

import com.examples.suggestions_project.model.Suggestion;

/**
 * A temporary test just to make sure jacoco.xml is generated and a report can
 * be sent to Coveralls.
 * 
 * This must be removed when we have at least a test executed by surefire.
 */
public class TemporaryTest {

	@Test
	public void test() {
		assertNotNull(new Suggestion(new Long(1), "first suggestion", true));
	}

}