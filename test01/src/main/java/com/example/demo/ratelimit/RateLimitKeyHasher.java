package com.example.demo.ratelimit;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RateLimitKeyHasher {

    private static final String ALGORITHM = "HmacSHA256";
    private final SecretKeySpec secretKey;

    public RateLimitKeyHasher(
            @Value("${myapp.security.rate-limit-hmac-key}") String encodedSecretKey) {
        byte[] keyBytes;

        try {
            keyBytes = Base64.getDecoder().decode(encodedSecretKey);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Rate Limit HMAC 키는 Base64 형식이어야 합니다.", exception);
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException("Rate Limit HMAC 키는 최소 32바이트여야 합니다.");
        }

        this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public String hmacSha256(String value) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(secretKey);

            byte[] hash = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Rate Limit 식별자 생성에 실패했습니다.", exception);
        }
    }
}
