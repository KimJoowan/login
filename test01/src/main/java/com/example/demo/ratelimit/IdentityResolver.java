package com.example.demo.ratelimit;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Rate Limit 버킷에 사용할 IP 및 계정 식별자를 생성합니다.
 */
@Component
public class IdentityResolver {

    private static final String ALGORITHM = "HmacSHA256";
    private static final int MINIMUM_KEY_BYTES = 32;
    private static final String UNKNOWN_IP = "unknown";
    private static final Pattern VALID_ID = Pattern.compile("[a-zA-Z0-9_]{4,30}");
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    private final SecretKeySpec secretKey;
    private final ThreadLocal<Mac> macByThread;

    /**
     * HMAC 비밀키를 검증하고 스레드별 해시 생성기를 준비합니다.
     *
     * @param encodedSecretKey Base64로 인코딩된 HMAC 비밀키
     */
    public IdentityResolver(@Value("${myapp.security.rate-limit-hmac-key}") String encodedSecretKey) {
        this.secretKey = new SecretKeySpec(decodeKey(encodedSecretKey), ALGORITHM);
        this.macByThread = ThreadLocal.withInitial(this::newMac);
    }

    /**
     * 요청의 원격 주소를 IP 식별자로 반환합니다.
     *
     * @param request HTTP 요청
     * @return 원격 주소 또는 {@code unknown}
     */
    public String clientIp(HttpServletRequest request) {
        String remoteAddress = request.getRemoteAddr();
        return remoteAddress != null && !remoteAddress.isBlank() ? remoteAddress : UNKNOWN_IP;
    }

    /**
     * 로그인 ID를 원문이 노출되지 않는 계정 식별자로 변환합니다.
     *
     * @param request 로그인 요청
     * @return HMAC으로 해싱된 계정 식별자
     */
    public String accountHash(HttpServletRequest request) {
        String id = request.getParameter("id");
        id = id == null ? null : id.trim();

        String subject = id != null && VALID_ID.matcher(id).matches()
                ? "id:" + id
                : "no-id:" + clientIp(request);

        return hash(subject);
    }

    // 값을 HMAC-SHA256으로 해싱합니다.
    private String hash(String value) {
        byte[] digest = macByThread.get().doFinal(value.getBytes(StandardCharsets.UTF_8));
        return URL_ENCODER.encodeToString(digest);
    }

    // 비밀키를 디코딩하고 최소 길이를 검증합니다.
    private static byte[] decodeKey(String encodedSecretKey) {
        if (encodedSecretKey == null || encodedSecretKey.isBlank())
            throw new IllegalArgumentException("RateLimit HMAC key must not be null or empty");

        final byte[] keyBytes;

        try {
            keyBytes = Base64.getDecoder().decode(encodedSecretKey);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("RateLimit HMAC key must be Base64-encoded", e);
        }

        if (keyBytes.length < MINIMUM_KEY_BYTES)
            throw new IllegalArgumentException(
                    "RateLimit HMAC key must be at least " + MINIMUM_KEY_BYTES + " bytes long");

        return keyBytes;
    }

    // 현재 스레드에서 재사용할 HMAC 인스턴스를 생성합니다.
    private Mac newMac() {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(secretKey);
            return mac;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to create Rate Limit identifier", e);
        }
    }
}
