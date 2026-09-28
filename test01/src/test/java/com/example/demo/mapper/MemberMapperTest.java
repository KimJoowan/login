package com.example.demo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.MemberDto;

import lombok.RequiredArgsConstructor;

@SpringBootTest
@RequiredArgsConstructor
@ActiveProfiles("test")
public class MemberMapperTest {
	
    @Autowired
    private MemberMapper memberMapper;
    
    private static final Logger log = LogManager.getLogger(MemberMapperTest.class);
    
    @Test
    void insertMemberTest() {
    	MemberDto member = new MemberDto();
    	
        member.setId("bbbbbbbb");
        member.setPassword("bbbbbbbb");
        member.setUserName("테스트");
        member.setEmail("bbbbbbbb@a.com");

        int result = memberMapper.insertMember(member);

        assertThat(result).isEqualTo(1);
    }

    @Test
    void findByIdTest() {
    	String id = "bbbbbbbb";
    	
    	MemberDto member = memberMapper.findById(id);
	    	if (member == null) {
	    		log.info("member not found: id={}", id);
	    	} else {
	    		log.info("member: number={}, id={}, userName={}, email={}, role={}",
	    				member.getNumber(), member.getId(), member.getUserName(), member.getEmail(), member.getRole());
	    	}
    }
    
    @Test
    void withdrawMemberTest() {
        String id = "bbbbbbbb";       
        int result = memberMapper.withdrawMember(id);
        log.info("회원탈퇴 결과: {}", result);
    }
    
    @Test
    void getByIdTest() {
        String id = "bbbbbbbb";       
        int result = memberMapper.getById(id);
        log.info("회원조회 결과: {}", result);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
      
}
