package com.example.boot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boot.domain.VodRepo;

public interface VODRepository extends JpaRepository<VodRepo, Long> {
	List<VodRepo> findByRegId(String regId);
//	List<VodRepo> findByCategoryIdx(String categoryIdx);
}
