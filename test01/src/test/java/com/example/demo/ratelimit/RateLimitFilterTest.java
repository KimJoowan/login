package com.example.demo.ratelimit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RateLimitFilterTest {

    @Test
    void getLoginPageIsNotRateLimited() throws Exception {
        RateLimitFilter filter = createFilter(properties(1, 1));

        for (int attempt = 0; attempt < 10; attempt++) {
            MockHttpServletResponse response = execute(
                    filter,
                    request("GET", "/member/login", "192.0.2.10", null, MediaType.TEXT_HTML_VALUE)
            );

            assertThat(response.getStatus()).isEqualTo(200);
        }
    }

    @Test
    void loginAccountLimitIsSharedAcrossDifferentIps() throws Exception {
        RateLimitFilter filter = createFilter(properties(10, 2));

        assertThat(execute(filter, loginRequest("192.0.2.1", "member_1")).getStatus()).isEqualTo(200);
        assertThat(execute(filter, loginRequest("192.0.2.2", "member_1")).getStatus()).isEqualTo(200);

        MockHttpServletResponse rejected = execute(filter, loginRequest("192.0.2.3", "member_1"));

        assertThat(rejected.getStatus()).isEqualTo(429);
        assertThat(rejected.getHeader("Retry-After")).isNotBlank();
        assertThat(rejected.getContentType()).startsWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    }

    @Test
    void loginIpLimitDoesNotAffectDifferentIp() throws Exception {
        RateLimitFilter filter = createFilter(properties(1, 10));

        assertThat(execute(filter, loginRequest("192.0.2.1", "member_1")).getStatus()).isEqualTo(200);
        assertThat(execute(filter, loginRequest("192.0.2.2", "member_1")).getStatus()).isEqualTo(200);
        assertThat(execute(filter, loginRequest("192.0.2.1", "member_2")).getStatus()).isEqualTo(429);
    }

    @Test
    void htmlClientReceivesHtmlRateLimitResponse() throws Exception {
        RateLimitFilter filter = createFilter(properties(1, 10));
        MockHttpServletRequest first = request(
                "POST", "/member/login", "192.0.2.1", "member_1", MediaType.TEXT_HTML_VALUE);
        MockHttpServletRequest second = request(
                "POST", "/member/login", "192.0.2.1", "member_2", MediaType.TEXT_HTML_VALUE);

        assertThat(execute(filter, first).getStatus()).isEqualTo(200);

        MockHttpServletResponse rejected = execute(filter, second);

        assertThat(rejected.getStatus()).isEqualTo(429);
        assertThat(rejected.getContentType()).startsWith(MediaType.TEXT_HTML_VALUE);
        assertThat(rejected.getContentAsString()).contains("요청 횟수를 초과했습니다");
    }

    private RateLimitFilter createFilter(RateLimitProperties properties) {
        byte[] key = new byte[32];
        String encodedKey = Base64.getEncoder().encodeToString(key);
        ClientIdentityResolver identityResolver =
                new ClientIdentityResolver(new RateLimitKeyHasher(encodedKey));

        return new RateLimitFilter(
                new ApiRateLimiter(new RateLimitBucketFactory()),
                identityResolver,
                new RateLimitPolicyResolver(properties)
        );
    }

    private RateLimitProperties properties(long loginIpCapacity, long loginAccountCapacity) {
        return new RateLimitProperties(
                new RateLimitProperties.Login(
                        limit(loginIpCapacity, Duration.ofMinutes(15)),
                        limit(loginAccountCapacity, Duration.ofMinutes(15))
                ),
                new RateLimitProperties.IpOnly(limit(5, Duration.ofHours(1))),
                new RateLimitProperties.IpOnly(limit(30, Duration.ofMinutes(1)))
        );
    }

    private RateLimitProperties.Limit limit(long capacity, Duration period) {
        return new RateLimitProperties.Limit(capacity, capacity, period);
    }

    private MockHttpServletRequest loginRequest(String ip, String id) {
        return request("POST", "/member/login", ip, id, MediaType.APPLICATION_JSON_VALUE);
    }

    private MockHttpServletRequest request(
            String method,
            String path,
            String ip,
            String id,
            String accept) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.setServletPath(path);
        request.setRemoteAddr(ip);
        request.addHeader("Accept", accept);

        if (id != null) {
            request.setParameter("id", id);
        }

        return request;
    }

    private MockHttpServletResponse execute(
            RateLimitFilter filter,
            MockHttpServletRequest request) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }
}

