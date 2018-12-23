package com.example.boot.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.boot.domain.VodRepo;

public interface VODRepository extends CrudRepository<VodRepo, Long> {
	List<VodRepo> findByIdx(long idx);
}
