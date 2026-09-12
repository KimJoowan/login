package com.example.demo.exception;

public class MemberNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public MemberNotFoundException() {
        super("회원 정보를 찾을 수 없습니다.");
    }
}