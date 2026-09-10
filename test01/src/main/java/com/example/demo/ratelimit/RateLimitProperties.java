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
        @NotEmpty Map<String, @Valid Rule> policies,
        @NotNull @Valid Cache cache) {

    public record Rule(
            @NotBlank
            @Pattern(regexp = "/.*")
            String path,

            @NotEmpty
            Set<@NotBlank String> methods,

            @NotNull
            RateLimitPolicy.ResponseFormat responseFormat,

            @NotEmpty
            Map<RateLimitPolicy.Scope, @Valid Limit> limits) {
    }

    public record Limit(
            @Positive long capacity,
            @Positive long refillTokens,
            @NotNull @DurationMin(seconds = 1)
            Duration refillPeriod) {
    }

    public record Cache(
            @Positive long maximumSize,
            @NotNull @DurationMin(seconds = 1)
            Duration expireAfterAccess) {
    }
}