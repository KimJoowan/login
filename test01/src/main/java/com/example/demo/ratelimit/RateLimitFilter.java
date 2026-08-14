package com.example.demo.ratelimit;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

	private final ApiRateLimiter rateLimiter;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getServletPath();

		if (!isRateLimitedPath(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		String endpoint = resolveEndpoint(path);
		String clientIp = rateLimiter.getClientIp(request);

		// 1. IP 전용 버킷
		ConsumptionProbe ipProbe = rateLimiter.tryConsumeIp(clientIp, endpoint);

		if (!ipProbe.isConsumed()) {
			writeTooManyRequests(response, ipProbe);
			return;
		}

		// 2. IP + 세션 + 엔드포인트 버킷
		String clientKey = rateLimiter.createClientKey(request, endpoint);

		ConsumptionProbe sessionProbe = rateLimiter.tryConsumeSession(clientKey, endpoint);

		if (!sessionProbe.isConsumed()) {
			writeTooManyRequests(response, sessionProbe);
			return;
		}

		// 3. 서버 전체 엔드포인트 버킷
		ConsumptionProbe globalProbe = rateLimiter.tryConsumeGlobal(endpoint);

		if (!globalProbe.isConsumed()) {
			writeTooManyRequests(response, globalProbe);
			return;
		}

		filterChain.doFilter(request, response);
	}

	private boolean isRateLimitedPath(String path) {
		return path.startsWith("/member") || path.startsWith("/api");
	}

	private String resolveEndpoint(String path) {
		return "/member/check-id".equals(path) ? "check-id" : "general";
	}

	private void writeTooManyRequests(HttpServletResponse response, ConsumptionProbe probe) throws IOException {

		long waitNanos = probe.getNanosToWaitForRefill();

		long retryAfterSeconds = Math.max(1, (waitNanos + 999_999_999L) / 1_000_000_000L);

		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));

		response.getWriter().write("{\"error\":\"요청 횟수를 초과했습니다. " + "잠시 후 다시 시도해주세요.\"}");
	}
}