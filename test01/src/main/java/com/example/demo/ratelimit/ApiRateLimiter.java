package com.example.demo.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import io.micrometer.core.instrument.Counter;

import java.math.BigInteger;
import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Component;


@Component
public class ApiRateLimiter {

	private static final long TOKENS_PER_REQUEST = 1;

	private final Cache<BucketKey, Bucket> buckets;
	private final RateLimitBucketFactory bucketFactory;
	

	public ApiRateLimiter(
	        RateLimitProperties properties,
	        RateLimitBucketFactory bucketFactory,
	        MeterRegistry meterRegistry) {

	    this.bucketFactory = bucketFactory;
	    
	    validateCacheExpiration(properties);

	    Counter sizeEvictions = meterRegistry.counter(
	            "ratelimit.cache.evictions",
	            "cause", "size");

	    Counter expiredEvictions = meterRegistry.counter(
	            "ratelimit.cache.evictions",
	            "cause", "expired");

	    this.buckets = Caffeine.newBuilder()
	            .maximumSize(properties.cache().maximumSize())
	            .expireAfterAccess(properties.cache().expireAfterAccess())
	            .recordStats()
	            .evictionListener(
	                    (BucketKey key, Bucket bucket, RemovalCause cause) -> {
	                        if (cause == RemovalCause.SIZE) {
	                            sizeEvictions.increment();
	                        } else if (cause == RemovalCause.EXPIRED) {
	                            expiredEvictions.increment();
	                        }
	                    })
	            .build();

	    CaffeineCacheMetrics.monitor(
	            meterRegistry,
	            buckets,
	            "rateLimitBuckets");
	}

	private static void validateCacheExpiration(
	        RateLimitProperties properties) {

	    BigInteger cacheNanos =
	            toBigNanos(properties.cache().expireAfterAccess());

	    List<RateLimitProperties.Limit> limits = List.of(
	            properties.login().ip(),
	            properties.login().account(),
	            properties.signup().ip(),
	            properties.checkId().ip()
	    );

	    for (RateLimitProperties.Limit limit : limits) {
	        // 만료시간 × 충전 토큰 >= 충전주기 × 용량
	        // 나눗셈 없이 비교하여 반올림 오류를 피합니다.
	        BigInteger availableRefill = cacheNanos.multiply(
	                BigInteger.valueOf(limit.refillTokens()));

	        BigInteger requiredRefill = toBigNanos(limit.refillPeriod())
	                .multiply(BigInteger.valueOf(limit.capacity()));

	        if (availableRefill.compareTo(requiredRefill) < 0) {
	            throw new IllegalArgumentException(
	                    "Rate Limit 캐시 만료시간은 모든 버킷의 "
	                    + "전체 충전 시간 이상이어야 합니다.");
	        }
	    }
	}

	private static BigInteger toBigNanos(Duration duration) {
	    return BigInteger.valueOf(duration.getSeconds())
	            .multiply(BigInteger.valueOf(1_000_000_000L))
	            .add(BigInteger.valueOf(duration.getNano()));
	}
	
	public ConsumptionProbe tryConsume(RateLimitPolicy policy, String identity) {
		BucketKey key = new BucketKey(policy.name(), policy.scope(), identity);
		Bucket bucket = buckets.get(key, ignored -> bucketFactory.createBucket(policy));
		return bucket.tryConsumeAndReturnRemaining(TOKENS_PER_REQUEST);
	}

	private record BucketKey(String policyName, RateLimitPolicy.Scope scope, String identity) {
	}
}
