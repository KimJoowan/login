package com.example.demo.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

	private final MemberService memberService;
	
	private final SavedRequestAwareAuthenticationSuccessHandler redirectHandler =
            new SavedRequestAwareAuthenticationSuccessHandler();



	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		// 로그인에 사용된 아이디
		String id = authentication.getName();
		memberService.recordSuccess(id);


		redirectHandler.setDefaultTargetUrl("/");
        redirectHandler.onAuthenticationSuccess(
                request,
                response,
                authentication
        );
	}

}
