package com.example.demo.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

	private static final Logger log = LogManager.getLogger(HelloController.class);

	@GetMapping("/")
	public String index(Authentication authentication) {
		return "main/index";
	}

}