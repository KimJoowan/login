package com.example.demo.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record SignupRequest(
		
		@NotBlank
	    @Size(min = 4, max = 30)
	    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
	    String id,

	    @NotBlank
	    @Size(min = 10, max = 100)
	    String password,

	    @NotBlank
	    @Size(max = 30)
	    String userName,

	    @NotBlank
	    @Email
	    @Size(max = 254)
	    String email
) {}
