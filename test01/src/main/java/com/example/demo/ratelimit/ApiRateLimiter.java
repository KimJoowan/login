package com.example.demo.ratelimit;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;

@Component
public class ApiRateLimiter {

	private static final String KEY_PREFIX = "test01:rate-limit:";

	private final RateLimitBucketFactory bucketFactory;
	private final Cache<String, Bucket> cache;

	public ApiRateLimiter(RateLimitBucketFactory bucketFactory) {
		this.bucketFactory = bucketFactory;
		this.cache = Caffeine.newBuilder()
				.maximumSize(100_000)
				.expireAfterAccess(Duration.ofHours(2))
				.build();
	}

	public ConsumptionProbe tryConsume(RateLimitPolicy policy, String identity) {
		String key = KEY_PREFIX + policy.cacheKey(identity);
		Bucket bucket = cache.get(key, ignored -> bucketFactory.createBucket(policy));

		return bucket.tryConsumeAndReturnRemaining(1);
	}
}
