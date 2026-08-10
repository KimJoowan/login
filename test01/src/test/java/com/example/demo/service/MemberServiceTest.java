package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.domain.MemberDto;

@SpringBootTest
public class MemberServiceTest {
	
	private static final Logger log = LogManager.getLogger(MemberServiceTest.class);
	
	@Autowired
	private MemberService servie;
	
	@RepeatedTest(value = 10, name = "{displayName} - {currentRepetition}/{totalRepetitions}")
    @DisplayName("회원가입 서비스 10회 반복 테스트")
    public void registerRepeatedTest(RepetitionInfo repetitionInfo) {
        int current = repetitionInfo.getCurrentRepetition();

        // Given (반복마다 다른 ID/이메일 적용)
        MemberDto member = new MemberDto(); 
        member.setId("zz_" + current);
        
        String randomStr = UUID.randomUUID().toString().substring(0, 16);
        member.setPassword(randomStr);
        
        member.setUserName("tt_" + current);
        member.setEmail("test" + current + "@test.com");

        // When & Then (예외 없이 정상 실행되는지 검증)
        assertThatCode(() -> servie.register(member))
                .doesNotThrowAnyException();
    }
	
	@Test
	public void findById() {
		log.info("==============================================================================================");
		log.info(servie.findById("zz"));
		log.info("==============================================================================================");
	}

	
	@Test
	public void update() {
		MemberDto dto = new MemberDto();   
    	dto.setId("aa");
    	dto.setUserName("bb");
    	dto.setEmail("a01055136572@gmail.com");
    	
        servie.updateMember(dto);
	}
	
	@Test
	public void delete() {
		String id = "zz";
		servie.deleteMember(id);
	}
	
	@Test
	public void increaseLoginFailCount() {
		servie.increaseLoginFailCount(82);
	}
	
	@Test
	public void recordSuccess() {
		servie.recordSuccess(82);		
	
	}
	
	
	
}
