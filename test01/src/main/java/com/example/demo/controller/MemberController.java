package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.domain.MemberDto;
import com.example.demo.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor 
@Log4j2
public class MemberController {
	
	private final MemberService service;
	
	@GetMapping("/signup")
	public String register() {
		return "member/signup";
	}
	
	@PostMapping("/signup")
	public String register2(MemberDto member) {
		service.register(member);
		return "redirect:/";
	}
	
	@GetMapping("/login")
	public String login() {
		return "member/login";
	}
	
	@GetMapping("/check-id")
	public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam("id") String id) {
		
		boolean isDuplicate = (service.findById(id) != null);

		Map<String, Boolean> response = new HashMap<>();
		response.put("isDuplicate", isDuplicate); 
        
		return ResponseEntity.ok(response);
	}	
	
	@GetMapping("/info")
	public String name(Authentication authentication, Model model) {
		
		String id = authentication.getName();
		
		 MemberDto dto = service.findById(id);
		 log.info("dto: {}", dto.getId());
		 
		 model.addAttribute("member", dto); 
		 
		return "member/info";
	}
	
	@PostMapping("/update")
	public String update(Authentication authentication, MemberDto member) {
		String id = authentication.getName();
		member.setId(id);
		service.updateMember(member);
		
		return "redirect:/";
		
	}
	
	@PostMapping("/delete")
	public String delete(Authentication authentication, HttpServletRequest request) {
		String id = authentication.getName();
		service.deleteMember(id);
		request.getSession().invalidate();

		return "redirect:/";
	}
	 
	
	
	
	
	
	
	
	
}