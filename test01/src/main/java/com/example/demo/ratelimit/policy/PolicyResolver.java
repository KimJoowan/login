package com.example.demo.ratelimit.policy;

import com.example.demo.ratelimit.config.RateLimitProperties;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

/**
 * 설정된 정책을 HTTP 메서드와 경로별로 관리합니다.
 */
@Component
public class PolicyResolver {

	// 같은 라우트에서는 IP → ACCOUNT, 같은 범위에서는 정책 이름 순서로 실행합니다.
	private static final Comparator<RateLimitPolicy> POLICY_ORDER = Comparator
			.comparingInt(PolicyResolver::scopePriority)
			.thenComparing(RateLimitPolicy::name);

	private final Map<Route, List<RateLimitPolicy>> policiesByRoute;
	public PolicyResolver(RateLimitProperties properties) {
        this.policiesByRoute = indexPoliciesByRoute(properties.policies());
    }
	
	/** 요청의 HTTP 메서드와 서블릿 경로에 정확히 일치하는 정책을 반환합니다. */
	public List<RateLimitPolicy> resolve(HttpServletRequest request) {
		Route route = new Route(request.getMethod(), request.getServletPath());
		return policiesByRoute.getOrDefault(route, List.of());
	}

	private static Map<Route, List<RateLimitPolicy>> indexPoliciesByRoute(Map<String, RateLimitProperties.Rule> rules) {
		Map<Route, List<RateLimitPolicy>> routes = new HashMap<>();

		for (var entry : rules.entrySet()) {
			String policyName = entry.getKey();
			RateLimitProperties.Rule rule = entry.getValue();
			List<RateLimitPolicy> policies = createPolicies(policyName, rule);
			registerRoutes(routes, rule, policies);
		}

		// 모든 규칙을 합친 뒤 한 번만 정렬하고 외부 변경을 막습니다.
		for (List<RateLimitPolicy> policies : routes.values()) {
			policies.sort(POLICY_ORDER);
		}
		
		routes.replaceAll((route, policies) -> List.copyOf(policies));
		return Map.copyOf(routes);
	}

	private static List<RateLimitPolicy> createPolicies(String name, RateLimitProperties.Rule rule) {
		List<RateLimitPolicy> policies = new ArrayList<>();

		for (var entry : rule.limits().entrySet()) {
			RateLimitPolicy.Scope scope = entry.getKey();
			RateLimitProperties.Limit limit = entry.getValue();
			policies.add(new RateLimitPolicy(
					name, scope, limit.capacity(), limit.refillTokens(), limit.refillPeriod(), rule.responseFormat()));
		}

		return policies;
	}

	private static void registerRoutes(Map<Route, List<RateLimitPolicy>> routes, RateLimitProperties.Rule rule, List<RateLimitPolicy> policies) {
		Set<Route> registeredRoutes = new HashSet<>();

		for (String method : rule.methods()) {
			Route route = new Route(method, rule.path());
			
			// GET/get처럼 대소문자만 다른 메서드가 한 규칙에 있으면 한 번만 등록합니다.
			if (!registeredRoutes.add(route)) {
				continue;
			}

			List<RateLimitPolicy> routePolicies = routes.computeIfAbsent(route, ignored -> new ArrayList<>());
			routePolicies.addAll(policies);
		}
	}

	private static int scopePriority(RateLimitPolicy policy) {
		return switch (policy.scope()) {
			case IP -> 0;
			case ACCOUNT -> 1;
		};
	}

	private record Route(String method, String path) {

		// HTTP 메서드 표기를 대문자로 통일합니다.
		private Route {
			method = method.toUpperCase(Locale.ROOT);
		}
	}
}
