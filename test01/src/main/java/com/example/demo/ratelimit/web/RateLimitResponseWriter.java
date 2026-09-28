package com.example.demo.ratelimit.web;

import com.example.demo.ratelimit.policy.RateLimitPolicy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 요청 제한 응답의 헤더와 HTML·JSON 본문을 작성합니다. */
final class RateLimitResponseWriter {

	private static final long NANOS_PER_SECOND = TimeUnit.SECONDS.toNanos(1);
	private static final String JSON_ERROR_TEMPLATE = """
			{"title":"%s","status":%d,"detail":"잠시 후 다시 시도해주세요."}
			""";
	private static final String HTML_ERROR_TEMPLATE = """
			<!doctype html>
			<html lang="ko">
			<head><meta charset="UTF-8"><title>%s</title></head>
			<body><h1>%s</h1>
			<p>잠시 후 다시 시도해주세요.</p></body>
			</html>
			""";

	private RateLimitResponseWriter() {
	}

	static void writeUnavailable(HttpServletRequest request, HttpServletResponse response,
			RateLimitPolicy.ResponseFormat format) throws IOException {
		writeError(request, response, format, Rejection.STORAGE_FULL);
	}

	static void writeTooManyRequests(HttpServletRequest request, HttpServletResponse response,
			RateLimitPolicy.ResponseFormat format, long nanosUntilRefill) throws IOException {
		// 충전 전에 재시도하지 않도록 소수 초는 올림하고 최소 1초를 기다리게 합니다.
		long retryAfterSeconds = Math.max(1L, Math.ceilDiv(nanosUntilRefill, NANOS_PER_SECOND));
		response.setHeader(HttpHeaders.RETRY_AFTER, Long.toString(retryAfterSeconds));
		writeError(request, response, format, Rejection.RATE_EXCEEDED);
	}

	private static void writeError(HttpServletRequest request, HttpServletResponse response,
			RateLimitPolicy.ResponseFormat format, Rejection rejection) throws IOException {
		boolean jsonResponse = format == RateLimitPolicy.ResponseFormat.JSON;
		String contentType = jsonResponse ? MediaType.APPLICATION_PROBLEM_JSON_VALUE : MediaType.TEXT_HTML_VALUE;

		response.setStatus(rejection.status.value());
		response.setContentType(contentType);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");

		// HEAD 요청에도 상태와 헤더는 동일하게 보내지만 본문은 작성하지 않습니다.
		if ("HEAD".equalsIgnoreCase(request.getMethod())) {
			return;
		}

		String body = jsonResponse
				? JSON_ERROR_TEMPLATE.formatted(rejection.title, rejection.status.value())
				: HTML_ERROR_TEMPLATE.formatted(rejection.title, rejection.message);
		response.getWriter().write(body);
	}

	// 템플릿에는 요청값을 넣지 않고 아래의 고정 문구만 사용합니다.
	private enum Rejection {
		RATE_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "요청 한도 초과", "요청이 너무 많습니다."),
		STORAGE_FULL(HttpStatus.SERVICE_UNAVAILABLE, "서비스 일시 사용 불가", "현재 요청을 처리할 수 없습니다.");

		private final HttpStatus status;
		private final String title;
		private final String message;

		Rejection(HttpStatus status, String title, String message) {
			this.status = status;
			this.title = title;
			this.message = message;
		}
	}
}