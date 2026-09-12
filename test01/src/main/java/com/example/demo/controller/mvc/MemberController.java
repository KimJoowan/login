package com.example.demo.controller.mvc;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.example.demo.exception.DuplicateEmailException;
import com.example.demo.exception.DuplicateMemberIdException;
import com.example.demo.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

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

		try {
			memberService.register(request);

		} catch (DuplicateMemberIdException e) {
			bindingResult.rejectValue("id", "duplicate", e.getMessage());
			return "member/signup";

		} catch (DuplicateEmailException e) {
			bindingResult.rejectValue("email", "duplicate", e.getMessage());
			return "member/signup";
		}

		return "redirect:/member/login";
	}

	@GetMapping("/login")
	public String login() {
		return "member/login";
	}

	@GetMapping("/info")
	public String showMemberInfo(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		
		if (userDetails == null) {
			return "redirect:/member/login";
		}

		String id = userDetails.getUsername();
		MemberDto member = memberService.showMemberInfo(userDetails.getUsername());

		model.addAttribute("id", id);
		model.addAttribute("memberUpdateRequest", new MemberUpdateRequest(member.getUserName(), member.getEmail()));

		return "member/info";
	}

	@PostMapping("/update")
	public String update(@AuthenticationPrincipal UserDetails userDetails,
			@Valid @ModelAttribute("memberUpdateRequest") MemberUpdateRequest request, BindingResult bindingResult,
			Model model) {

		String id = userDetails.getUsername();
		model.addAttribute("id", id);

		if (bindingResult.hasErrors()) {
			return "member/info";
		}

		try {
			memberService.updateMember(id, request);
		} catch (DuplicateEmailException exception) {
			bindingResult.rejectValue("email", "duplicate", exception.getMessage());
			return "member/info";
		}

		return "redirect:/member/info";
	}

	@PostMapping("/delete")
	public String delete(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
		String id = userDetails.getUsername();
		memberService.withdrawMember(id);

		request.getSession().invalidate();

		return "redirect:/";
	}

}
