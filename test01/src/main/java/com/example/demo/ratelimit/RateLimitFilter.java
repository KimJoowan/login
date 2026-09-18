package com.example.demo.ratelimit;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.ConsumptionProbe;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 요청에 해당하는 정책을 순서대로 검사하고, 모두 통과한 요청만 다음 필터로 전달합니다.
 */
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

	private final RateLimiter rateLimiter;
	private final IdentityResolver identityResolver;
	private final PolicyResolver policyResolver;
	private final MeterRegistry meterRegistry;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		for (RateLimitPolicy policy : policyResolver.resolve(request)) {
			String identity = resolveIdentity(request, policy.scope());
			ConsumptionProbe result;

			try {
				result = rateLimiter.tryConsume(policy, identity);
			} catch (RateLimiter.CapacityExceededException e) {
				RateLimitResponseWriter.writeUnavailable(request, response, policy.responseFormat());
				return;
			}

			if (!result.isConsumed()) {
				recordBlockedRequest(policy);
				RateLimitResponseWriter.writeTooManyRequests(
						request, response, policy.responseFormat(), result.getNanosToWaitForRefill());
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	private String resolveIdentity(HttpServletRequest request, RateLimitPolicy.Scope scope) {
		return switch (scope) {
			case IP -> identityResolver.clientIp(request);
			case ACCOUNT -> identityResolver.accountHash(request);
		};
	}

	private void recordBlockedRequest(RateLimitPolicy policy) {
		meterRegistry.counter(
				"ratelimit.blocked",
				"policy", policy.name(),
				"scope", policy.scope().name())
				.increment();
	}
}