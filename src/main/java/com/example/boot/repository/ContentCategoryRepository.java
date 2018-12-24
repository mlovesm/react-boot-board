package com.example.boot.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.boot.domain.ContentCategory;
import com.example.boot.domain.VodRepo;

public interface ContentCategoryRepository extends CrudRepository<ContentCategory, Long> {
	List<ContentCategory> findByIdx(long Idx);
}
