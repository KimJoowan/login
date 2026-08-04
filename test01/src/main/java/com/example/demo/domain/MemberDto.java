package com.example.demo.domain;

import lombok.Data;

@Data
public class MemberDto {
    private int number;
    private String id;
    private String password;
    private String userName;
    private String email;
    private boolean isActive;
    private String role;
	
}
