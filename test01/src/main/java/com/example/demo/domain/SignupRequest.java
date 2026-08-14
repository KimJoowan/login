package com.example.demo.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(

    @NotBlank(message = "아이디를 입력해 주세요.")
    @Size(
        min = 4,
        max = 30,
        message = "아이디는 4~30자로 입력해 주세요."
    )
    @Pattern(
        regexp = "^[a-zA-Z0-9_]+$",
        message = "아이디는 영문, 숫자, 밑줄만 사용할 수 있습니다."
    )
    String id,

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Size(
        min = 10,
        max = 100,
        message = "비밀번호는 10~100자로 입력해 주세요."
    )
    String password,

    @NotBlank(message = "닉네임을 입력해 주세요.")
    @Size(
        max = 30,
        message = "닉네임은 30자 이하로 입력해 주세요."
    )
    String userName,

    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(
        message = "올바른 이메일 형식으로 입력해 주세요."
    )
    @Size(
        max = 254,
        message = "이메일은 254자 이하로 입력해 주세요."
    )
    String email
) {}