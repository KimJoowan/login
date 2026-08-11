package com.example.demo.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Component
public class ApiRateLimiter {

    // 1. Caffeine 캐시 정의 (10분간 요청이 없으면 캐시에서 버킷 삭제)
    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    // 2. IP 또는 API Key별 버킷 가져오기/생성하기
    public Bucket resolveBucket(String key) {
        return cache.get(key, k -> createNewBucket());
    }

    // 3. 버킷 정책 정의 (예: 1분당 최대 10개 요청 허용)
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(30)
                .refillGreedy(10, Duration.ofMinutes(1))
                .build();
        
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    // 4. API 요청 허용 여부 확인 메소드
    public boolean tryConsume(String key) {
        Bucket bucket = resolveBucket(key);
        return bucket.tryConsume(1); // 토큰 1개 소비 시도
    }
}
