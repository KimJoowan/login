package com.example.demo.ratelimit;

import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitPolicyResolver {

	private final RateLimitProperties properties;

	public List<RateLimitPolicy> resolve(HttpServletRequest request) {
		String method = request.getMethod();
		String path = request.getServletPath();

		if ("POST".equals(method) && "/member/login".equals(path)) {
			return List.of(toPolicy("login", RateLimitPolicy.Scope.IP, properties.login().ip()),
					toPolicy("login", RateLimitPolicy.Scope.ACCOUNT, properties.login().account()));
		}

		if ("POST".equals(method) && "/member/signup".equals(path)) {
			return List.of(toPolicy("signup", RateLimitPolicy.Scope.IP, properties.signup().ip()));
		}

		if ("GET".equals(method) && "/member/check-id".equals(path)) {
			return List.of(toPolicy("check-id", RateLimitPolicy.Scope.IP, properties.checkId().ip()));
		}

		return List.of();
	}

	private RateLimitPolicy toPolicy(String name, RateLimitPolicy.Scope scope, RateLimitProperties.Limit limit) {
		return new RateLimitPolicy(name, scope, limit.capacity(), limit.refillTokens(), limit.refillPeriod());
	}
}
