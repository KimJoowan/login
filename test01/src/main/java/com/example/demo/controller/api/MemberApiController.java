package com.example.demo.controller.api;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberApiController {

	private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,30}$");

	private final MemberService service;

	@GetMapping("/check-id")
	public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam String id) {

		if (!ID_PATTERN.matcher(id).matches()) {
			return ResponseEntity.badRequest().body(
					Map.of("valid", false, "isDuplicate", false, "message", "아이디는 영문, 숫자, 밑줄을 사용해 4~30자로 입력해주세요."));
		}

		boolean duplicate = service.existsById(id);

		return ResponseEntity.ok(Map.of("valid", true, "isDuplicate", duplicate, "message",
				duplicate ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다."));
	}
}
