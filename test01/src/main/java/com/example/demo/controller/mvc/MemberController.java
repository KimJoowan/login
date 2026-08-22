package com.example.demo.controller.mvc;

import org.springframework.dao.DataIntegrityViolationException;
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
			if (bindingResult.hasErrors()) {
			    return "member/signup";
			}

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

	@GetMapping("/info")
	public String name(@AuthenticationPrincipal UserDetails userDetails, Model model) {
	    if (userDetails == null) {
	        return "redirect:/member/login";
	    }
	    String id = userDetails.getUsername();
	    MemberDto member = service.findById(id);

	    model.addAttribute("id", id);
	    model.addAttribute("memberUpdateRequest", new MemberUpdateRequest(member.getUserName(), member.getEmail()));

	    return "member/info";
	}

	@PostMapping("/update")
	public String update(
	        @AuthenticationPrincipal UserDetails userDetails,
	        @Valid @ModelAttribute MemberUpdateRequest request,
	        BindingResult bindingResult,
	        Model model) {

	    if (bindingResult.hasErrors()) {
	        model.addAttribute("id", userDetails.getUsername());
	        return "member/info";
	    }

	    try {
	    	service.updateMember(userDetails.getUsername(), request);
	    } catch (DataIntegrityViolationException exception) {
	        bindingResult.rejectValue(
	                "email",
	                "duplicate.email",
	                "이미 사용 중인 이메일입니다."
	        );
	        model.addAttribute("id", userDetails.getUsername());
	        return "member/info";
	    }

	    return "redirect:/member/info";
	} 

	@PostMapping("/delete")
	public String delete(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
		String id = userDetails.getUsername();
		service.deleteMember(id);
		
		request.getSession().invalidate();

		return "redirect:/";
	}

}
