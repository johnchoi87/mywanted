package com.wanted.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.wanted.company.*;
import com.wanted.tag.Tag;
import com.wanted.tag.TagDto;
import com.wanted.tag.TagRepository;

import lombok.extern.slf4j.Slf4j;

import com.wanted.common.WantedResponse;

@Slf4j
@Service
public class CompanyService {
	CompanyRepository companyRepository;
	CompanyNameRepository companyNameRepository;
	TagRepository tagRepository;
	
	Logger log = LogManager.getLogger();
	
	@Autowired
	public CompanyService(
			CompanyRepository companyRepository,
			CompanyNameRepository companyNameRepository, 
			TagRepository tagRepository) {
		this.companyRepository = companyRepository;
		this.companyNameRepository = companyNameRepository;
		this.tagRepository = tagRepository;
	}
	
	public ResponseEntity<CompanyDto> findCompanyByName(String language, String name) {
		List<CompanyName> result = companyNameRepository.findByName(name);
		ResponseEntity<CompanyDto> response;
		CompanyDto dto = new CompanyDto();
		if(!result.isEmpty()) {
			CompanyName companyName = result.get(0);
			Company company = companyName.getCompany();
			List<CompanyName> searchedCompanyNames = companyNameRepository.findByCompany_IdAndLang(company.getId(), language);
			List<Tag> tags = tagRepository.findByCompany_IdAndLang(company.getId(), language);
			List<String> tagList = new ArrayList();
			CompanyName searchedCompanyName = searchedCompanyNames.get(0);
			dto.setCompanyName(searchedCompanyName.getName());
			for(Tag tag : tags) {
				tagList.add(tag.getName());
			}
			dto.setTags(tagList);
			response = new ResponseEntity<>(dto, HttpStatus.OK);
		} else {
			response = new ResponseEntity<>(dto, HttpStatus.NOT_FOUND);
		}
		return response;
	}

	public ResponseEntity<List<CompanyDto>> searchCompany(String query) {
		List<CompanyName> companyNames = companyNameRepository.findByNameContains(query);
		List<CompanyDto> results = new ArrayList<>();
		ResponseEntity<List<CompanyDto>> response;
		for(CompanyName companyName : companyNames) {
			CompanyDto companyDto = new CompanyDto();
			companyDto.setCompanyName(companyName.getName());
			results.add(companyDto);
		}
		response = new ResponseEntity<>(results, HttpStatus.OK);
		return response;
	}

	// 새로운 회사를 등록한다.
	public ResponseEntity<CompanyDto> addNewCompany(CompanyNewDto companyNewDto, String language) {
		Company company = new Company();
		company = companyRepository.save(company);
		Map<String, Object> nameMap = companyNewDto.getCompanyName();
		
		for (Map.Entry<String, Object> entry : nameMap.entrySet()) {
			String lang = entry.getKey(); // lang
			String name = (String)entry.getValue(); // name;
			CompanyName companyName = new CompanyName(name, lang, company);
			companyNameRepository.save(companyName);
		}
		
		List<TagDto> tags = companyNewDto.getTags();
		
		for(TagDto tag : tags) {
			Map<String, Object> tagNameMap = tag.getTagName();
			for(Map.Entry<String, Object> entry : tagNameMap.entrySet()) {
				String lang = entry.getKey(); // lang
				String name = (String)entry.getValue(); // name;
				
				List<Tag> tagsOri = tagRepository.findByCompany_IdAndLang(company.getId(), lang);
				if(tagsOri.isEmpty()) {
					Tag newTag = new Tag(name, lang, company);
					tagRepository.save(newTag);
				} else {
					Tag tagOrigin = tagsOri.get(0);
					String oldTagName = tagOrigin.getName();
					String newTagName = oldTagName + "|" + name;
					//System.out.println("Update oldTag:"+oldTagName+"-> newTag:"+newTagName);
					tagOrigin.setName(newTagName);
					tagRepository.save(tagOrigin);
				}
				
			}
		}
		
		ResponseEntity<CompanyDto> response;
		
		List<CompanyName> searchedCompanyNamesLang = companyNameRepository.findByCompany_IdAndLang(company.getId(), language);
		CompanyName searchedCompanyNameLang = searchedCompanyNamesLang.get(0);
		List<Tag> tagsLang = tagRepository.findByCompany_IdAndLang(company.getId(), language);
		CompanyDto dto = new CompanyDto();
		dto.setCompanyName(searchedCompanyNameLang.getName());
		String tagKo = tagsLang.get(0).getName();
		String[] array = tagKo.split("\\|");
		dto.setTags(Arrays.asList(array));
		response = new ResponseEntity<>(dto, HttpStatus.OK);
		return response;
	}

	// 새로운 태그를 등록한다.
	public ResponseEntity<CompanyDto> addTags(String name, List<Map<String, Object>> param, String language) {
		// 회사 이름으로 Company Id 조회 
		List<CompanyName> result = companyNameRepository.findByName(name);
		Company company = result.get(0).getCompany();
		
		// Deserialzed Param
		for(Map<String, Object> tagMap : param) {
			Map<String, String> tagNameMap = (Map)tagMap.get("tag_name");
			for (Map.Entry<String, String> entry : tagNameMap.entrySet()) {
				String lang = entry.getKey(); // lang
				String tagName = (String)entry.getValue(); // name;
				if(tagRepository.findByCompany_IdAndNameAndLang(company.getId(), tagName, lang).isEmpty()) { 
					Tag newTag = new Tag(tagName, lang, company);
					tagRepository.save(newTag);
				}
			}
		}
		
		CompanyDto dto = new CompanyDto();
		List<CompanyName> searchedCompanyNames = companyNameRepository.findByCompany_IdAndLang(company.getId(), language);
		dto.setCompanyName(searchedCompanyNames.get(0).getName());
		
		List<Tag> tags = tagRepository.findByCompany_IdAndLang(company.getId(), language);
		List<String> tagsList = new ArrayList();
		for(Tag tagObj : tags) {
			tagsList.add(tagObj.getName());
		}
		Collections.sort(tagsList);
		dto.setTags(tagsList);
		
		ResponseEntity<CompanyDto> response;
		response = new ResponseEntity<>(dto, HttpStatus.OK);
		return response;
	}

	public ResponseEntity<CompanyDto> deleteTag(String name, String tag, String language) {
		List<CompanyName> result = companyNameRepository.findByName(name);
		Company company = result.get(0).getCompany();
		
		//삭제할 태그를 조회한다.
		List<Tag> deleteTags = tagRepository.findByCompany_IdAndNameContains(company.getId(), tag);
		Tag deleteTag = deleteTags.get(0);
		tagRepository.delete(deleteTag);
		
		ResponseEntity<CompanyDto> response;
		CompanyDto dto = new CompanyDto();
		List<CompanyName> companyName = companyNameRepository.findByCompany_IdAndLang(company.getId(), language);
		List<Tag> langTags = tagRepository.findByCompany_IdAndLang(company.getId(), language);

		dto.setCompanyName(companyName.get(0).getName());
		
		List<String> langTagsList = new ArrayList();;
		for(Tag tagObj : langTags) 
			langTagsList.add(tagObj.getName());
		dto.setTags(langTagsList);
		response = new ResponseEntity<>(dto, HttpStatus.OK);
		return response;
	}

}
