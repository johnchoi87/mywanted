package com.wanted;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.wanted.company.Company;
import com.wanted.company.CompanyDto;
import com.wanted.company.CompanyName;
import com.wanted.company.CompanyNameRepository;
import com.wanted.company.CompanyNewDto;
import com.wanted.company.CompanyRepository;
import com.wanted.tag.Tag;
import com.wanted.tag.TagRepository;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.MethodOrderer.*;
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j

public class ControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private CompanyNameRepository companyNameRepository;
	@Autowired
	private TagRepository tagRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	Logger log = LogManager.getLogger();
	@BeforeAll
	void loadDBData() {
		Path filePath = Paths.get("wanted_temp_data.csv");
		Reader reader;
		log.info("initialize DB");
		try {
			log.info("load data from csv : "+filePath);
			reader = Files.newBufferedReader(filePath);
			try (CSVReader csvReader = new CSVReader(reader)) {
				String[] line;
				while ((line = csvReader.readNext()) != null) {
					Company company = new Company();
					
					try {
						company = companyRepository.save(company);
						CompanyName companyName;
						if(!line[0].isEmpty()) {
						companyName= new CompanyName(line[0], "ko", company);
						companyNameRepository.save(companyName);
						}
						if(!line[1].isEmpty()) {
						companyName = new CompanyName(line[1], "en", company);
						companyNameRepository.save(companyName);
						}
						if(!line[2].isEmpty()) {
						companyName = new CompanyName(line[2], "ja", company);
						companyNameRepository.save(companyName);
						}

						if(!line[3].isEmpty()) {
							insertTags(line[3], "ko", company);
						}
						if(!line[4].isEmpty()) {
							insertTags(line[4], "en", company);
						}
						if(!line[5].isEmpty()) {
							insertTags(line[5], "ja", company);
						}						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("=== End Loading ===");
	}
	
	private void insertTags(String tags, String lang, Company company) {
		String[] tagArray = tags.split("\\|");
		for(String tagStr : tagArray) {
			Tag tag = new Tag(tagStr, lang, company);
			tagRepository.save(tag);
		}
	}

	@DisplayName("1. 회사명 자동완성")
	@Test
	@Order(1)
	void testCompanyNameAutoComplete() throws Exception{
		/*
	    1. 회사명 자동완성
	    회사명의 일부만 들어가도 검색이 되어야 합니다.
	    header의 x-wanted-language 언어값에 따라 해당 언어로 출력되어야 합니다.
	    */
		log.info("=================================");
		log.info("= 1. 회사명 자동완성 ");
		log.info("= 테스트 시작");
	    MvcResult result = mockMvc.perform(get("/search?query=링크")
	            .header("x-wanted-language", "ko")
	            .contentType(MediaType.APPLICATION_JSON))
	    	    .andExpect(status().isOk())
	    	    .andReturn();
	    	    
	    String searched_companies = result.getResponse().getContentAsString();
	    log.info("결과값: "+searched_companies);
	    Assertions.assertEquals("["
	    		+ "{\"company_name\":\"주식회사 링크드코리아\"},"
	    		+ "{\"company_name\":\"스피링크\"}]",
	    		searched_companies);
	    log.info("= 테스트 성공 =");
	}
	

	@DisplayName("2.회사 이름으로 회사 검색")
	@Test
	@Order(2)
	void testCompanySearch() throws Exception{
		/*
		 * 2. 회사 이름으로 회사 검색
		 * header의 x-wanted-language 언어값에 따라 해당 언어로 출력되어야 합니다.
		 */
		log.info("=================================");
		log.info("= 2.회사 이름으로 회사 검색 ");
		log.info("= 테스트 시작");
		log.info("= 2.1 NORMAL TEST  ");
	    MvcResult result = mockMvc.perform(get("/companies/Wantedlab")
        .header("x-wanted-language", "ko")
        .contentType(MediaType.APPLICATION_JSON))
	    .andExpect(status().isOk())
	    .andReturn();
	    
	    String content = result.getResponse().getContentAsString();
	    //System.out.println(content);
	    Assertions.assertEquals(
	    		"{\"company_name\":\"원티드랩\","
	    		+ "\"tags\":[\"태그_4\",\"태그_20\",\"태그_16\"]}", 
	    		content);
	    log.info("= 2.1 검색된 회사가 없는경우 404를 리턴합니다.  ");
	    // 검색된 회사가 없는경우 404를 리턴합니다.
	    result = mockMvc.perform(get("/companies/없는회사")
	    		.header("x-wanted-language", "ko")
	    	    .contentType(MediaType.APPLICATION_JSON))
	    	    .andExpect(status().is(404))
	    	    .andReturn();
	    log.info("= 테스트 성공 ");
		
	}
	
	@DisplayName("3. 새로운 회사 추가")
	@Test
	@Order(3)
	void testNewCompany() throws Exception{
	    /*
	    3.  새로운 회사 추가
	    새로운 언어(tw)도 같이 추가 될 수 있습니다.
	    저장 완료후 header의 x-wanted-language 언어값에 따라 해당 언어로 출력되어야 합니다.
	    */
		log.info("=================================");
		log.info("= 3. 새로운 회사 추가 ");
		log.info("= 테스트 시작");
		log.info("= 새로운 언어(tw)도 같이 추가 될 수 있습니다.");
		log.info("= 저장 완료후 header의 x-wanted-language 언어값에 따라 해당 언어로 출력되어야 합니다.");
		
		CompanyNewDto companyNewDto = new CompanyNewDto();
		// Create Input Parameter 
		log.info("= Create Input Parameter");
		Map<String, String> companyNameObject = new HashMap<>();
		companyNameObject.put("ko", "라인 프레쉬");
		companyNameObject.put("tw", "LINE FRESH");
		companyNameObject.put("en", "LINE FRESH");
		List<Map<String, Object>> tagsObject = new ArrayList<>();
		Map<String, Object> tagObject = new HashMap<>();
		Map<String, String> tagNameObject = new HashMap<>();
		tagNameObject.put("ko", "태그_1");
		tagNameObject.put("tw", "tag_1");
		tagNameObject.put("en", "tag_1");
		tagObject.put("tag_name", tagNameObject);
		tagsObject.add(tagObject);
		
		tagObject = new HashMap<>();
		tagNameObject = new HashMap<>();
		tagNameObject.put("ko", "태그_8");
		tagNameObject.put("tw", "tag_8");
		tagNameObject.put("en", "tag_8");
		tagObject.put("tag_name", tagNameObject);
		tagsObject.add(tagObject);
		
		tagObject = new HashMap<>();
		tagNameObject = new HashMap<>();
		tagNameObject.put("ko", "태그_15");
		tagNameObject.put("tw", "tag_15");
		tagNameObject.put("en", "tag_15");
		tagObject.put("tag_name", tagNameObject);
		tagsObject.add(tagObject);
		
		Map<String, Object> input = new HashMap<>();
		input.put("company_name", companyNameObject);
		input.put("tags", tagsObject);
		
		log.info("= Requuest Create Company (POST)");
		MvcResult result = mockMvc.perform(post("/companies")
				.header("x-wanted-language", "tw")
				.content(objectMapper.writeValueAsString(input))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn();

	    String response = result.getResponse().getContentAsString();
	    System.out.println(response);
	    Assertions.assertEquals(
	    		"{\"company_name\":\"LINE FRESH\",\"tags\":[\"tag_1\",\"tag_8\",\"tag_15\"]}", 
	    		response);
	    log.info("= 테스트 성공 ");
	    log.info("=======================");
	}
	

	@DisplayName("4. 태그명으로 회사 검색")
	@Test
	@Order(4)
	void testSearchTagName() throws Exception{
		/*
		    4.  태그명으로 회사 검색
		    태그로 검색 관련된 회사가 검색되어야 합니다.
		    다국어로 검색이 가능해야 합니다.
		    일본어 태그로 검색을 해도 language가 ko이면 한국 회사명이 노출이 되어야 합니다.
		    ko언어가 없을경우 노출가능한 언어로 출력합니다.
		    동일한 회사는 한번만 노출이 되어야합니다.
		 */
		log.info("=================================");
		log.info("= 4. 태그명으로 회사 검색");
		log.info("= 테스트 시작");
		
		//System.out.println("testSearchTagName");
	    MvcResult result = mockMvc.perform(get("/tags?query=タグ_22")
        .header("x-wanted-language", "ko")
        .contentType(MediaType.APPLICATION_JSON))
	    .andExpect(status().isOk())
	    .andReturn();
	    
	    String contents = result.getResponse().getContentAsString();
	    log.info("= 테스트 결과 : "+contents);
	    Assertions.assertEquals("[{\"company_name\":\"딤딤섬 대구점\"},{\"company_name\":\"마이셀럽스\"},{\"company_name\":\"Rejoice Pregnancy\"},{\"company_name\":\"삼일제약\"},{\"company_name\":\"투게더앱스\"}]",
	    		contents);
	    log.info("= 테스트 성공 ");
	    log.info("=======================");
	}
	

	@DisplayName("5. 회사 태그 정보 추가")
	@Rollback(false) 
	@Test
	@Order(5)
	void testNewTag() throws Exception{
	    /*
	    5.  회사 태그 정보 추가
	    저장 완료후 header의 x-wanted-language 언어값에 따라 해당 언어로 출력되어야 합니다.
	    */
		log.info("=================================");
		log.info("= 5. 회사 태그 정보 추가");
		log.info("= 테스트 시작");
		Map<String, Object> input = new HashMap<>();
		ArrayList<Map<String, Object>> tags = new ArrayList<>();
		Map<String, Object> tagObject = new HashMap<>();
		Map<String, String> tagNameObject = new HashMap<>();
		tagNameObject.put("ko", "태그_50");
		tagNameObject.put("jp", "タグ_50");
		tagNameObject.put("en", "tag_50");
		tagObject.put("tag_name", tagNameObject);
		tags.add(tagObject);
		
		Map<String, Object> tagObject2 = new HashMap<>();
		Map<String, String> tagNameObject2 = new HashMap<>();
		tagNameObject2 = new HashMap<>();
		tagNameObject2.put("ko", "태그_4");
		tagNameObject2.put("jp", "タグ_4");
		tagNameObject2.put("en", "tag_4");
		tagObject2.put("tag_name", tagNameObject2);
		tags.add(tagObject2);
		
		MvcResult result = mockMvc.perform(put("/companies/원티드랩/tags")
				.header("x-wanted-language", "en")
				.content(objectMapper.writeValueAsString(tags))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn();
		
		String contents = result.getResponse().getContentAsString();
		log.info("= 테스트 결과 : "+contents);
		Assertions.assertEquals("{\"company_name\":\"Wantedlab\",\"tags\":[\"tag_16\",\"tag_20\",\"tag_4\",\"tag_50\"]}",
				contents);
	    log.info("= 테스트 성공 ");
	    log.info("=======================");
	}
	

	@DisplayName("6. 회사 태그 정보 삭제")
	@Test
	@Order(6)
	void testDeleteTag()throws Exception{
		/*    
		 * 6.  회사 태그 정보 삭제
		 * 저장 완료후 header의 x-wanted-language 언어값에 따라 해당 언어로 출력되어야 합니다.
		 */
		log.info("=================================");
		log.info("= 6. 회사 태그 정보 삭제");
		log.info("= 테스트 시작");
//		MvcResult result = mockMvc.perform(delete("/companies/원티드랩/tags/태그_16")
		MvcResult result = mockMvc.perform(delete("/companies/원티드랩/tags/tag_16")
				.header("x-wanted-language", "en")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn();
		
		String contents = result.getResponse().getContentAsString();
		log.info("= 테스트 결과 : "+contents);
		Assertions.assertEquals("{\"company_name\":\"Wantedlab\",\"tags\":[\"tag_4\",\"tag_20\",\"tag_50\"]}",
				contents);
	    log.info("= 테스트 성공 ");
	    log.info("=======================");
	}
}
