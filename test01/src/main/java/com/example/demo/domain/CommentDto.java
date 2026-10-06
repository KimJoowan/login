package com.example.demo.domain;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class CommentDto {
    private long ccode;
    private long bcode;
    private long idNumber;
    private String authorName;
    private String content;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
