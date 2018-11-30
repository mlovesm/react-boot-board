package com.example.boot;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.boot.domain.Member;
import com.example.boot.domain.MemberRole;
import com.example.boot.repository.MemberRepository;

import lombok.extern.java.Log;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class MemberRepositoryTest {
	@Autowired
	MemberRepository memberRepository;
	
	@Test
	public void insertTest() {
		for(int i=0; i<100; i++) {
			
			MemberRole role = new MemberRole();
			if(i <= 80) {
				role.setRoleName("BASIC");
			}else if(i <= 90) {
				role.setRoleName("MANAGER");
			}else {
				role.setRoleName("ADMIN");
			}
			Member member= Member.builder()
				.uid("user" + i)
				.upw("pw" + i)
				.uemail("hihi@" + i)
				.roles(Arrays.asList(role))
				.build();
			
			memberRepository.save(member);
		}
	}
	
	@Test
	public void testMember() {
		Optional<Optional<Member>> result = Optional.ofNullable(memberRepository.findById(85L));
		result.ifPresent(member -> log.info("member " + member));
	}
}
