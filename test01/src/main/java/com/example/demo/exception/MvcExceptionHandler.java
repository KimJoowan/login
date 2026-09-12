package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice(basePackages = "com.example.demo.controller.mvc")
public class MvcExceptionHandler {

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleException(Exception e, HttpServletRequest request) {
		log.error("서버 예외 발생 - method={}, uri={}", request.getMethod(), request.getRequestURI(), e);
		return "error/500";
	}
	
	@ExceptionHandler(MemberNotFoundException.class)
	public String handleMemberNotFound(
	        HttpServletRequest request) {

	    HttpSession session = request.getSession(false);

	    if (session != null) {
	        session.invalidate();
	    }

	    return "redirect:/member/login";
	}
}
