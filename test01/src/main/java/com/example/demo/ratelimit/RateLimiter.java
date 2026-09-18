package com.example.demo.ratelimit;

import java.math.BigInteger;
import java.time.Duration;
import java.util.concurrent.Semaphore;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;

/**
 * 식별자별 토큰 버킷과 캐시를 관리합니다.
 */
@Component
public class RateLimiter {

	private static final long TOKENS_PER_REQUEST = 1;
	private static final BigInteger NANOS_PER_SECOND = BigInteger.valueOf(1_000_000_000L);

	private final Cache<BucketKey, Bucket> buckets;
	private final Semaphore bucketSlots;
	private final Counter rejectedBucketCounter;

	public RateLimiter(RateLimitProperties properties, MeterRegistry meterRegistry) {
		validateCacheExpiration(properties);

		RateLimitProperties.Cache cacheSettings = properties.cache();
		int maximumBuckets = Math.toIntExact(cacheSettings.maximumSize());
		this.bucketSlots = new Semaphore(maximumBuckets);
		this.rejectedBucketCounter = meterRegistry.counter("ratelimit.cache.admission.rejected");

		Counter expiredBucketCounter = meterRegistry.counter("ratelimit.cache.evictions", "cause", "expired");

		// 용량 초과 시 기존 버킷을 퇴출하면 제한이 초기화되므로, 슬롯으로 새 버킷 생성을 제한합니다.
		this.buckets = Caffeine.newBuilder()
				.expireAfterAccess(cacheSettings.expireAfterAccess())
				.recordStats()
				.evictionListener((BucketKey key, Bucket bucket, RemovalCause cause) -> {
					if (cause == RemovalCause.EXPIRED) {
						bucketSlots.release();
						expiredBucketCounter.increment();
					}
				})
				.build();

		CaffeineCacheMetrics.monitor(meterRegistry, buckets, "rateLimitBuckets");
	}

	/**
	 * 해당 버킷에서 요청 토큰 하나를 소비합니다.
	 *
	 * @param policy 적용할 정책
	 * @param identity 클라이언트 식별자
	 * @return 토큰 소비 결과
	 * @throws CapacityExceededException 새 버킷을 저장할 캐시 공간이 없을 때
	 */
	public ConsumptionProbe tryConsume(RateLimitPolicy policy, String identity) {
		BucketKey key = new BucketKey(policy.name(), policy.scope(), identity);
		Bucket bucket = buckets.getIfPresent(key);
		if (bucket == null) {
			bucket = getOrCreateBucket(key, policy);
		}

		return bucket.tryConsumeAndReturnRemaining(TOKENS_PER_REQUEST);
	}

	// 캐시 미스일 때만 잠금을 획득하고, 대기 중 생성된 버킷이 있는지 다시 확인합니다.
	private synchronized Bucket getOrCreateBucket(BucketKey key, RateLimitPolicy policy) {
		Bucket bucket = buckets.getIfPresent(key);
		if (bucket != null) {
			return bucket;
		}

		reserveSlot();
		try {
			Bucket createdBucket = createBucket(policy);
			buckets.put(key, createdBucket);
			return createdBucket;
		} catch (RuntimeException e) {
			bucketSlots.release();
			throw e;
		}
	}

	// 만료된 항목을 정리한 뒤 새 버킷의 캐시 공간을 확보합니다.
	private void reserveSlot() {
		if (bucketSlots.tryAcquire()) {
			return;
		}

		buckets.cleanUp();
		if (!bucketSlots.tryAcquire()) {
			rejectedBucketCounter.increment();
			throw new CapacityExceededException();
		}
	}

	// 정책에 맞는 Bucket4j 버킷을 생성합니다.
	private static Bucket createBucket(RateLimitPolicy policy) {
		return Bucket.builder()
				.addLimit(limit -> limit.capacity(policy.capacity())
						.refillGreedy(policy.refillTokens(), policy.refillPeriod()))
				.build();
	}

	// 만료 후 새 버킷이 생성되어도 제한이 느슨해지지 않도록 전체 충전 시간을 보장합니다.
	private static void validateCacheExpiration(RateLimitProperties properties) {
		BigInteger expirationNanos = toBigNanos(properties.cache().expireAfterAccess());

		for (RateLimitProperties.Rule rule : properties.policies().values()) {
			for (RateLimitProperties.Limit limit : rule.limits().values()) {
				// 만료시간 × 충전 토큰 >= 충전주기 × 용량: 나눗셈의 반올림 없이 비교합니다.
				BigInteger availableRefill = expirationNanos.multiply(BigInteger.valueOf(limit.refillTokens()));
				BigInteger requiredRefill = toBigNanos(limit.refillPeriod())
						.multiply(BigInteger.valueOf(limit.capacity()));

				if (availableRefill.compareTo(requiredRefill) < 0) {
					throw new IllegalArgumentException("Rate Limit 캐시 만료시간은 모든 버킷의 전체 충전 시간 이상이어야 합니다.");
				}
			}
		}
	}

	// 긴 Duration도 오버플로 없이 비교합니다.
	private static BigInteger toBigNanos(Duration duration) {
		return BigInteger.valueOf(duration.getSeconds()).multiply(NANOS_PER_SECOND)
				.add(BigInteger.valueOf(duration.getNano()));
	}

	public static final class CapacityExceededException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		/** 버킷 캐시가 최대 용량에 도달했을 때 발생합니다. */
		public CapacityExceededException() {
			super("Rate limit bucket storage is full");
		}
	}

	private record BucketKey(String policyName, RateLimitPolicy.Scope scope, String identity) {
	}
}
