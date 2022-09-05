package com.wanted.company;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CompanyNameRepository extends JpaRepository<CompanyName, Long>{

	List<CompanyName> findByName(String name);

	List<CompanyName> findByNameContains(String query);
	
	List<CompanyName> findByCompany_IdAndLang(@Param(value = "companyId") Long companyId, String lang);
	List<CompanyName> findByCompany_Id(@Param(value = "companyId") Long companyId);

}
