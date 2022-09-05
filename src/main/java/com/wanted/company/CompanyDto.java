package com.wanted.company;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

@JsonInclude(JsonInclude.Include.NON_NULL) // Null 값인 필드 제외
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CompanyDto {
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	private String companyName;
	private List<String> tags;
}
