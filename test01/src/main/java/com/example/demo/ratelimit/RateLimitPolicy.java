package com.example.demo.ratelimit;

import java.time.Duration;
import java.util.Objects;

/**
 * 단일 식별 범위에 적용할 토큰 버킷 정책입니다.
 */
public record RateLimitPolicy(
        String name,
        Scope scope,
        long capacity,
        long refillTokens,
        Duration refillPeriod,
        ResponseFormat responseFormat) {

    // 생성 시 정책값의 유효성을 검증합니다.
    public RateLimitPolicy {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(scope, "scope");
        Objects.requireNonNull(refillPeriod, "refillPeriod");
        Objects.requireNonNull(responseFormat, "responseFormat");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Policy name must not be blank");
        }

        if (capacity < 1
                || refillTokens < 1
                || refillPeriod.isZero()
                || refillPeriod.isNegative()) {
            throw new IllegalArgumentException(
                    "Rate Limit values must be positive");
        }
    }

    /** 요청을 구분하는 기준입니다. */
    public enum Scope {
        IP, ACCOUNT
    }

    /** 요청 제한 시 반환할 응답 형식입니다. */
    public enum ResponseFormat {
        HTML, JSON
    }
}
