package com.example.demo.service;

import java.util.Optional;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.MemberDto;
import com.example.demo.domain.MemberUpdateRequest;
import com.example.demo.domain.SignupRequest;
import com.example.demo.exception.DuplicateEmailException;
import com.example.demo.exception.DuplicateMemberIdException;
import com.example.demo.exception.MemberNotFoundException;
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
	@Transactional
	public void register(SignupRequest request) {
		try {
			MemberDto member = new MemberDto();

			member.setId(request.id());
			member.setPassword(passwordEncoder.encode(request.password()));
			member.setUserName(request.userName());
			member.setEmail(request.email());

			int affectedRows = memberMapper.insertMember(member);

			if (affectedRows != 1) {
				throw new IllegalStateException("회원 등록에 실패했습니다.");
			}

			int accountLockRows = accountLockMapper.insertAccountLock(member.getNumber());

			if (accountLockRows != 1) {
				throw new IllegalStateException("계정 잠금 정보 등록에 실패했습니다.");
			}

		} catch (DataIntegrityViolationException e) {
			Throwable cause = e.getMostSpecificCause();

			if (cause instanceof PSQLException psqlException && psqlException.getServerErrorMessage() != null) {

				String message = psqlException.getServerErrorMessage().getConstraint();

				if (message != null && message.contains("uk_member_id")) {
					throw new DuplicateMemberIdException();
				}

				if (message != null && message.contains("uq_member_email")) {
					throw new DuplicateEmailException();
				}
			}

			throw e;
		}
	}

	@Override
	public MemberDto findById(String id) {
		return Optional.ofNullable(memberMapper.findById(id)).orElseThrow(MemberNotFoundException::new);
	}

	@Override
	@Transactional
	public void updateMember(String id, MemberUpdateRequest request) {

		try {
			int affectedRows = memberMapper.updateMember(id, request);

			if (affectedRows != 1) {
				throw new MemberNotFoundException();
			}

		} catch (DataIntegrityViolationException e) {

			Throwable cause = e.getMostSpecificCause();

			if (cause instanceof PSQLException psqlException && psqlException.getServerErrorMessage() != null) {

				String constraint = psqlException.getServerErrorMessage().getConstraint();

				if ("uq_member_email".equals(constraint)) {
					throw new DuplicateEmailException();
				}
			}

			throw e;
		}
	}

	@Override
	public void withdrawMember(String id) {
		int affectedRows = memberMapper.withdrawMember(id);

		if (affectedRows != 1) {
			throw new IllegalArgumentException("삭제할 회원을 찾을 수 없습니다.");
		}
	}

}
