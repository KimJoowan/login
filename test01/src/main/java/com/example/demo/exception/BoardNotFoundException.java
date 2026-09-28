package com.example.demo.exception;

public class BoardNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BoardNotFoundException() {
        super("게시글이 없거나 수정·삭제 권한이 없습니다.");
    }
}
