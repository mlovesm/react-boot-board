package com.example.boot.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.boot.domain.Member;

public interface MemberRepository extends CrudRepository<Member, Long> {

}
