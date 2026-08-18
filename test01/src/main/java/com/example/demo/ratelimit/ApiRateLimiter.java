package com.example.demo.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Component
@RequiredArgsConstructor
public class ApiRateLimiter {

	private final RateLimitBucketFactory bucketFactory;

	private final Cache<String, Bucket> cache = Caffeine.newBuilder().maximumSize(100_000).expireAfterAccess(30, TimeUnit.MINUTES).build();

	public ConsumptionProbe tryConsumeSession(String clientKey, String endpoint) {
		Bucket bucket = cache.get("session:" + clientKey, ignored -> bucketFactory.createSessionBucket(endpoint));

		return consume(bucket);
	}

	public ConsumptionProbe tryConsumeIp(String clientIp, String endpoint) {
		String key = "ip:" + endpoint + ":" + clientIp;

		Bucket bucket = cache.get(key, ignored -> bucketFactory.createIpBucket(endpoint));

		return consume(bucket);
	}

	private ConsumptionProbe consume(Bucket bucket) {
		return bucket.tryConsumeAndReturnRemaining(1);
	}
}