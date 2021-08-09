package com.examples.suggestions_project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.examples.suggestions_project.repository.SuggestionRepository;

@Service
public class CommentService {

	@Autowired
	private SuggestionRepository suggestionRep;

}
