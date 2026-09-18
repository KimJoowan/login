package com.example.demo.ratelimit;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

/**
 * Rate Limit 버킷에 사용할 IP 및 계정 식별자를 생성합니다.
 */
@Component
@Data
public class IdentityResolver {

	private final HMAC_SHA256 hmacSha256;
	
	private static final String UNKNOWN_IP = "unknown";
	private static final Pattern VALID_ACCOUNT_ID = Pattern.compile("[a-zA-Z0-9_]{4,30}");

	/** 요청의 원격 주소를 반환하며, 주소가 없으면 {@code unknown}을 사용합니다. */
	public String clientIp(HttpServletRequest request) {
		String remoteAddress = request.getRemoteAddr();
		
		if (remoteAddress == null || remoteAddress.isBlank()) {
			return UNKNOWN_IP;
		}
		return remoteAddress;
	}

	/** 로그인 ID를 HMAC으로 해싱하여 원문이 노출되지 않는 식별자를 만듭니다. */
	public String accountHash(HttpServletRequest request) {
		String rawAccountId = request.getParameter("id");
		String accountId = rawAccountId == null ? "" : rawAccountId.trim();

		if (VALID_ACCOUNT_ID.matcher(accountId).matches()) {
			return hmacSha256.hash("id:" + accountId);
		}

		// ID가 없거나 형식이 잘못된 요청은 IP별로 같은 버킷을 사용합니다.
		return hmacSha256.hash("no-id:" + clientIp(request));
	}
	
}
