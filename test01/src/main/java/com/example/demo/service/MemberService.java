package com.example.demo.service;

import com.example.demo.domain.MemberDto;
import com.example.demo.domain.MemberUpdateRequest;
import com.example.demo.domain.SignupRequest;

public interface MemberService {
	public void register(SignupRequest request);

	public MemberDto findById(String id);

	void updateMember(String id, MemberUpdateRequest request);
	
	void deleteMember(String id);

	void increaseLoginFailCountById(String id);

	void recordSuccess(String id);
	
	

}
