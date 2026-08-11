package com.example.demo.service;

import com.example.demo.domain.MemberDto;
import com.example.demo.domain.SignupRequest;

public interface MemberService {
	public void register(SignupRequest request);

	public MemberDto findById(String id);

	void updateMember(MemberDto dto);	
	
	void deleteMember(String id);

	void increaseLoginFailCount(int number);

	void recordSuccess(int num);

}
