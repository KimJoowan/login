package com.example.demo.domain;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class BoardDto {
    private long bcode;
    private long idNumber;
    private String title;
    private String content;
    private OffsetDateTime createdAt;
}
