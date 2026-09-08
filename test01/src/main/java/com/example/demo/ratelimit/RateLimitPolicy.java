package com.example.demo.ratelimit;

import java.time.Duration;
import java.util.Objects;

public record RateLimitPolicy(
        String name,
        Scope scope,
        long capacity,
        long refillTokens,
        Duration refillPeriod,
        ResponseFormat responseFormat) {

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

    public enum Scope {
        IP, ACCOUNT
    }

    public enum ResponseFormat {
        HTML, JSON
    }
}