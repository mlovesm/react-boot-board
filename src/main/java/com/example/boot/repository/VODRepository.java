package com.example.boot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.boot.domain.VodRepo;

public interface VODRepository extends JpaRepository<VodRepo, Long> {
//	Page<VodRepo> findByCategoryIdx(int categoryIdx, Pageable pageable);
}
