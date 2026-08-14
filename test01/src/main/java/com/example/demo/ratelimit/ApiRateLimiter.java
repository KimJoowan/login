package com.example.demo.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Component
@RequiredArgsConstructor
public class ApiRateLimiter {

	private final RateLimitBucketFactory bucketFactory;

	private final Cache<String, Bucket> cache = Caffeine.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();

	public ConsumptionProbe tryConsumeSession(String clientKey, String endpoint) {

		Bucket bucket = cache.get("session:" + clientKey, ignored -> bucketFactory.createSessionBucket(endpoint));

		return consume(bucket);
	}

	public ConsumptionProbe tryConsumeIp(String clientIp, String endpoint) {

		String key = "ip:" + endpoint + ":" + clientIp;

		Bucket bucket = cache.get(key, ignored -> bucketFactory.createIpBucket(endpoint));

		return consume(bucket);
	}

	public ConsumptionProbe tryConsumeGlobal(String endpoint) {
		String key = "global:" + endpoint;

		Bucket bucket = cache.get(key, ignored -> bucketFactory.createGlobalBucket(endpoint));

		return consume(bucket);
	}

	private ConsumptionProbe consume(Bucket bucket) {
		return bucket.tryConsumeAndReturnRemaining(1);
	}

	public String getClientIp(HttpServletRequest request) {
		String remoteAddress = request.getRemoteAddr();

		return remoteAddress != null && !remoteAddress.isBlank() ? remoteAddress : "unknown";
	}

	public String createClientKey(HttpServletRequest request, String endpoint) {
		String clientIp = getClientIp(request);

		// 기존 세션만 조회하며 새 세션은 생성하지 않음
		HttpSession session = request.getSession(false);

		String sessionId = session != null ? session.getId() : "anonymous";

		return endpoint + ":" + clientIp + ":" + sessionId;
	}

}