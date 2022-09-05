package com.wanted.tag;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.wanted.company.CompanyDto;
import com.wanted.company.CompanyName;
import com.wanted.company.CompanyNameRepository;
import com.wanted.company.CompanyRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TagService {

	CompanyRepository companyRepository;
	CompanyNameRepository companyNameRepository;
	TagRepository tagRepository;
	
	Logger log = LogManager.getLogger();
	
	@Autowired
	public TagService(	
			CompanyRepository companyRepository,
			CompanyNameRepository companyNameRepository,
			TagRepository tagRepository) {
		this.companyRepository = companyRepository;
		this.companyNameRepository = companyNameRepository;
		this.tagRepository = tagRepository;
	}
	
	public ResponseEntity<List<CompanyDto>> findCompanyByTag(String query, String language) {
		ResponseEntity<List<CompanyDto>> response;
		List<CompanyDto> searchedCompanyList = new ArrayList<>();
		
		List<Tag> tags = tagRepository.findByName(query);
		
		log.info("findCompanyByTag query:"+query+", language:"+language);
		for(Tag tag:tags) {
			Long companyId = tag.getCompany().getId();
			// 헤더 언어로 조회 
			List<CompanyName> companyNames = companyNameRepository.findByCompany_IdAndLang(companyId, language);
			// 언어가 없을경우 노출가능한 언어로 출력
			List<CompanyName> companyNames2 = companyNameRepository.findByCompany_Id(companyId);

			if(companyNames != null && companyNames.size() > 0) {
				CompanyName companyName = companyNames.get(0);
				CompanyDto dto = new CompanyDto();
				dto.setCompanyName(companyName.getName());
				searchedCompanyList.add(dto);
			} else { // 언어가 없을경우 노출가능한 언어로 출력
				CompanyName companyName = companyNames2.get(0);
				CompanyDto dto = new CompanyDto();
				dto.setCompanyName(companyName.getName());
				searchedCompanyList.add(dto);
			}
		}
		response = new ResponseEntity<>(searchedCompanyList, HttpStatus.OK);
		return response;
	}

}
