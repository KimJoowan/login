package com.example.demo.ratelimit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.rate-limit")
public record RateLimitProperties(
        @NotNull @Valid Login login,
        @NotNull @Valid IpLimit signup,
        @NotNull @Valid IpLimit checkId,
        @NotNull @Valid Cache cache) {

    public record Login(@NotNull @Valid Limit ip,
                        @NotNull @Valid Limit account) {}

    public record IpLimit(@NotNull @Valid Limit ip) {}

    public record Limit(@Positive long capacity,
                        @Positive long refillTokens,
                        @NotNull @DurationMin(seconds = 1) Duration refillPeriod) {}

    public record Cache(@Positive long maximumSize,
                        @NotNull @DurationMin(seconds = 1) Duration expireAfterAccess) {}
}
