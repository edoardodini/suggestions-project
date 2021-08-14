package com.examples.suggestions_project.model;

import java.util.Objects;

public class Comment {

	private Long commentId;
	private String commentText;
	private Suggestion suggestion;

	public Comment(Long commentId, String commentText, Suggestion suggestion) {
		this.commentId = commentId;
		this.commentText = commentText;
		this.suggestion = suggestion;
	}

	public Comment() {
		// 
	}

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	public Suggestion getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(Suggestion suggestion) {
		this.suggestion = suggestion;
	}

	@Override
	public String toString() {
		return "Comment [commentId=" + commentId + ", commentText=" + commentText + ", suggestion=" + suggestion + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(commentId, commentText, suggestion);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comment other = (Comment) obj;
		return Objects.equals(commentId, other.commentId) && Objects.equals(commentText, other.commentText)
				&& Objects.equals(suggestion, other.suggestion);
	}

}
