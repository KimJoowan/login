package com.example.demo.exception;

public class DuplicateEmailException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DuplicateEmailException() {
        super("이미 사용 중인 이메일입니다.");
    }
}
