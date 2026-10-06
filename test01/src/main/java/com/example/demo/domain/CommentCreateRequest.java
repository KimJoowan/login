package com.example.demo.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
        @Positive(message = "게시글 번호는 양수여야 합니다.") long bcode,
        @NotBlank(message = "댓글 내용을 입력해 주세요.")
        @Size(max = 2000, message = "댓글은 2,000자 이하로 입력해 주세요.") String content) {
}
