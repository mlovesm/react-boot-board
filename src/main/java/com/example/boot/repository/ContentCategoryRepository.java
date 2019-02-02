package com.example.boot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boot.domain.ContentCategory;

public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Long> {
			
	List<ContentCategory> findByParentId(int parentId);
	
	long countByParentId(int parentId);
	
}
