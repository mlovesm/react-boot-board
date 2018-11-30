package com.example.boot.domain;

import java.util.List;

import javax.persistence.Column;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class MemberDto {
	
	@Column(nullable = false, unique = true, length=50)
	private String uid;
	
	@Column(nullable = false, length=200)
	private String upw;
	
	@Column(nullable = false, unique = true, length=50)
	private String uemail;
	
	private List<MemberRole> roles;
	
	public Member toEntity() {
		return Member.builder()
				.uid(uid)
				.upw(upw)
				.uemail(uemail)
				.roles(roles)
				.build();
		
	}
	
}
