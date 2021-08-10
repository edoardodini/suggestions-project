package com.examples.suggestions_project.model;

import java.util.Objects;

public class Suggestion {

	private Long id;
	private String suggestionText;
	private Boolean visible;

	public Suggestion(Long id, String suggestionText, boolean visible) {
		this.id = id;
		this.suggestionText = suggestionText;
		this.visible = visible;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSuggestionText() {
		return suggestionText;
	}

	public void setSuggestionText(String suggestion) {
		this.suggestionText = suggestion;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	@Override
	public String toString() {
		return "Suggestion [id=" + id + ", suggestionText=" + suggestionText + ", visible=" + visible + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, suggestionText, visible);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Suggestion other = (Suggestion) obj;
		return Objects.equals(id, other.id) && Objects.equals(suggestionText, other.suggestionText)
				&& Objects.equals(visible, other.visible);
	}

}
