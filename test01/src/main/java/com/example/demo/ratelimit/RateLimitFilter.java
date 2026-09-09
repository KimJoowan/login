package com.example.demo.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.ConsumptionProbe;

import java.io.IOException;
import java.util.List;

/**
 * 요청별 Rate Limit 정책을 실행합니다.
 */
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

	private final RateLimiter rateLimiter;
	private final IdentityResolver identityResolver;
	private final PolicyResolver policyResolver;
	private final MeterRegistry meterRegistry;

	/**
	 * 등록된 정책을 검사하고 통과한 요청만 다음 필터로 전달합니다.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		List<RateLimitPolicy> policies = policyResolver.resolve(request);
		for (RateLimitPolicy policy : policies) {
			String identity = identity(request, policy.scope());
			ConsumptionProbe probe;

			try {
				probe = rateLimiter.tryConsume(policy, identity);
			} catch (RateLimiter.CapacityExceededException e) {
				writeUnavailable(request, response, policy.responseFormat());
				return;
			}

			if (!probe.isConsumed()) {
				meterRegistry.counter("ratelimit.blocked", "policy", policy.name(), "scope", policy.scope().name())
						.increment();

				long retryAfterSeconds = Math.max(1L, Math.ceilDiv(probe.getNanosToWaitForRefill(), 1_000_000_000L));

				writeTooManyRequests(request, response, policy.responseFormat(), retryAfterSeconds);

				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	// 정책 범위에 맞는 클라이언트 식별자를 생성합니다.
	private String identity(HttpServletRequest request, RateLimitPolicy.Scope scope) {
		return switch (scope) {
		case IP -> identityResolver.clientIp(request);
		case ACCOUNT -> identityResolver.accountHash(request);
		};
	}

	// 정책에 지정된 형식으로 503 응답을 작성합니다.
	private static void writeUnavailable(HttpServletRequest request, HttpServletResponse response,
			RateLimitPolicy.ResponseFormat format) throws IOException {

		boolean json = format == RateLimitPolicy.ResponseFormat.JSON;
		write(request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, // ← 추가
				json ? MediaType.APPLICATION_PROBLEM_JSON_VALUE : MediaType.TEXT_HTML_VALUE, json ? """
						{"title":"서비스 일시 사용 불가","status":503,"detail":"잠시 후 다시 시도해주세요."}
						""" : """
						<!doctype html>
						<html lang="ko">
						<head><meta charset="UTF-8"><title>서비스 일시 사용 불가</title></head>
						<body><h1>현재 요청을 처리할 수 없습니다.</h1>
						<p>잠시 후 다시 시도해주세요.</p></body>
						</html>
						""");
	}

	// 공통 헤더를 설정하고 HEAD 요청이 아닐 때만 본문을 작성합니다.
	private static void write(HttpServletRequest request, HttpServletResponse response, int status, String contentType,
			String body) throws IOException {

		response.setStatus(status);
		response.setContentType(contentType);
		response.setCharacterEncoding("UTF-8");
		response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");

		if (!"HEAD".equals(request.getMethod()))
			response.getWriter().write(body);
	}

	public void writeTooManyRequests(HttpServletRequest request, HttpServletResponse response,
			RateLimitPolicy.ResponseFormat format, long retryAfterSeconds) throws IOException {

		response.setHeader(HttpHeaders.RETRY_AFTER, Long.toString(retryAfterSeconds));

		boolean json = format == RateLimitPolicy.ResponseFormat.JSON;

		write(request, response, 429, json ? MediaType.APPLICATION_PROBLEM_JSON_VALUE : MediaType.TEXT_HTML_VALUE,
				json ? """
						{"title":"요청 한도 초과","status":429,"detail":"잠시 후 다시 시도해주세요."}
						""" : """
						<!doctype html>
						<html lang="ko">
						<head><meta charset="UTF-8"><title>요청 한도 초과</title></head>
						<body><h1>요청이 너무 많습니다.</h1>
						<p>잠시 후 다시 시도해주세요.</p></body>
						</html>
						""");
	}

}
