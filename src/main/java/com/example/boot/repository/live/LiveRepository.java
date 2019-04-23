package com.example.boot.repository.live;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.boot.domain.ContentCategory;
import com.example.boot.domain.LiveCategory;
import com.example.boot.domain.LiveRepo;

@Repository
public interface LiveRepository extends JpaRepository<LiveRepo, Long> {
	
	Page<LiveRepo> findByContentCategory(ContentCategory category, Pageable pageable);
	
	Page<LiveRepo> findByContentCategoryIn(List<LiveCategory> category, Pageable pageable);	
}
