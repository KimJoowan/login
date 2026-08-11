package com.example.demo.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final ApiRateLimiter rateLimiter;

   

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteAddr(); 
        
        if (!rateLimiter.tryConsume(ip)) {
            // 예외를 던지지 않고, 즉시 429 응답 규격을 작성합니다.
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429
            response.setContentType(MediaType.APPLICATION_JSON_VALUE); // application/json
            response.setCharacterEncoding("UTF-8");
            
            // 클라이언트에게 내려줄 JSON 메시지
            String jsonResponse = "{\"status\": 429, \"error\": \"Too Many Requests\", \"message\": \"요청 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.\"}";
            response.getWriter().write(jsonResponse);
            
            return false; // 더 이상 컨트롤러로 진행하지 않고 여기서 요청 종료
        }
        
        return true; 
    }
}
