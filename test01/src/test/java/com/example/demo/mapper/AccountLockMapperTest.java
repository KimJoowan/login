package com.example.demo.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.RequiredArgsConstructor;

@SpringBootTest
@RequiredArgsConstructor
public class AccountLockMapperTest {
	
	@Autowired
    private AccountLockMapper accountLockMapper;
   
    @Test
    void findById() {
    	int num = 172;
    	accountLockMapper.findById(num);
    }
  
    @Test
    void isLoginAllowed() {
    	int num = 172;
    	accountLockMapper.isLoginAllowed(num);
    }

    @Test
    void recordSuccess() {
    	accountLockMapper.recordSuccess("test01");
    }
}
