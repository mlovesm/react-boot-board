package com.example.boot.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.boot.domain.ContentCategory;
import com.example.boot.domain.VodRepo;

@Repository
public interface VODRepository extends JpaRepository<VodRepo, Long> {
	
	@Query("select a from VodRepo a where a.vodTitle= :vodTitle")
	Page<VodRepo> findByVodTitle(@Param("vodTitle") String vodTitle, Pageable pageable);
	
	Page<VodRepo> findByContentCategory(ContentCategory category, Pageable pageable);
	
	Page<VodRepo> findByContentCategoryIn(List<ContentCategory> category, Pageable pageable);
	
}
