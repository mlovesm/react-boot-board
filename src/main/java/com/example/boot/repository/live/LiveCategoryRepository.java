package com.example.boot.repository.live;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.boot.domain.LiveCategory;

public interface LiveCategoryRepository extends JpaRepository<LiveCategory, Long> {
	
	List<LiveCategory> findByIdxIn(List<Long> categoryIdxList);	// �迭 ī�װ�
			
	List<LiveCategory> findByParentIdOrderByIdx(int parentId);	// ���� ī�װ�
		
	List<LiveCategory> findByParentIdOrderByPosition(int parentId);	// ���� ī�װ�
	
	LiveCategory findTopByParentIdOrderByIdxDesc(int parentId);	// ���� ī�װ� �� �� ������ ���
	
	LiveCategory findTopByParentIdOrderByPositionDesc(int parentId);	// position max
		
	Long removeByParentId(int parentId);	// ����ī�װ� ����
	
    @Transactional
	int deleteByParentId(int parentId);
	
}
