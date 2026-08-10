package com.example.demo.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.demo.domain.MemberDto;
import com.example.demo.mapper.MemberMapper;
import com.example.demo.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	
    private final MemberService memberService; // 회원 관련 로직을 처리하는 서비스 (가정)
    
    private final MemberMapper memberMapper;
	
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        // 1. 로그인 폼에서 제출한 usernameParameter 값("id")을 가져옴
        String username = request.getParameter("id");
        
        MemberDto dto = memberMapper.findById(username);
        
        if(dto != null) {
        	int num = dto.getNumber();
        	memberService.increaseLoginFailCount(num);
        }

        // 3. 기존 실패 페이지 및 에러 파라미터 유지를 위해 포워드 또는 리다이렉트 처리
        setDefaultFailureUrl("/member/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }

}
