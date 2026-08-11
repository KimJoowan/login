package com.example.demo.ratelimit; // 인터셉터와 동일하거나 상위 패키지로 지정

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // 전역 컨트롤러 예외 가로채기 활성화
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        
        // 인터셉터에서 던진 특정 메시지인지 확인
        if ("Too Many Requests".equals(ex.getMessage())) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
            responseBody.put("message", "요청 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.");
            
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS) // HTTP 429 응답
                    .body(responseBody);
        }
        
        // 속도 제한 외의 다른 일반 런타임 에러는 500 에러 처리
        Map<String, Object> defaultBody = new HashMap<>();
        defaultBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        defaultBody.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(defaultBody);
    }
}
