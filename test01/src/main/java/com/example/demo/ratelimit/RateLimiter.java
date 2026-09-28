package com.example.demo.ratelimit;

import com.example.demo.ratelimit.policy.RateLimitPolicy;
import com.example.demo.ratelimit.storage.CapacityExceededException;
import com.example.demo.ratelimit.storage.RateLimitBucketStore;

import org.springframework.stereotype.Component;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;

/**
 * Rate Limit 정책에 따라 요청 토큰 소비를 처리합니다.
 */
@Component
public class RateLimiter {

	private static final long TOKENS_PER_REQUEST = 1;

	private final RateLimitBucketStore bucketStore;

	public RateLimiter(RateLimitBucketStore bucketStore) {
		this.bucketStore = bucketStore;
	}

	/**
	 * 해당 버킷에서 요청 토큰 하나를 소비합니다.
	 *
	 * @param policy 적용할 정책
	 * @param identity 클라이언트 식별자
	 * @return 토큰 소비 결과
	 * @throws CapacityExceededException 새 버킷을 저장할 공간이 없을 때
	 */
	public ConsumptionProbe tryConsume(
			RateLimitPolicy policy,
			String identity) {

		Bucket bucket = bucketStore.getOrCreate(policy, identity);

		return bucket.tryConsumeAndReturnRemaining(
				TOKENS_PER_REQUEST);
	}
}