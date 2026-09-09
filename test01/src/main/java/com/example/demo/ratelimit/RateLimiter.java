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
import java.util.concurrent.Semaphore;

import org.springframework.stereotype.Component;

/**
 * 식별자별 토큰 버킷과 캐시를 관리합니다.
 */
@Component
public class RateLimiter {

	private static final long TOKENS_PER_REQUEST = 1;

	private final Cache<BucketKey, Bucket> buckets;
	private final Semaphore bucketSlots;
	private final Counter admissionRejected;

	/**
	 * 버킷 캐시와 관련 메트릭을 초기화합니다.
	 *
	 * @param properties Rate Limit 설정
	 * @param meterRegistry 메트릭 레지스트리
	 */
	public RateLimiter(RateLimitProperties properties, MeterRegistry meterRegistry) {
		validateCacheExpiration(properties);

		int maximumBuckets = Math.toIntExact(properties.cache().maximumSize());
		this.bucketSlots = new Semaphore(maximumBuckets);
		this.admissionRejected = meterRegistry.counter("ratelimit.cache.admission.rejected");

		Counter expiredEvictions = meterRegistry.counter("ratelimit.cache.evictions", "cause", "expired");

		this.buckets = Caffeine.newBuilder()
				.expireAfterAccess(properties.cache().expireAfterAccess()).recordStats()
				.evictionListener((BucketKey key, Bucket bucket, RemovalCause cause) -> {
					if (cause == RemovalCause.EXPIRED) {
						bucketSlots.release();
						expiredEvictions.increment();
					}
				}).build();

		CaffeineCacheMetrics.monitor(meterRegistry, buckets, "rateLimitBuckets");
	}

	// 완전히 충전되기 전에 버킷이 만료되지 않도록 검증합니다.
	private static void validateCacheExpiration(RateLimitProperties properties) {
		BigInteger cacheNanos = toBigNanos(properties.cache().expireAfterAccess());

		for (RateLimitProperties.Rule rule : properties.policies().values()) {
			for (RateLimitProperties.Limit limit : rule.limits().values()) {
				// 만료시간 × 충전 토큰 >= 충전주기 × 용량
				// 나눗셈 없이 비교하여 반올림 오류를 피합니다.
				BigInteger availableRefill = cacheNanos.multiply(BigInteger.valueOf(limit.refillTokens()));

				BigInteger requiredRefill = toBigNanos(limit.refillPeriod())
						.multiply(BigInteger.valueOf(limit.capacity()));

				if (availableRefill.compareTo(requiredRefill) < 0)
					throw new IllegalArgumentException("Rate Limit 캐시 만료시간은 모든 버킷의 전체 충전 시간 이상이어야 합니다.");
			}
		}
	}

	// Duration 비교 과정의 오버플로를 방지합니다.
	private static BigInteger toBigNanos(Duration duration) {
		return BigInteger.valueOf(duration.getSeconds()).multiply(BigInteger.valueOf(1_000_000_000L))
				.add(BigInteger.valueOf(duration.getNano()));
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
		if (bucket == null)
			bucket = createBucket(key, policy);

		return bucket.tryConsumeAndReturnRemaining(TOKENS_PER_REQUEST);
	}

	// 동일 키의 버킷이 중복 생성되지 않도록 동기화합니다.
	private synchronized Bucket createBucket(BucketKey key, RateLimitPolicy policy) {
		Bucket bucket = buckets.getIfPresent(key);
		if (bucket == null) {
			reserveSlot();

			try {
				bucket = newBucket(policy);
				buckets.put(key, bucket);
			} catch (RuntimeException e) {
				bucketSlots.release();
				throw e;
			}
		}
		return bucket;
	}

	// 만료된 항목을 정리한 뒤 새 버킷의 캐시 공간을 확보합니다.
	private void reserveSlot() {
		if (bucketSlots.tryAcquire())
			return;

		buckets.cleanUp();
		if (!bucketSlots.tryAcquire()) {
			admissionRejected.increment();
			throw new CapacityExceededException();
		}
	}

	// 정책에 맞는 Bucket4j 버킷을 생성합니다.
	private static Bucket newBucket(RateLimitPolicy policy) {
		return Bucket.builder()
				.addLimit(limit -> limit.capacity(policy.capacity())
						.refillGreedy(policy.refillTokens(), policy.refillPeriod()))
				.build();
	}

	public static final class CapacityExceededException extends RuntimeException {

		/** 버킷 캐시가 최대 용량에 도달했을 때 발생합니다. */
		public CapacityExceededException() {
			super("Rate limit bucket storage is full");
		}
	}

	private record BucketKey(String policyName, RateLimitPolicy.Scope scope, String identity) {
	}
}
