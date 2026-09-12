package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Random.OptimizedRandom;
import com.example.demo.domain.SignupRequest;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MemberServiceTest {

	private static final Logger log = LogManager.getLogger(MemberServiceTest.class);

	@Autowired
	private MemberService service;
	
	@Test
	void registerSucceedsWithValidRequest() {  
		String id = "bbbbbbbb";
	    String rawPassword = OptimizedRandom.generate(128);
	    String userName = "test-user";
	    String email = "test_01" + "@test.com";
	    
	    SignupRequest request = new SignupRequest(id, rawPassword, userName, email);  
	    assertThatCode(() -> service.register(request)).doesNotThrowAnyException();
	}
	
	@Test
	public void findById() {
		registerSucceedsWithValidRequest();
		
		String id = "bbbbbbbb"; 
		log.info("==============================================================================================");
		log.info(service.existsById(id));
		log.info("==============================================================================================");
	}

	@Test
	public void delete() {
		registerSucceedsWithValidRequest();
		
		String id = "bbbbbbbb";
		log.info("==============================================================================================");
		service.withdrawMember(id);
		log.info("==============================================================================================");
	}

}
