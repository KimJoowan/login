package com.example.demo.ratelimit;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "app.rate-limit")
public record RateLimitProperties(
        @NotNull @Valid Login login,
        @NotNull @Valid IpOnly signup,
        @NotNull @Valid IpOnly checkId
) {
    public record Login(
            @NotNull @Valid Limit ip,
            @NotNull @Valid Limit account
    ) {}

    public record IpOnly(@NotNull @Valid Limit ip) {}

    public record Limit(
            @Min(1) long capacity,
            @Min(1) long refillTokens,
            @NotNull Duration refillPeriod
    ) {}
}

