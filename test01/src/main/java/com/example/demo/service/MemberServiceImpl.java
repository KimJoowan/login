package com.example.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.MemberDto;
import com.example.demo.domain.MemberUpdateRequest;
import com.example.demo.domain.SignupRequest;
import com.example.demo.mapper.AccountLockMapper;
import com.example.demo.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final PasswordEncoder passwordEncoder;

	private final MemberMapper memberMapper;
	private final AccountLockMapper accountLockMapper;

	@Override
	public void register(SignupRequest request) {
		MemberDto member = new MemberDto();

		member.setId(request.id());
		member.setPassword(passwordEncoder.encode(request.password()));
		member.setUserName(request.userName());
		member.setEmail(request.email());

		memberMapper.insertMember(member);
	}

	@Override
	public MemberDto findById(String id) {
		return memberMapper.findById(id);
	}

	@Override
	public void updateMember(String id, MemberUpdateRequest request) {
		MemberDto member = new MemberDto();
		member.setId(id);
		member.setUserName(request.userName());
		member.setEmail(request.email());

		memberMapper.updateMember(member);
	}

	@Override
	public void deleteMember(String id) {
		memberMapper.deleteMember(id);
	}

	@Override
	public void increaseLoginFailCount(int number) {
		accountLockMapper.increaseLoginFailCount(number);
	}

	@Override
	public void recordSuccess(int num) {
		accountLockMapper.recordSuccess(num);
	}

}
