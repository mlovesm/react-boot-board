package com.example.boot.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boot.domain.ContentCategory;

public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Long> {
	List<ContentCategory> findByIdx(long Idx, Pageable pageable);
}
