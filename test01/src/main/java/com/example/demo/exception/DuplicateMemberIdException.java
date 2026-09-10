package com.example.demo.exception;

public class DuplicateMemberIdException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DuplicateMemberIdException() {
        super("이미 사용 중인 아이디입니다.");
    }
}
