package com.example.demo.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientIdentityResolver {

    private static final String UNKNOWN_IP = "unknown";
    private static final String ID_PARAMETER = "id";

    private final RateLimitKeyHasher keyHasher;

    public ClientIdentityResolver(RateLimitKeyHasher keyHasher) {
        this.keyHasher = keyHasher;
    }

    public String getClientIp(HttpServletRequest request) {
        String remoteAddress = request.getRemoteAddr();
        return remoteAddress != null && !remoteAddress.isBlank() ? remoteAddress : UNKNOWN_IP;
    }

    public String getLoginAccountHash(HttpServletRequest request) {
        String rawId = request.getParameter(ID_PARAMETER);
        String normalizedId = rawId == null ? null : rawId.strip();

        String subject = (normalizedId != null && !normalizedId.isEmpty() && isValidId(normalizedId))
                ? "id:" + normalizedId
                : "no-id:" + getClientIp(request);

        return keyHasher.hmacSha256(subject);
    }

    private static boolean isValidId(String id) {
        int len = id.length();
        if (len < 4 || len > 30) return false;
        for (int i = 0; i < len; i++) {
            char c = id.charAt(i);
            if (!(c >= 'a' && c <= 'z' ||
                  c >= 'A' && c <= 'Z' ||
                  c >= '0' && c <= '9' ||
                  c == '_')) {
                return false;
            }
        }
        return true;
    }
}
