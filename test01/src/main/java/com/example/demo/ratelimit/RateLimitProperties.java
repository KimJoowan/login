package com.example.demo.ratelimit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

/**
 * {@code app.rate-limit} 설정을 바인딩합니다.
 */
@Validated
@ConfigurationProperties(prefix = "app.rate-limit")
public record RateLimitProperties(
		@NotEmpty @Valid Map<String, Rule> policies,
		@NotNull @Valid Cache cache) {

	/** 경로와 HTTP 메서드에 적용할 정책 그룹입니다. */
	public record Rule(
			@NotBlank @Pattern(regexp = "/.*") String path,
			@NotEmpty Set<@NotBlank String> methods,
			@NotNull RateLimitPolicy.ResponseFormat responseFormat,
			@NotEmpty @Valid Map<RateLimitPolicy.Scope, Limit> limits) {
	}

	/** 버킷 용량과 토큰 충전 규칙입니다. */
	public record Limit(
			@Positive long capacity,
			@Positive long refillTokens,
			@NotNull @DurationMin(seconds = 1) Duration refillPeriod) {
	}

	/** 버킷 캐시의 크기와 만료 설정입니다. */
	public record Cache(
			@Positive long maximumSize,
			@NotNull @DurationMin(seconds = 1) Duration expireAfterAccess) {
	}
}
