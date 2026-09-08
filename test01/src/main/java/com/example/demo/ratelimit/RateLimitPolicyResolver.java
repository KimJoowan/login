package com.example.demo.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RateLimitPolicyResolver {

	private static final List<RateLimitPolicy> NO_POLICIES = List.of();

	private final List<RateLimitPolicy> loginPolicies;
	private final List<RateLimitPolicy> signupPolicies;
	private final List<RateLimitPolicy> checkIdPolicies;

	public RateLimitPolicyResolver(RateLimitProperties properties) {
		this.loginPolicies = List.of(
				toPolicy("login", RateLimitPolicy.Scope.IP, properties.login().ip(),
						RateLimitPolicy.ResponseFormat.HTML),
				toPolicy("login", RateLimitPolicy.Scope.ACCOUNT, properties.login().account(),
						RateLimitPolicy.ResponseFormat.HTML));

		this.signupPolicies = List.of(toPolicy("signup", RateLimitPolicy.Scope.IP, properties.signup().ip(),
				RateLimitPolicy.ResponseFormat.HTML));

		this.checkIdPolicies = List.of(toPolicy("check-id", RateLimitPolicy.Scope.IP, properties.checkId().ip(),
				RateLimitPolicy.ResponseFormat.JSON));
	}

	public List<RateLimitPolicy> resolve(HttpServletRequest request) {
		String path = request.getServletPath();

		return switch (request.getMethod()) {
		case "POST" -> resolvePost(path);

		case "GET", "HEAD" -> "/member/check-id".equals(path) ? checkIdPolicies : NO_POLICIES;

		default -> NO_POLICIES;
		};
	}

	private List<RateLimitPolicy> resolvePost(String path) {
		return switch (path) {
		case "/member/login" -> loginPolicies;
		case "/member/signup" -> signupPolicies;
		default -> NO_POLICIES;
		};
	}

	private static RateLimitPolicy toPolicy(String name, RateLimitPolicy.Scope scope, RateLimitProperties.Limit limit,
			RateLimitPolicy.ResponseFormat responseFormat) {

		return new RateLimitPolicy(name, scope, limit.capacity(), limit.refillTokens(), limit.refillPeriod(),
				responseFormat);
	}
}
