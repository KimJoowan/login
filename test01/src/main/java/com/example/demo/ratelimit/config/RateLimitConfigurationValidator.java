package com.example.demo.ratelimit.config;

import java.math.BigInteger;
import java.time.Duration;

import org.springframework.stereotype.Component;

/**
 * Rate Limit 설정값 간의 유효성을 검증합니다.
 */
@Component
public class RateLimitConfigurationValidator {

	private static final BigInteger NANOS_PER_SECOND = BigInteger.valueOf(1_000_000_000L);

	public RateLimitConfigurationValidator(RateLimitProperties properties) {
		validateCacheExpiration(properties);
	}

	// 캐시 만료시간이 모든 버킷의 전체 충전 시간 이상인지 검증합니다.
	private static void validateCacheExpiration(RateLimitProperties properties) {

		BigInteger expirationNanos = toBigNanos(properties.cache().expireAfterAccess());

		for (RateLimitProperties.Rule rule : properties.policies().values()) {

			for (RateLimitProperties.Limit limit : rule.limits().values()) {

				// 만료시간 × 충전 토큰 >= 충전주기 × 용량
				BigInteger availableRefill = expirationNanos.multiply(BigInteger.valueOf(limit.refillTokens()));

				BigInteger requiredRefill = toBigNanos(limit.refillPeriod())
						.multiply(BigInteger.valueOf(limit.capacity()));

				if (availableRefill.compareTo(requiredRefill) < 0) {
					throw new IllegalArgumentException("Rate Limit 캐시 만료시간은 " + "모든 버킷의 전체 충전 시간 이상이어야 합니다.");
				}
			}
		}
	}


	// 긴 Duration도 오버플로 없이 나노초 단위로 변환합니다.
	private static BigInteger toBigNanos(Duration duration) {
		return BigInteger.valueOf(duration.getSeconds()).multiply(NANOS_PER_SECOND)
				.add(BigInteger.valueOf(duration.getNano()));
	}
}
