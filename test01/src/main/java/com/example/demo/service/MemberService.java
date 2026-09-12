package com.example.demo.service;

import com.example.demo.domain.MemberDto;
import com.example.demo.domain.MemberUpdateRequest;
import com.example.demo.domain.SignupRequest;

public interface MemberService {
	public void register(SignupRequest request);

	public MemberDto showMemberInfo(String id);

	void withdrawMember(String id);

	void updateMember(String id, MemberUpdateRequest request);
	
	boolean existsById(String id);
}
