package com.example.demo.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final ApiRateLimiter rateLimiter;

    

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 감시할 주소 패턴 필터링 (/member/** 및 /api/**)
        if (path.startsWith("/member") || path.startsWith("/api")) {
            String ip = request.getRemoteAddr();

            if (!rateLimiter.tryConsume(ip)) {
                // 필터 단계에서는 예외를 던지는 것보다 직접 응답을 작성하는 것이 안전합니다.
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\": \"요청 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.\"}");
                return; // 다음 필터로 진행하지 않고 즉시 리턴(차단)
            }
        }

        filterChain.doFilter(request, response); // 통과 시 다음 필터 진행
    }
}

