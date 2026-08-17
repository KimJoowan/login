package com.example.demo.controller;

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
		try {
			service.register(request);
		} catch (DataIntegrityViolationException e) {
			bindingResult.rejectValue("id", "duplicate.id", "이미 사용 중인 아이디입니다.");
			return "member/signup";

		}
		return "redirect:/member/login";
	}

	@GetMapping("/login")
	public String login() {
		return "member/login";
	}

	@GetMapping("/check-id")
	public ResponseEntity<Map<String, Object>> checkUsername(String id) {
		if (id == null || !id.matches("^[a-zA-Z0-9_]{4,30}$")) {
			return ResponseEntity.badRequest().body(
					Map.of("valid", false, "isDuplicate", false, "message", "아이디는 영문, 숫자, 밑줄을 사용해 4~30자로 입력해주세요."));
		}

		boolean duplicate = service.findById(id) != null;

		return ResponseEntity.ok(Map.of("valid", true, "isDuplicate", duplicate, "message",
				duplicate ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다."));
	}

	@GetMapping("/info")
	public String name(Authentication authentication, Model model) {
		String id = authentication.getName();
		MemberDto member = service.findById(id);

		model.addAttribute("id", id);
		model.addAttribute("memberUpdateRequest", new MemberUpdateRequest(member.getUserName(), member.getEmail()));

		return "member/info";
	}

	@PostMapping("/update")
	public String update(Authentication authentication,
			@Valid @ModelAttribute("memberUpdateRequest") MemberUpdateRequest request, BindingResult bindingResult,
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