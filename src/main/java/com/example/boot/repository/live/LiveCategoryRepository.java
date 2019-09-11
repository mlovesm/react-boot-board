package com.example.boot.repository.live;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.boot.domain.LiveCategory;

public interface LiveCategoryRepository extends JpaRepository<LiveCategory, Long> {
	
	List<LiveCategory> findByIdxIn(List<Long> categoryIdxList);	// 배열 카테고리
			
	List<LiveCategory> findByParentIdOrderByIdx(int parentId);	// 상위 카테고리
		
	List<LiveCategory> findByParentIdOrderByPosition(int parentId);	// 하위 카테고리
	
	LiveCategory findTopByParentIdOrderByIdxDesc(int parentId);	// 상위 카테고리 중 맨 마지막 노드
	
	LiveCategory findTopByParentIdOrderByPositionDesc(int parentId);	// position max
		
	Long removeByParentId(int parentId);	// 하위카테고리 삭제
	
    @Transactional
	int deleteByParentId(int parentId);
	
}