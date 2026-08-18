package com.example.demo.ratelimit;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class ClientIdentityResolver {

	public String getClientIp(HttpServletRequest request) {
		String remoteAddress = request.getRemoteAddr();

		return remoteAddress != null && !remoteAddress.isBlank() ? remoteAddress : "unknown";
	}

	public String createClientKey(HttpServletRequest request, String endpoint) {
		String clientIp = getClientIp(request);
		HttpSession session = request.getSession(false);
		String sessionId = "anonymous";

		if(session != null) {
			sessionId = session.getId();
		}

		return endpoint + ":" + clientIp + ":" + sessionId;
	}
}
