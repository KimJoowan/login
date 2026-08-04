package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.DispatcherType;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    	http
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
            
        .formLogin(form -> form
                .loginPage("/member/login")
                .loginProcessingUrl("/member/login")
                .usernameParameter("id")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .failureUrl("/member/login?error")
                .permitAll()
            )
        
        .logout(logout -> logout
                .logoutUrl("/member/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );
        
        return http.build();
    }
}