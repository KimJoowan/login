package com.example.demo.ratelimit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

class RateLimitPropertiesTest {

	@Test
	void bindsPoliciesByName() {
		MapConfigurationPropertySource source = new MapConfigurationPropertySource(Map.of(
				"app.rate-limit.policies.login.path", "/member/login",
				"app.rate-limit.policies.login.methods", "POST",
				"app.rate-limit.policies.login.response-format", "HTML",
				"app.rate-limit.policies.login.limits.ip.capacity", "10",
				"app.rate-limit.policies.login.limits.ip.refill-tokens", "10",
				"app.rate-limit.policies.login.limits.ip.refill-period", "PT1M",
				"app.rate-limit.cache.maximum-size", "100",
				"app.rate-limit.cache.expire-after-access", "PT10M"));

		RateLimitProperties properties = new Binder(source)
				.bind("app.rate-limit", Bindable.of(RateLimitProperties.class))
				.orElseThrow(() -> new AssertionError("Rate limit properties were not bound"));

		RateLimitProperties.Rule login = properties.policies().get("login");
		assertThat(login.path()).isEqualTo("/member/login");
		assertThat(login.methods()).containsExactly("POST");
		assertThat(login.responseFormat()).isEqualTo(RateLimitPolicy.ResponseFormat.HTML);
		assertThat(login.limits().get(RateLimitPolicy.Scope.IP).refillPeriod()).isEqualTo(Duration.ofMinutes(1));
	}
}
