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
@Transactional
public class MemberMapperTest {
	
    @Autowired
    private MemberMapper memberMapper;
    
    private static final Logger log = LogManager.getLogger(MemberMapperTest.class);

    @Test
    void findByIdTest() {
    	String id = "aaaaaaaa";
    	
    	MemberDto member = memberMapper.findById(id);
	    	if (member == null) {
	    		log.info("member not found: id={}", id);
	    	} else {
	    		log.info("member: number={}, id={}, userName={}, email={}, role={}",
	    				member.getNumber(), member.getId(), member.getUserName(), member.getEmail(), member.getRole());
	    	}
    }
    
    @Test
    void insertMemberTest() {
    	MemberDto member = new MemberDto();
    	
    	String uniqueValue = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12);

        member.setId("test_" + uniqueValue);
        member.setPassword("encoded-test-password");
        member.setUserName("테스트");
        member.setEmail(uniqueValue + "@test.com");

        int result = memberMapper.insertMember(member);

        assertThat(result).isEqualTo(1);
    }
    
    @Test
    void deleteMemberTest() {
        String id = "zz";
        
        int result = memberMapper.deleteMember(id);
        log.info("회원탈퇴 결과: {}", result);
    }
    
    
    @Test
    void updateMemberTest() {   	  
    	MemberDto dto = new MemberDto();   
    	dto.setId("ee");
    	dto.setUserName("bb");
    	dto.setEmail("a01055136572@gmail.com");
    	
        memberMapper.updateMember(dto);
    }
    
    
    
    
    
    
    
    
      
}
