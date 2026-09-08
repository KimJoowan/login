package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.mapper.AccountLockMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginRecordService {

    private final AccountLockMapper accountLockMapper;

    public void recordLoginSuccess(String id) {
        try {
            accountLockMapper.recordSuccess(id);
        } catch (Exception e) {
            log.error("로그인 성공 후 계정 잠금 초기화 실패 - id={}", id, e);
        }
    }
    
   
    @Transactional
    public void recordFailure(String id) {
        accountLockMapper.resetIfExpired(id);
        accountLockMapper.increaseLoginFailCountById(id);
    }
    
    
    
}
