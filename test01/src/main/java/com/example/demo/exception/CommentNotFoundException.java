package com.example.demo.exception;

public class CommentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CommentNotFoundException() {
        super("댓글이 없거나 수정·삭제 권한이 없습니다.");
    }
}
