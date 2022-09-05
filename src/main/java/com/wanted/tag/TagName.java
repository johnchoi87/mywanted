package com.wanted.tag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonInclude(JsonInclude.Include.NON_NULL) // Null 값인 필드 제외
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TagName {
	private String ko;
	private String en;
	private String ja;
}
