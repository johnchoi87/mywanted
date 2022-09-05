package com.wanted.company;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wanted.tag.TagDto;

@JsonInclude(JsonInclude.Include.NON_NULL) // Null 값인 필드 제외
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CompanyNewDto {


	private Map<String, Object> companyName;
	private List<TagDto> tags;
	public Map<String, Object> getCompanyName() {
		return companyName;
	}
	public void setCompanyName(Map<String, Object> companyName) {
		this.companyName = companyName;
	}
	public List<TagDto> getTags() {
		return tags;
	}
	public void setTags(List<TagDto> tags) {
		this.tags = tags;
	}

}
