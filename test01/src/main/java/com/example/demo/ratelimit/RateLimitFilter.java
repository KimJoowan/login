package com.example.demo.ratelimit;

import java.io.IOException;

import org.springframework.http.HttpMethod;
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
	private final ClientIdentityResolver clientIdentityResolver;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getServletPath();

		if (!HttpMethod.POST.matches(request.getMethod()) || !isRateLimitedPath(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		String endpoint = resolveEndpoint(path);

		// 모든 제한 대상 요청에 IP 버킷을 적용한다.
		String clientIp = clientIdentityResolver.getClientIp(request);
		ConsumptionProbe ipProbe = rateLimiter.tryConsumeIp(clientIp, endpoint);

		if (!ipProbe.isConsumed()) {
			writeTooManyRequests(response, ipProbe);
			return;
		}

		// 인증 전 민감 엔드포인트에는 세션 버킷을 추가로 적용한다.
		if (requiresSessionLimit(endpoint)) {
			String clientKey = clientIdentityResolver.createClientKey(request, endpoint);
			ConsumptionProbe sessionProbe = rateLimiter.tryConsumeSession(clientKey, endpoint);

			if (!sessionProbe.isConsumed()) {
				writeTooManyRequests(response, sessionProbe);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	private boolean isRateLimitedPath(String path) {
		return path.equals("/member") || path.startsWith("/member/") || path.equals("/api") || path.startsWith("/api/");
	}

	private String resolveEndpoint(String path) {
		if ("/member/check-id".equals(path)) {
			return "check-id";
		}

		if ("/member/login".equals(path)) {
			return "login";
		}

		if ("/member/signup".equals(path)) {
			return "signup";
		}

		return "general";
	}

	private boolean requiresSessionLimit(String endpoint) {
		return "check-id".equals(endpoint) || "login".equals(endpoint) || "signup".equals(endpoint);
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
