package com.example.demo.ratelimit;

import org.springframework.stereotype.Component;

import io.github.bucket4j.Bucket;

@Component
public class RateLimitBucketFactory {

    public Bucket createBucket(RateLimitPolicy policy) {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(policy.capacity())
                        .refillGreedy(
                                policy.refillTokens(),
                                policy.refillPeriod()
                        ))
                .build();
    }
}

