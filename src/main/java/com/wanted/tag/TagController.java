package com.wanted.tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wanted.company.CompanyDto;
import com.wanted.company.CompanyNewDto;
import com.wanted.company.CompanyService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class TagController {
	
	TagService tagService;
	
	@Autowired 
	TagController(TagService tagService) {
		this.tagService = tagService;
	}
	
	@ApiOperation(value="태그 검색 ", notes="태그로 회사 검색, 헤더 언어값에 해당하는 회사이름 반환")
	@GetMapping(value = "/tags", produces="application/json; charset=utf8")
	public ResponseEntity<List<CompanyDto>> searchCompany(
			@ApiParam(value="언어값", required=true, example="ko")
			@RequestHeader(value="x-wanted-language") String language,
			@ApiParam(value="태그검색어", required=true, example="tag_16")
			@RequestParam("query") String query) {
		return tagService.findCompanyByTag(query, language);
	}
}
