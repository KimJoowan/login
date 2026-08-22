package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.SignupRequest;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MemberServiceTest {

	private static final Logger log = LogManager.getLogger(MemberServiceTest.class);

	@Autowired
	private MemberService service;
	String id = "aaaaaaaa";

	@Test
	void registerSucceedsWithValidRequest(String id) {  
	    String rawPassword = "password_";
	    String userName = "test-user";
	    String email = "test_" + "@test.com";
	    
	    SignupRequest request = new SignupRequest(id, rawPassword, userName, email);  
	    assertThatCode(() -> service.register(request)).doesNotThrowAnyException();
	}

	@Test
	public void findById() {
		registerSucceedsWithValidRequest(id);
		log.info("==============================================================================================");
		log.info(service.findById(id));
		log.info("==============================================================================================");
	}

	@Test
	public void existsById() {
		registerSucceedsWithValidRequest(id);
		log.info("==============================================================================================");
		log.info(service.existsById("aaaaaaaa"));
		log.info("==============================================================================================");
	}

	@Test
	public void delete() {
		registerSucceedsWithValidRequest(id);
		String id = "aaaaaaaa";
		service.deleteMember(id);
	}

}
