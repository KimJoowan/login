package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Spring Security 6.x 기준 정적 자원 및 URL 권한 설정
            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(
                    DispatcherType.FORWARD, 
                    DispatcherType.ERROR
                ).permitAll()
                .requestMatchers(
                    "/", 
                    "/member/login", 
                    "/member/signup", 
                    "/member/check-id", 
                    "/css/**", 
                    "/js/**", 
                    "/images/**", 
                    "/error"
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // 폼 로그인 설정
            .formLogin(form -> form
                .loginPage("/member/login")
                .loginProcessingUrl("/member/login")
                .usernameParameter("id")
                .passwordParameter("password")
                // ⭐️ defaultSuccessUrl(..., true)을 지우고 핸들러만 남겨야 정상 작동합니다!
                .successHandler(loginSuccessHandler) 
                .failureUrl("/member/login?error")
                .permitAll()
            )
            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/member/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}
