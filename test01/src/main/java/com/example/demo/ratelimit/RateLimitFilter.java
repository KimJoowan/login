package com.example.demo.ratelimit;

import java.io.IOException;
import java.util.List;

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
	private final RateLimitPolicyResolver policyResolver;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		List<RateLimitPolicy> policies = policyResolver.resolve(request);

		if (policies.isEmpty()) {
			filterChain.doFilter(request, response);
			return;
		}

		for (RateLimitPolicy policy : policies) {
			String identity = resolveIdentity(request, policy.scope());
			ConsumptionProbe probe = rateLimiter.tryConsume(policy, identity);

			if (!probe.isConsumed()) {
				writeTooManyRequests(request, response, probe);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	private String resolveIdentity(HttpServletRequest request, RateLimitPolicy.Scope scope) {
		return switch (scope) {
			case IP -> clientIdentityResolver.getClientIp(request);
			case ACCOUNT -> clientIdentityResolver.getLoginAccountHash(request);
		};
	}

	private void writeTooManyRequests(
			HttpServletRequest request,
			HttpServletResponse response,
			ConsumptionProbe probe) throws IOException {

		long waitNanos = probe.getNanosToWaitForRefill();

		long retryAfterSeconds = Math.max(1, (waitNanos + 999_999_999L) / 1_000_000_000L);

		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));

		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains(MediaType.TEXT_HTML_VALUE)) {
			response.setContentType(MediaType.TEXT_HTML_VALUE);
			response.getWriter().write("""
					<!doctype html>
					<html lang="ko"><head><meta charset="UTF-8"><title>요청 한도 초과</title></head>
					<body><h1>요청 횟수를 초과했습니다.</h1><p>잠시 후 다시 시도해주세요.</p></body></html>
					""");
			return;
		}

		response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
		response.getWriter().write("""
				{"title":"요청 한도 초과","status":429,"detail":"잠시 후 다시 시도해주세요."}
				""");
	}
}

