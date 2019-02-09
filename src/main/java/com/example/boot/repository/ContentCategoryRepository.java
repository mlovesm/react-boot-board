package com.example.boot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.boot.domain.ContentCategory;

public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Long> {
			
	List<ContentCategory> findByParentIdOrderByIdx(int parentId);	// 상위 카테고리
	
	List<ContentCategory> findByParentIdOrderByPosition(int parentId);	// 하위 카테고리
	
	ContentCategory findTopByParentIdOrderByPositionDesc(int parentId);	// position max
		
	Long removeByParentId(int parentId);	// 하위카테고리 삭제
	
    @Transactional
	int deleteByParentId(int parentId);
	
}
