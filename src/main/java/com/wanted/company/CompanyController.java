package com.wanted.company;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class CompanyController {
	
	CompanyService companyService;
	
	@Autowired
	public CompanyController(CompanyService companyService) {
		this.companyService = companyService;
	}
	
	@ApiOperation(value="회사이름 검색 ", notes="자동완성 기능 제공")
	@GetMapping(value = "/search", produces="application/json; charset=utf8")
	public ResponseEntity<List<CompanyDto>> searchCompany(
			@ApiParam(value="회사이름검색어", required=true, example="링크")
			@RequestParam("query") String query) {
		return companyService.searchCompany(query);
	}
	
	@ApiOperation(value="회사이름 검색 ", notes="회사이름 검색, 헤더 언어값에 해당하는 회사이름 반환")
	@GetMapping(value = "/companies/{name}", produces="application/json; charset=utf8")
	public ResponseEntity<CompanyDto> findCompanyByName(
			@ApiParam(value="언어값", required=true, example="ko")
			@RequestHeader(value="x-wanted-language") String language,
			@ApiParam(value="회사이름", required=true, example="Wantedlab")
			@PathVariable("name") String name) {
		return companyService.findCompanyByName(language, name);
	}
	
	@ApiOperation(value="회사 등록 ", notes="새로운 회사 등록 ")
	@PostMapping(value = "/companies", produces="application/json; charset=utf8")
	public ResponseEntity<CompanyDto> addNewCompany(
			@ApiParam(value="언어값", required=true, example="ko")
			@RequestHeader(value="x-wanted-language") String language,
			@ApiParam(value="회사등록값", required=true, example="")
			@RequestBody CompanyNewDto companyNewDto	) {
		return companyService.addNewCompany(companyNewDto, language);
	}
	
	@ApiOperation(value="태그추가", notes="태그 추가 등록 ")
	@PutMapping(value = "/companies/{name}/tags", produces="application/json; charset=utf8")
	public ResponseEntity<CompanyDto> addTagsForCompany(
			@ApiParam(value="언어값", required=true, example="ko")
			@RequestHeader(value="x-wanted-language") String language,
			@ApiParam(value="회사이름", required=true, example="원티드랩")
			@PathVariable("name") String name,
			@ApiParam(value="태그값", required=true, example="")
			@RequestBody List<Map<String,Object>> param) {
		return companyService.addTags(name, param, language);
	}
	
	@ApiOperation(value="태그삭제", notes="태그 삭제")
	@DeleteMapping(value = "/companies/{name}/tags/{tag}", produces="application/json; charset=utf8")
	public ResponseEntity<CompanyDto> deleteTagsForCompany(
			@ApiParam(value="언어값", required=true, example="ko")
			@RequestHeader(value="x-wanted-language") String language,
			@ApiParam(value="회사이름", required=true, example="원티드랩")
			@PathVariable("name") String name,
			@ApiParam(value="태그이름", required=true, example="tag_15")
			@PathVariable("tag") String tag) {
		return companyService.deleteTag(name, tag, language);
	}
}
