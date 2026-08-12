package com.example.demo.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AccountLockDto {
	private int number;
    private int failCount;
    private LocalDateTime lockedUntil;
    private boolean active = true;
}
