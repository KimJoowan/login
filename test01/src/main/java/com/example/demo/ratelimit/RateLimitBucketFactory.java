package com.example.demo.ratelimit;

import java.time.Duration;

import org.springframework.stereotype.Component;

import io.github.bucket4j.Bucket;

@Component
public class RateLimitBucketFactory {

    public Bucket createSessionBucket(String endpoint) {
        if ("check-id".equals(endpoint)) {
            return createBucket(10, 5);
        }

        return createBucket(30, 10);
    }

    public Bucket createIpBucket(String endpoint) {
        if ("check-id".equals(endpoint)) {
            return createBucket(20, 10);
        }

        return createBucket(60, 20);
    }

    public Bucket createGlobalBucket(String endpoint) {
        if ("check-id".equals(endpoint)) {
            return createBucket(100, 100);
        }

        return createBucket(1_000, 1_000);
    }

    private Bucket createBucket(long capacity, long refillPerMinute) {

        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(capacity)
                        .refillGreedy(
                                refillPerMinute,
                                Duration.ofMinutes(1)
                        ))
                .build();
    }
}