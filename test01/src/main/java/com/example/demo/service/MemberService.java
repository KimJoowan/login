package com.example.demo.service;

import com.example.demo.domain.MemberDto;

public interface MemberService {
	public void register(MemberDto Dto);

	public MemberDto findById(String id);

	void updateMember(MemberDto dto);	
	
	void deleteMember(String id);

	void increaseLoginFailCount(int number);

	void recordSuccess(int num);
}
