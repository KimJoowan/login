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

import com.example.demo.domain.SignupRequest;

@SpringBootTest
public class MemberServiceTest {
	
	private static final Logger log = LogManager.getLogger(MemberServiceTest.class);
	
	@Autowired
	private MemberService service;
	
	@RepeatedTest(
		    value = 10,
		    name = "{displayName} - {currentRepetition}/{totalRepetitions}"
		)
		@DisplayName("회원가입 서비스 10회 반복 테스트")
		void registerRepeatedTest(RepetitionInfo repetitionInfo) {
		    int current = repetitionInfo.getCurrentRepetition();

		    String uniqueValue = UUID.randomUUID()
		            .toString()
		            .replace("-", "")
		            .substring(0, 12);

		    SignupRequest request = new SignupRequest(
		        "zz_" + uniqueValue,
		        UUID.randomUUID().toString().substring(0, 16),
		        "tt_" + current,
		        "test_" + uniqueValue + "@test.com"
		    );

		    assertThatCode(() -> service.register(request))
		            .doesNotThrowAnyException();
		}
	
	@Test
	public void findById() {
		log.info("==============================================================================================");
		log.info(service.findById("zz"));
		log.info("==============================================================================================");
	}
	
	@Test
	public void delete() {
		String id = "zz";
		service.deleteMember(id);
	}
	

	
	
}
