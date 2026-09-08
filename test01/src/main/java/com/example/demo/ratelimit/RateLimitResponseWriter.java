package com.example.demo.ratelimit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitResponseWriter {
    
    private static final long NANOS_PER_SECOND = 1_000_000_000L;
    private static final String UTF_8_NAME = StandardCharsets.UTF_8.name();
    private static final String CACHE_CONTROL_NO_STORE = "no-store";
    
    private static final String HTML_BODY = """
            <!doctype html>
            <html lang="ko"><head><meta charset="UTF-8"><title>요청 한도 초과</title></head>
            <body><h1>요청 횟수를 초과했습니다.</h1><p>잠시 후 다시 시도해주세요.</p></body></html>""";
    
    private static final String PROBLEM_JSON = """
            {"title":"요청 한도 초과","status":429,"detail":"잠시 후 다시 시도해주세요."}""";

    public void writeTooManyRequests(HttpServletRequest request, HttpServletResponse response,
            ConsumptionProbe probe, RateLimitPolicy.ResponseFormat responseFormat)
            throws IOException {

        long retryAfterSeconds = toRetryAfterSeconds(probe.getNanosToWaitForRefill());

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setCharacterEncoding(UTF_8_NAME);
        
        // Spring의 HttpHeaders 대신 안전하게 문자열을 사용하거나 Spring 상수를 활용할 수 있습니다.
        response.setHeader(HttpHeaders.RETRY_AFTER, Long.toString(retryAfterSeconds));
        response.setHeader(HttpHeaders.CACHE_CONTROL, CACHE_CONTROL_NO_STORE);
        
        if ("HEAD".equals(request.getMethod())) {
            return;
        }

        switch (responseFormat) {
            case HTML -> {
                response.setContentType(MediaType.TEXT_HTML_VALUE);
                response.getWriter().write(HTML_BODY);
            }
            case JSON -> {
                response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                response.getWriter().write(PROBLEM_JSON);
            }
        }
    }
    
    private static long toRetryAfterSeconds(long waitNanos) {
        long seconds = waitNanos / NANOS_PER_SECOND;
        if (waitNanos % NANOS_PER_SECOND != 0) {
            seconds++;
        }
        return Math.max(1, seconds);
    }
}
