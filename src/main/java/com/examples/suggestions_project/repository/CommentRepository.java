package com.examples.suggestions_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.examples.suggestions_project.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	public List<Comment> findBySuggestionId(Long suggestionId);

}
