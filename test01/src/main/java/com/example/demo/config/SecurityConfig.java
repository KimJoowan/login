package com.example.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        	// 아래 corsConfigurationSource Bean의 정책 사용
        	.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        	
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
                    "/error",
                    "/.well-known/appspecific/com.chrome.devtools.json"
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
                .defaultSuccessUrl("/", true)
                .failureHandler(customAuthenticationFailureHandler)               
                .permitAll()
            )
                      
            // 인증 성공 시 세션 ID 변경
            .sessionManagement(session -> session
            		.sessionFixation(fixation -> fixation.changeSessionId())
                    .maximumSessions(1) // 최대 허용 세션 수
                    .maxSessionsPreventsLogin(true)
                    .expiredUrl("/") // 세션이 만료되었을 때 이동할 페이지
             )
            
            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/member/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://localhost:5173",
            "https://example.com"
        ));

        configuration.setAllowedMethods(List.of(
            "GET",
            "POST",
            "PUT",
            "PATCH",
            "DELETE",
            "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "X-XSRF-TOKEN"
        ));

        // 세션 쿠키를 다른 Origin에서 전송할 때 필요
        configuration.setAllowCredentials(true);

        // 브라우저의 preflight 결과 캐시 시간
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
    
    @Bean
	 public HttpSessionEventPublisher httpSessionEventPublisher() {
		 return new HttpSessionEventPublisher();
	}
}
