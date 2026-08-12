package com.example.demo.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.domain.MemberDto;
import com.example.demo.mapper.AccountLockMapper;
import com.example.demo.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
    private final MemberMapper memberMapper;
    private final AccountLockMapper accountLockMapper;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

        MemberDto member = memberMapper.findById(id);

        if (member == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
             
        boolean active = accountLockMapper.findById(member.getNumber());
              
        return User.builder()
                .username(member.getId())
                .password(member.getPassword())
                .roles(member.getRole())
                .accountLocked(!active)
                .build();
    }
}