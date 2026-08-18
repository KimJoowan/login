package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PasswordConfig { 

	@Value("${myapp.security.pepper}")
	private String pepper;

	@Bean
	PasswordEncoder passwordEncoder() {
		String idForEncode = "argon2-pepper";
		Map<String, PasswordEncoder> encoders = new HashMap<>();

		// 신형 Argon2 + 페퍼 결합
		encoders.put("argon2-pepper", createPepperEncoder(new Argon2PasswordEncoder(16, 32, 2, 65536, 3)));

		return new DelegatingPasswordEncoder(idForEncode, encoders);
	}

	private PasswordEncoder createPepperEncoder(PasswordEncoder delegate) {
		return new PasswordEncoder() {
			@Override
			public String encode(CharSequence rawPassword) {
				return delegate.encode(rawPassword + pepper);
			}

			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				return delegate.matches(applyPepper(rawPassword), encodedPassword);
			}
			
			private CharSequence applyPepper(CharSequence rawPassword) {
	            StringBuilder sb = new StringBuilder(rawPassword.length() + pepper.length());
	            sb.append(rawPassword).append(pepper);
	            return sb;
	        }
		};
	}
}
