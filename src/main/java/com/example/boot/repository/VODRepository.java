package com.example.boot.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.boot.domain.VodRepo;

@Repository
public interface VODRepository extends JpaRepository<VodRepo, Long> {
//	List<VodRepo> findByCategoryIdx(Long categoryIdx);
}
