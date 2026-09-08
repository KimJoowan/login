package com.example.demo.ratelimit;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class RateLimitKeyHasher {

	private static final String ALGORITHM = "HmacSHA256";
	private static final int MINIMUM_KEY_BYTES = 32;
	private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

	private final SecretKeySpec secretKey;
	private final ThreadLocal<Mac> macByThread;

	public RateLimitKeyHasher(@Value("${myapp.security.rate-limit-hmac-key}") String encodedSecretKey) {
		this.secretKey = new SecretKeySpec(parseAndValidateKey(nonNullOrEmpty(encodedSecretKey)), ALGORITHM);
		this.macByThread = ThreadLocal.withInitial(this::createInitializedMac);
	}

	public String hmacSha256(String value) {
		return hmacSha256(value.getBytes(StandardCharsets.UTF_8));
	}

	public String hmacSha256(byte[] data) {
		return URL_ENCODER.encodeToString(rawHmacSha256(data));
	}

	byte[] rawHmacSha256(byte[] data) {
		return macByThread.get().doFinal(data);
	}

	static String nonNullOrEmpty(String s) {
		if (s == null || s.isBlank()) {
			throw new IllegalArgumentException("RateLimit HMAC key must not be null or empty");
		}
		return s;
	}

	private byte[] parseAndValidateKey(String encodedSecretKey) {
		final byte[] keyBytes;

		// Base64 형식 검사
		try {
			keyBytes = Base64.getDecoder().decode(encodedSecretKey);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("RateLimit HMAC key must be Base64-encoded", e);
		}

		// 디코딩한 키의 길이 검사
		if (keyBytes.length < MINIMUM_KEY_BYTES) {
			throw new IllegalArgumentException(
					"RateLimit HMAC key must be at least " + MINIMUM_KEY_BYTES + " bytes long");
		}

		return keyBytes;
	}

	private Mac createInitializedMac() {
		try {
			Mac mac = Mac.getInstance(ALGORITHM);
			mac.init(secretKey);
			return mac;
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Failed to create Rate Limit identifier", e);
		}
	}
}
