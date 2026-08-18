package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.example.demo.ratelimit.ApiRateLimiter;
import com.example.demo.ratelimit.ClientIdentityResolver;
import com.example.demo.ratelimit.RateLimitFilter;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CustomAuthenticationFailureHandler failureHandler;
	private final CustomLoginSuccessHandler successHandler;
	private final ApiRateLimiter apiRateLimiter;
	private final ClientIdentityResolver clientIdentityResolver;
	
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        	// 최전방(UsernamePasswordAuthenticationFilter 직전)에 속도 제한 필터 배치
        	.addFilterBefore(new RateLimitFilter(apiRateLimiter,clientIdentityResolver), 
        			UsernamePasswordAuthenticationFilter.class)
        	
        	.headers(headers -> headers
        		    .contentSecurityPolicy(csp -> csp
        		        .policyDirectives(
        		            "default-src 'self'; " +
        		            "script-src 'self'; " +
        		            "style-src 'self'; " +
        		            "img-src 'self' data:; " +
        		            "font-src 'self'; " +
        		            "object-src 'none'; " +
        		            "base-uri 'self'; " +
        		            "frame-ancestors 'none'"
        		        )
        		    )
        		)
        	  
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
                        "/fonts/**",
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
                .successHandler(successHandler)
                .failureHandler(failureHandler)               
                .permitAll()
            )
                      
            // 인증 성공 시 세션 ID 변경
            .sessionManagement(session -> session
            		.sessionFixation(fixation -> fixation.changeSessionId())
                    .maximumSessions(1) // 최대 허용 세션 수
                    .maxSessionsPreventsLogin(false)
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
	 HttpSessionEventPublisher httpSessionEventPublisher() {
		 return new HttpSessionEventPublisher();
	}
}
