package com.wanted.tag;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonInclude(JsonInclude.Include.NON_NULL) // Null 값인 필드 제외
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TagDto {
	private Map<String, Object> tagName;

	public Map<String, Object> getTagName() {
		return tagName;
	}

	public void setTagName(Map<String, Object> tagName) {
		this.tagName = tagName;
	}
}
