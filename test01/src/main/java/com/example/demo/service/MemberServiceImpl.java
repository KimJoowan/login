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

		int affectedRows = memberMapper.insertMember(member);

		if (affectedRows != 1) {
		    throw new IllegalStateException("회원 등록에 실패했습니다.");
		}
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

		int affectedRows = memberMapper.updateMember(member);

		if (affectedRows != 1) {
		    throw new IllegalArgumentException("수정할 회원을 찾을 수 없습니다.");
		}
	}

	@Override
	public void deleteMember(String id) {
		int affectedRows = memberMapper.deleteMember(id);

		if (affectedRows != 1) {
		    throw new IllegalArgumentException("삭제할 회원을 찾을 수 없습니다.");
		}
	}

	@Override
	public void increaseLoginFailCountById(String id) {
		accountLockMapper.increaseLoginFailCountById(id);
	}

	@Override
	public void recordSuccess(String id) {
		accountLockMapper.recordSuccess(id);
	}

}
