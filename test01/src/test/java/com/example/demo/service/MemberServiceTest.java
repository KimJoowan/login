package com.example.demo.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.domain.MemberDto;

@SpringBootTest
public class MemberServiceTest {
	
	private static final Logger log = LogManager.getLogger(MemberServiceTest.class);
	
	@Autowired
	private MemberService servie;
	
	@Test
	public void register() {
		MemberDto member = new MemberDto(); 
    	member.setId("zz");
    	member.setPassword("tt");
    	member.setUserName("tt");
    	member.setEmail("test@test.com");
    	
		servie.register(member);
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
	
	
	
	
	
	
}
