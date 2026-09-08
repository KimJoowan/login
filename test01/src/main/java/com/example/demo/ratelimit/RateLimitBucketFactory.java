package com.example.demo.ratelimit;

import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

@Component
public class RateLimitBucketFactory {

    public Bucket createBucket(RateLimitPolicy policy) {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(policy.capacity())
                        .refillGreedy(policy.refillTokens(), policy.refillPeriod()))
                .build();
    }
}
