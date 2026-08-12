package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.domain.MemberDto;
import com.example.demo.domain.MemberUpdateRequest;
import com.example.demo.domain.SignupRequest;
import com.example.demo.service.MemberService;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Log4j2
public class MemberController {

	private final MemberService service;

	@GetMapping("/signup")
	public String register(Model model) {
		model.addAttribute("signupRequest", new SignupRequest("", "", "", ""));

		return "member/signup";
	}

	@PostMapping("/signup")
	public String register(@Valid @ModelAttribute("signupRequest") SignupRequest request, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return "member/signup";
		}

		if (service.findById(request.id()) != null) {
			bindingResult.rejectValue("id", "duplicate.id", "이미 사용 중인 아이디입니다.");
			return "member/signup";
		}

		try {
		    service.register(request);
		} catch (DataIntegrityViolationException exception) {
		    log.warn("중복 회원가입 요청: {}", request.id());

		    bindingResult.rejectValue(
		        "id",
		        "duplicate.id",
		        "이미 사용 중인 아이디입니다."
		    );

		    return "member/signup";
		}
	
		return "redirect:/member/login";
	}

	@GetMapping("/login")
	public String login() {
		return "member/login";
	}

	@GetMapping("/check-id")
	public ResponseEntity<Map<String, Boolean>> checkUsername(String id) {

		boolean isDuplicate = (service.findById(id) != null);

		Map<String, Boolean> response = new HashMap<>();
		response.put("isDuplicate", isDuplicate);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/info")
	public String name(Authentication authentication, Model model) {
		String id = authentication.getName();
		MemberDto member = service.findById(id);
		
		model.addAttribute("id", id);
	    model.addAttribute(
	        "memberUpdateRequest",
	        new MemberUpdateRequest(
	            member.getUserName(),
	            member.getEmail()
	        )
	    );
		
		return "member/info";
	}

	@PostMapping("/update")
	public String update(
	        Authentication authentication,
	        @Valid @ModelAttribute("memberUpdateRequest")
	        MemberUpdateRequest request,
	        BindingResult bindingResult,
	        Model model) {

	    String id = authentication.getName();

	    if (bindingResult.hasErrors()) {
	        model.addAttribute("id", id);
	        return "member/info";
	    }

	    service.updateMember(id, request);
	    return "redirect:/member/info";
	}

	@PostMapping("/delete")
	public String delete(Authentication authentication, HttpServletRequest request) {
		String id = authentication.getName();
		service.deleteMember(id);
		request.getSession().invalidate();

		return "redirect:/";
	}

}