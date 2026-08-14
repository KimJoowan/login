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
    void increaseLoginFailCount() {
    	int number = 172;
    	accountLockMapper.increaseLoginFailCount(number);
    }
    
    @Test
    void findById_loop() {
    	for(int i=0; i<6; i++) {
    		int number = 172;
        	accountLockMapper.increaseLoginFailCount(number);	
    	}

    }
    
    @Test
    void findById() {
    	int num = 172;
    	accountLockMapper.findById(num);
    }
   
    @Test
    void recordSuccess() {
    	int num = 172;
    	accountLockMapper.recordSuccess(num);
    }
    
    @Test
    void isLoginAllowed() {
    	int num = 172;
    	accountLockMapper.isLoginAllowed(num);
    }

}
