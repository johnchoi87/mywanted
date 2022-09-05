package com.wanted.tag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long>{
	List<Tag> findByCompany_IdAndLang(@Param(value = "companyId") Long companyId, String lang);
	List<Tag> findByCompany_IdAndNameAndLang(@Param(value = "companyId") Long companyId, String name, String lang);
	List<Tag> findByNameContains(String name);
	List<Tag> findByName(String name);
	List<Tag> findByCompany_IdAndNameContains(@Param(value = "companyId") Long companyId, String name);
	
}
