package com.example.demo.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.domain.MemberDto;
import com.example.demo.mapper.MemberMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
	
	private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // 1. 로그인에 성공한 사용자 ID(username) 가져오기
        String userId = authentication.getName();
        
        // 1. 성공 로그 찍어보기
        System.out.println("====== [로그인 성공] 사용자 ID: " + userId + " ======");
        
        // 2. 사용자가 입력한 날것의 비밀번호(평문) 가져오기 (시큐리티 기본 폼 로그인 기준 파라미터명: password)
        String rawPassword = request.getParameter("password");
        
        // 3. DB에서 현재 저장된 암호화 상태 조회
        MemberDto member = memberMapper.findById(userId);

        if (member != null && rawPassword != null) {
            // 4. ⭐️ 핵심: 현재 DB 암호 포맷이 구형설정({bcrypt})이면 true 반환
            if (passwordEncoder.upgradeEncoding(member.getPassword())) {
            	// 2. 마이그레이션 대상인지 로그로 확인
                System.out.println("-> 구형 암호 포맷 발견! 신형 알고리즘으로 업그레이드를 진행합니다.");
            	
                // 새로운 포맷({argon2-pepper} + 평문비밀번호 + 페퍼 조합)으로 재인코딩
                String newEncodedPassword = passwordEncoder.encode(rawPassword);
                System.out.println("-> DB 업데이트 완료: " + newEncodedPassword);
                // 마이바티스 Mapper를 통해 DB 비밀번호 컬럼 업데이트
                // (멤버 DTO나 맵퍼에 비밀번호 업데이트 메서드가 구현되어 있어야 합니다)
                memberMapper.updatePassword(userId, newEncodedPassword); 
            }else {
            	System.out.println("-> 이미 최신 알고리즘({argon2-pepper})으로 암호화된 안전한 상태입니다.");
            }
        }

        // 5. 로그인 성공 후 이동할 메인 페이지 설정
        response.sendRedirect("/"); 
    }

}
