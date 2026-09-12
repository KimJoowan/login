package com.example.demo.ratelimit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class PolicyResolverTest {

	private final PolicyResolver resolver = new PolicyResolver(properties());

	@Test
	void resolvesAllPoliciesForRoute() {
		MockHttpServletRequest request = request("POST", "/member/login");

		assertThat(resolver.resolve(request))
				.extracting(RateLimitPolicy::scope)
				.containsExactly(RateLimitPolicy.Scope.IP, RateLimitPolicy.Scope.ACCOUNT);
	}

	@Test
	void supportsGetAndHeadForSameRoute() {
		assertThat(resolver.resolve(request("GET", "/member/check-id"))).hasSize(1);
		assertThat(resolver.resolve(request("HEAD", "/member/check-id"))).hasSize(1);
	}

	@Test
	void returnsNoPoliciesForUnregisteredRoute() {
		assertThat(resolver.resolve(request("DELETE", "/member/login"))).isEmpty();
		assertThat(resolver.resolve(request("GET", "/member/unknown"))).isEmpty();
	}

	private static MockHttpServletRequest request(String method, String path) {
		MockHttpServletRequest request = new MockHttpServletRequest(method, path);
		request.setServletPath(path);
		return request;
	}

	private static RateLimitProperties properties() {
		RateLimitProperties.Limit limit = new RateLimitProperties.Limit(10, 10, Duration.ofMinutes(1));

		return new RateLimitProperties(
				Map.of(
						"login", new RateLimitProperties.Rule(
								"/member/login", Set.of("POST"), RateLimitPolicy.ResponseFormat.HTML,
								Map.of(RateLimitPolicy.Scope.IP, limit, RateLimitPolicy.Scope.ACCOUNT, limit)),
						"signup", new RateLimitProperties.Rule(
								"/member/signup", Set.of("POST"), RateLimitPolicy.ResponseFormat.HTML,
								Map.of(RateLimitPolicy.Scope.IP, limit)),
						"check-id", new RateLimitProperties.Rule(
								"/member/check-id", Set.of("GET", "HEAD"), RateLimitPolicy.ResponseFormat.JSON,
								Map.of(RateLimitPolicy.Scope.IP, limit))),
				new RateLimitProperties.Cache(100, Duration.ofMinutes(10)));
	}
}
