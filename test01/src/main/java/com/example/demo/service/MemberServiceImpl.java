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
import com.example.demo.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
	private final PasswordEncoder passwordEncoder;

	private final MemberMapper memberMapper;
	
	@Override
	@Transactional
	public void register(SignupRequest request) {
		try { 
			MemberDto member = new MemberDto();
			member.setId(request.id());
			member.setPassword(passwordEncoder.encode(request.password()));
			member.setUserName(request.userName());
			member.setEmail(request.email());
			
		    memberMapper.insertMember(member);

		} catch (DataIntegrityViolationException e) {

		    String constraint = extractConstraint(e);

		    if ("uk_member_id".equals(constraint)) {
		        throw new DuplicateMemberIdException();
		    }

		    if ("uq_member_email".equals(constraint)) {
		        throw new DuplicateEmailException();
		    }

		    throw e;
		}
	}
	
	@Override
	public MemberDto showMemberInfo(String id) {
		return Optional.ofNullable(memberMapper.findById(id)).orElseThrow(MemberNotFoundException::new);
	}

	@Override
	@Transactional
	public void updateMember(String id, MemberUpdateRequest request) {

		try {
		    int affectedRows =
		            memberMapper.updateMember(id, request);

		    if (affectedRows != 1) {
		        throw new MemberNotFoundException();
		    }

		} catch (DataIntegrityViolationException e) {

		    String constraint = extractConstraint(e);

		    if ("uq_member_email".equals(constraint)) {
		        throw new DuplicateEmailException();
		    }
		    
		throw e;
		}
	}

	@Override
	public void withdrawMember(String id) {
		int affectedRows = memberMapper.withdrawMember(id);

		if (affectedRows != 1) {
		    throw new MemberNotFoundException();
		}
	}
	
	private String extractConstraint(DataIntegrityViolationException exception) {

	    Throwable cause = exception.getMostSpecificCause();

	    if (cause instanceof PSQLException psqlException
	            && psqlException.getServerErrorMessage() != null) {

	        return psqlException
	                .getServerErrorMessage()
	                .getConstraint();
	    }
 
	    return null;
	}

	@Override
	public boolean existsById(String id) {
		return memberMapper.existsById(id) > 0;
	}

}
