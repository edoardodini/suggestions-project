package com.examples.suggestions_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.examples.suggestions_project.model.Suggestion;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

	public List<Suggestion> findByVisible(Boolean visible);
}
