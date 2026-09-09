package com.example.demo.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 설정된 정책을 HTTP 메서드와 경로별로 관리합니다.
 */
@Component
public class PolicyResolver {

	private static final List<RateLimitPolicy> NO_POLICIES = List.of();

	private final Map<Route, List<RateLimitPolicy>> policiesByRoute;

	/**
	 * 설정된 정책으로 라우트 맵을 생성합니다.
	 *
	 * @param properties Rate Limit 설정
	 */
	public PolicyResolver(RateLimitProperties properties) {
		this.policiesByRoute = routes(properties.policies());
	}

	// 정책 설정을 (HTTP 메서드, 경로) 기준으로 변환합니다.
	private static Map<Route, List<RateLimitPolicy>> routes(Map<String, RateLimitProperties.Rule> rules) {
		Map<Route, List<RateLimitPolicy>> routes = new HashMap<>();

		rules.forEach((name, rule) -> {
			List<RateLimitPolicy> policies = rule.limits().entrySet().stream().sorted(Map.Entry.comparingByKey())
					.map(entry -> policy(name, entry.getKey(), entry.getValue(), rule.responseFormat())).toList();

			rule.methods().stream().map(String::strip).map(method -> method.toUpperCase(Locale.ROOT)).distinct()
					.forEach(method -> routes.merge(new Route(method, rule.path()), policies, PolicyResolver::combine));
		});

		return Map.copyOf(routes);
	}

	// 같은 라우트에 등록된 정책을 하나의 목록으로 합칩니다.
	private static List<RateLimitPolicy> combine(List<RateLimitPolicy> first, List<RateLimitPolicy> second) {
		return Stream.concat(first.stream(), second.stream()).toList();
	}

	// 설정값을 실행 가능한 정책으로 변환합니다.
	private static RateLimitPolicy policy(String name, RateLimitPolicy.Scope scope, RateLimitProperties.Limit limit,
			RateLimitPolicy.ResponseFormat responseFormat) {

		return new RateLimitPolicy(name, scope, limit.capacity(), limit.refillTokens(), limit.refillPeriod(),
				responseFormat);
	}

	/**
	 * 요청에 등록된 정책을 반환합니다.
	 *
	 * @param request HTTP 요청
	 * @return 적용할 정책 목록
	 */
	public List<RateLimitPolicy> resolve(HttpServletRequest request) {
		Route route = new Route(request.getMethod(), request.getServletPath());
		return policiesByRoute.getOrDefault(route, NO_POLICIES);
	}

	private record Route(String method, String path) {

		// HTTP 메서드 표기를 대문자로 통일합니다.
		private Route {
			method = method.toUpperCase(Locale.ROOT);
		}
	}
}
