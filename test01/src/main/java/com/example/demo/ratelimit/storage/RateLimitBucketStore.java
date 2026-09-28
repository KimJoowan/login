package com.example.demo.ratelimit.storage;

import com.example.demo.ratelimit.config.RateLimitProperties;
import com.example.demo.ratelimit.policy.RateLimitPolicy;

import java.util.concurrent.Semaphore;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;

import io.github.bucket4j.Bucket;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;

/**
 * Rate Limit 버킷의 생성, 저장, 만료 및 캐시 용량을 관리합니다.
 */
@Component
public class RateLimitBucketStore {

	private final Cache<BucketKey, Bucket> buckets;
	private final Semaphore bucketSlots;
	private final Counter rejectedBucketCounter;

	public RateLimitBucketStore(RateLimitProperties properties, MeterRegistry meterRegistry) {

		RateLimitProperties.Cache cacheSettings = properties.cache();
		int maximumBuckets = Math.toIntExact(cacheSettings.maximumSize());

		this.bucketSlots = new Semaphore(maximumBuckets);
		this.rejectedBucketCounter = meterRegistry.counter("ratelimit.cache.admission.rejected");

		Counter expiredBucketCounter = meterRegistry.counter("ratelimit.cache.evictions", "cause", "expired");

		// 용량 초과 시 기존 버킷을 퇴출하면 Rate Limit이 초기화될 수 있으므로
		// 슬롯으로 동시에 저장할 수 있는 버킷 수를 제한합니다.
		this.buckets = Caffeine.newBuilder().expireAfterAccess(cacheSettings.expireAfterAccess()).recordStats()
				.evictionListener((BucketKey key, Bucket bucket, RemovalCause cause) -> {
					if (cause == RemovalCause.EXPIRED) {
						bucketSlots.release();
						expiredBucketCounter.increment();
					}
				}).build();

		CaffeineCacheMetrics.monitor(meterRegistry, buckets, "rateLimitBuckets");
	}

	/**
	 * 정책과 식별자에 해당하는 버킷을 반환합니다. 버킷이 없으면 새로 생성합니다.
	 *
	 * @param policy   적용할 Rate Limit 정책
	 * @param identity 클라이언트 식별자
	 * @return 기존 또는 새로 생성된 버킷
	 * @throws CapacityExceededException 새 버킷을 저장할 공간이 없을 때
	 */
	public Bucket getOrCreate(RateLimitPolicy policy, String identity) {

		BucketKey key = new BucketKey(policy.name(), policy.scope(), identity);

		Bucket bucket = buckets.getIfPresent(key);

		if (bucket != null) {
			return bucket;
		}

		return getOrCreateBucket(key, policy);
	}

	/**
	 * 캐시 미스일 때 버킷을 생성합니다.
	 *
	 * 동기화 대기 중 다른 스레드가 같은 버킷을 생성했을 수 있으므로 잠금 획득 후 다시 캐시를 확인합니다.
	 */
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

	/**
	 * 새 버킷을 저장할 슬롯을 확보합니다.
	 */
	private void reserveSlot() {

		if (bucketSlots.tryAcquire()) {
			return;
		}

		// 만료됐지만 아직 정리되지 않은 캐시 항목을 제거합니다.
		buckets.cleanUp();

		if (!bucketSlots.tryAcquire()) {
			rejectedBucketCounter.increment();
			throw new CapacityExceededException();
		}
	}

	/**
	 * 정책에 맞는 Bucket4j 버킷을 생성합니다.
	 */
	private static Bucket createBucket(RateLimitPolicy policy) {

		return Bucket.builder().addLimit(
				limit -> limit.capacity(policy.capacity()).refillGreedy(policy.refillTokens(), policy.refillPeriod()))
				.build();
	}


	/**
	 * 캐시에 저장되는 버킷을 구분하는 내부 키입니다.
	 */
	private record BucketKey(String policyName, RateLimitPolicy.Scope scope, String identity) {
	}
}