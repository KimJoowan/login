package com.example.demo.ratelimit;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClientIdentityResolver {

	private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,30}$");
	private final RateLimitKeyHasher keyHasher;

	public String getClientIp(HttpServletRequest request) {
		String remoteAddress = request.getRemoteAddr();

		return remoteAddress != null && !remoteAddress.isBlank() ? remoteAddress : "unknown";
	}

	public String getLoginAccountHash(HttpServletRequest request) {
		String normalizedId = normalizeId(request.getParameter("id"));

		return keyHasher.hmacSha256(normalizedId);
	}

	private String normalizeId(String rawId) {
		if (rawId == null) {
			return "missing";
		}

		String normalizedId = rawId.strip();

		return ID_PATTERN.matcher(normalizedId).matches() ? normalizedId : "invalid";
	}
}

