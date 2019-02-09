package com.example.boot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.boot.domain.ContentCategory;

public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Long> {
			
	List<ContentCategory> findByParentIdOrderByIdx(int parentId);	// ���� ī�װ�
	
	List<ContentCategory> findByParentIdOrderByPosition(int parentId);	// ���� ī�װ�
	
	ContentCategory findTopByParentIdOrderByPositionDesc(int parentId);	// position max
		
	Long removeByParentId(int parentId);	// ����ī�װ� ����
	
    @Transactional
	int deleteByParentId(int parentId);
	
}
