package com.example.demo.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Component
public class ApiRateLimiter {

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
		String key = policy.cacheKey(identity);
		Bucket bucket = cache.get(key, ignored -> bucketFactory.createBucket(policy));

		return consume(bucket);
	}

	private ConsumptionProbe consume(Bucket bucket) {
		return bucket.tryConsumeAndReturnRemaining(1);
	}
}

