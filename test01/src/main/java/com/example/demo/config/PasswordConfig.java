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

        // Argon2 권장 파라미터 (saltLength, hashLength, parallelism, memory, iterations)
        Argon2PasswordEncoder argon2Delegate = new Argon2PasswordEncoder(16, 32, 2, 65536, 3);
        
        // 페퍼가 적용된 Argon2 인코더 등록
        encoders.put(idForEncode, createPepperEncoder(argon2Delegate));

        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }

    private PasswordEncoder createPepperEncoder(PasswordEncoder delegate) {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return delegate.encode(concatPepper(rawPassword));
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return delegate.matches(concatPepper(rawPassword), encodedPassword);
            }

            private String concatPepper(CharSequence rawPassword) {
                return rawPassword.toString() + pepper;
            }
        };
    }
}
