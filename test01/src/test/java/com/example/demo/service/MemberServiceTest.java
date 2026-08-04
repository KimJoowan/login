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
    	member.setId("1");
    	member.setPassword("sss");
    	member.setUserName("kimddo");
    	member.setEmail("test@test.com");
    	member.setActive(true);
    	
		servie.register(member);
	}
	
	@Test
	public void findById() {
		log.info("==============================================================================================");
		log.info(servie.findById("aa"));
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
		servie.deleteMember("1");
	}
	
	
	
	
}
