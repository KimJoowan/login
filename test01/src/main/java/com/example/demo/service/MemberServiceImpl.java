package com.example.demo.service; 

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.AccountLockDto;
import com.example.demo.domain.MemberDto;
import com.example.demo.mapper.AccountLockMapper;
import com.example.demo.mapper.MemberMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
	
	private static final Logger log = LogManager.getLogger(MemberServiceImpl.class);
	
	private final PasswordEncoder passwordEncoder;
	
	private final MemberMapper memberMapper;
	private final AccountLockMapper accountLockMapper;
	

	@Override
	public void register(MemberDto member) {			
		String password = passwordEncoder.encode(member.getPassword());
		member.setPassword(password);
		
		memberMapper.insertMember(member);
	}
	
	@Override
	public MemberDto findById(String id) {
		return memberMapper.findById(id);		
	}
	
	
	@Override
	public void updateMember(MemberDto dto) {
		memberMapper.updateMember(dto);
	}
	
	@Override
	public void deleteMember(String id) {
		memberMapper.deleteMember(id);
	}

	@Override
	public void increaseLoginFailCount(int number) {
		AccountLockDto dto = new AccountLockDto();  
		
		int count = accountLockMapper.findById(number); 
		
		if(count > 3) {
    		dto.setActive(false);
    	}
		
		dto.setNumber(number);
		dto.setFailCount(count+1);
		
		accountLockMapper.increaseLoginFailCount(dto);		
	}
	
}
