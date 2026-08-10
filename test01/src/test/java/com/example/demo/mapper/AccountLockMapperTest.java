package com.example.demo.mapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.domain.AccountLockDto;

import lombok.RequiredArgsConstructor;

@SpringBootTest
@RequiredArgsConstructor
public class AccountLockMapperTest {
	
	@Autowired
    private AccountLockMapper accountLockMapper;
    
    private static final Logger log = LogManager.getLogger(AccountLockMapperTest.class);
    
    
    @Test
    void increaseLoginFailCount() {
    	AccountLockDto dto = new AccountLockDto();
    	dto.setNumber(82);
    	dto.setFailCount(3);
    	accountLockMapper.increaseLoginFailCount(dto);
    }
    
    @Test
    void findById() {
    	int num = 80;
    	int count = accountLockMapper.findById(num);
    	log.info("count: {}", count);
    }
    
    
    @Test
    void findById_loop() {
    	for(int i=0; i<4; i++) {
    		AccountLockDto dto = new AccountLockDto();
        	dto.setNumber(81);
        	
        	int count = accountLockMapper.findById(dto.getNumber());
        	
        	if(count > 3) {
        		dto.setActive(false);
        	}
        	
        	dto.setFailCount(count+1);
        	
        	accountLockMapper.increaseLoginFailCount(dto);	
    	}

    }
    
    
    @Test
    void recordSuccess() {
    	int num = 83;
    	accountLockMapper.recordSuccess(num);
    }
    
    
    
    
    
    
    
    
    
    
    

}
