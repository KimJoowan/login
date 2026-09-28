package com.example.demo.ratelimit.identity;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HMAC_SHA256 {
	
	private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
	private final ThreadLocal<Mac> macByThread = ThreadLocal.withInitial(this::createMac);
	private final SecretKeySpec secretKey;
	
	public HMAC_SHA256(@Value("${myapp.security.rate-limit-hmac-key}")String encodedSecretKey) {
        this.secretKey = new SecretKeySpec(decodeKey(encodedSecretKey), "HmacSHA256");
    }
	
	private Mac createMac() {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
				mac.init(secretKey);
				
			return mac;
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Failed to create Rate Limit identifier", e);
		}
	}
	
	// 값을 HMAC-SHA256으로 해싱합니다.
	String hash(String value) {
		byte[] digest = macByThread.get().doFinal(value.getBytes(StandardCharsets.UTF_8));
		return URL_ENCODER.encodeToString(digest);
	}

	// 비밀키를 디코딩하고 최소 길이를 검증합니다.
	private static byte[] decodeKey(String encodedSecretKey) {
		
		if (encodedSecretKey == null || encodedSecretKey.isBlank()) {
			throw new IllegalArgumentException("RateLimit HMAC key must not be null or empty");
		}

		final byte[] keyBytes;
		
		try {
			// Base64 디코딩 (표준 Base64 디코더 사용)
			keyBytes = Base64.getDecoder().decode(encodedSecretKey.trim());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("RateLimit HMAC key must be Base64-encoded", e);
		}

		if (keyBytes.length < 32) {
			throw new IllegalArgumentException(
                "RateLimit HMAC key must be at least 32bytes long (got " + keyBytes.length + ")"
			);
		}

		return keyBytes;
	}

}
