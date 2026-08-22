package com.example.demo.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.controller.api.MemberApiController;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice(assignableTypes = MemberApiController.class)
@Slf4j
public class ApiExceptionHandler {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException exception) {

		log.warn("API 데이터 충돌", exception);

		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "이미 존재하거나 처리할 수 없는 데이터입니다.");
		problem.setTitle("데이터 충돌");

		return problem;
	}
}