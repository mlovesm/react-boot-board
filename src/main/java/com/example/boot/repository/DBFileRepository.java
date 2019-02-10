package com.example.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.boot.domain.DBFile;

@Repository
public interface DBFileRepository extends JpaRepository<DBFile, String> {
	
}
