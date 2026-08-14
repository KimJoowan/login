package com.example.demo.domain;

import lombok.Data;
import lombok.ToString;

@Data
public class MemberDto {
	private int number;
    private String id;
    private String userName;
    private String email;
    private String role;
    
    @ToString.Exclude
    private String password;   
}
