package com.example.demo.ratelimit.config;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import com.example.demo.ratelimit.policy.RateLimitPolicy.ResponseFormat;
import com.example.demo.ratelimit.policy.RateLimitPolicy.Scope;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * {@code app.rate-limit} 설정을 바인딩합니다.
 */
@Validated
@ConfigurationProperties(prefix = "app.rate-limit")
public record RateLimitProperties(@NotEmpty Map<@NotBlank String, @NotNull @Valid Rule> policies,

		@NotNull @Valid Cache cache) {

	/** 하나의 경로와 HTTP 메서드에 적용할 IP·계정 제한입니다. */
	public record Rule(@NotBlank @Pattern(regexp = "/.*") String path,

			@NotEmpty Set<@NotBlank String> methods,

			@NotNull ResponseFormat responseFormat,

			@NotEmpty Map<@NotNull Scope, @NotNull @Valid Limit> limits) {
	}

	/** 최대 capacity개의 토큰을 저장하고, refillPeriod 동안 refillTokens개를 점진적으로 충전합니다. */
	public record Limit(@Positive long capacity,

			@Positive long refillTokens,

			@NotNull @DurationMin(seconds = 1) Duration refillPeriod) {
	}

	/** 저장할 버킷 수와 마지막 접근 이후 만료 시간을 설정합니다. */
	public record Cache(
			// 버킷 슬롯을 관리하는 Semaphore는 int 범위까지만 지원합니다.
			@Positive @Max(Integer.MAX_VALUE) long maximumSize,

			@NotNull @DurationMin(seconds = 1) Duration expireAfterAccess) {
	}
}
