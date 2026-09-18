package com.example.demo.ratelimit;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

@Data
public class HMAC_SHA256 {
	
	private static final String HMAC_ALGORITHM = "HmacSHA256";
	private static final int MINIMUM_KEY_BYTES = 32;
	private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
	private final ThreadLocal<Mac> macByThread = ThreadLocal.withInitial(this::createMac);;
	
	@Value("${myapp.security.rate-limit-hmac-key}")
	private String encodedSecretKey;
	
	private final SecretKeySpec secretKey = new SecretKeySpec(decodeKey(encodedSecretKey), HMAC_ALGORITHM);
	
	private Mac createMac() {
		try {
			Mac mac = Mac.getInstance(HMAC_ALGORITHM);
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

    if (keyBytes.length < MINIMUM_KEY_BYTES) {
        throw new IllegalArgumentException(
                "RateLimit HMAC key must be at least " + MINIMUM_KEY_BYTES + " bytes long (got " + keyBytes.length + ")");
    }

    return keyBytes;
}

}
