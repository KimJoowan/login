package com.example.demo.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

	private final ApiRateLimiter rateLimiter;
	private final ClientIdentityResolver clientIdentityResolver;
	private final RateLimitPolicyResolver policyResolver;
	private final io.micrometer.core.instrument.MeterRegistry meterRegistry;
	private final RateLimitResponseWriter rateLimitResponseWriter;

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
			var probe = rateLimiter.tryConsume(policy, identity);
			
			if (!probe.isConsumed()) {
				meterRegistry.counter("ratelimit.blocked", "policy", policy.name(), "scope", policy.scope().name()).increment();
				rateLimitResponseWriter.writeTooManyRequests(request, response, probe, policy.responseFormat());
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

}
