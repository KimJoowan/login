package com.example.demo.ratelimit;

import java.time.Duration;
import java.util.Objects;

public record RateLimitPolicy(
        String name,
        Scope scope,
        long capacity,
        long refillTokens,
        Duration refillPeriod
) {
    public RateLimitPolicy {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(scope, "scope");
        Objects.requireNonNull(refillPeriod, "refillPeriod");

        if (capacity < 1 || refillTokens < 1 || refillPeriod.isZero() || refillPeriod.isNegative()) {
            throw new IllegalArgumentException("Rate Limit 값은 0보다 커야 합니다.");
        }
    }

    public String cacheKey(String identity) {
        return scope.keyPrefix + ":" + name + ":v1:" + identity;
    }

    public enum Scope {
        IP("ip"),
        ACCOUNT("account");

        private final String keyPrefix;

        Scope(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }
    }
}
