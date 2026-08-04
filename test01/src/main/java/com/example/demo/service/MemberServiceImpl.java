package com.example.demo.service; 

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.MemberDto;
import com.example.demo.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
	private final MemberMapper Mapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void register(MemberDto member) {			
		String password = passwordEncoder.encode(member.getPassword());
		member.setPassword(password);
		
		Mapper.insertMember(member);
	}
	
	@Override
	public MemberDto findById(String id) {
		return Mapper.findById(id);		
	}
	
	
	@Override
	public void updateMember(MemberDto dto) {
		Mapper.updateMember(dto);
	}
	
	@Override
	public void deleteMember(String id) {
		Mapper.deleteMember(id);
	}
	
	
	
	
	
	
	
}
