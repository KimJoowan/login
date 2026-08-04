package com.example.demo.mapper;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.domain.MemberDto;

import lombok.RequiredArgsConstructor;


@SpringBootTest
@RequiredArgsConstructor
public class MemberMapperTest {
	
    @Autowired
    private MemberMapper memberMapper;
    
    private static final Logger log = LogManager.getLogger(MemberMapperTest.class);

    @Test
    void selectAllTest() {
        List<MemberDto> list = memberMapper.selectAll();
        log.info("조회된 데이터 개수: {}", list.size());

        for (MemberDto member : list) {
        	log.info("member:",member);
        }
    }
    
    @Test
    void findByIdTest() {
    	String id = "aa";
    	
    	MemberDto member = memberMapper.findById(id);
    	log.info(member);
    }
      
    @Test
    void insertMemberTest() {
    	MemberDto member = new MemberDto();
    	member.setId("1");
    	member.setPassword("sss");
    	member.setUserName("kimof");
    	member.setEmail("test@test.com");
    	member.setActive(true);

    	int result = memberMapper.insertMember(member);
    	log.info("회원가입 결과: {}", result);
    }
    
    @Test
    void deleteMemberTest() {
        String id = "a";
        
        int result = memberMapper.deleteMember(id);
        log.info("회원탈퇴 결과: {}", result);
    }
    
    
    @Test
    void updateMemberTest() {   	  
    	MemberDto dto = new MemberDto();   
    	dto.setId("aa");
    	dto.setUserName("bb");
    	dto.setEmail("a01055136572@gmail.com");
    	
        memberMapper.updateMember(dto);
    }
    
    
    
    
    
    
    
    
      
}