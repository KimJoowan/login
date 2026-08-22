package com.example.demo.ratelimit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class ClientIdentityResolverTest {

    private final ClientIdentityResolver resolver = new ClientIdentityResolver(
            new RateLimitKeyHasher(Base64.getEncoder().encodeToString(new byte[32]))
    );

    @Test
    void resolvesRemoteAddressAsClientIp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.0.2.10");

        assertThat(resolver.getClientIp(request)).isEqualTo("192.0.2.10");
    }

    @Test
    void trimsValidLoginIdBeforeHashing() {
        MockHttpServletRequest original = requestWithId("member_1");
        MockHttpServletRequest padded = requestWithId("  member_1  ");

        assertThat(resolver.getLoginAccountHash(padded))
                .isEqualTo(resolver.getLoginAccountHash(original));
    }

    @Test
    void groupsInvalidLoginIdsIntoOneBucketIdentity() {
        MockHttpServletRequest first = requestWithId("잘못된 아이디");
        MockHttpServletRequest second = requestWithId("!!invalid!!");

        assertThat(resolver.getLoginAccountHash(first))
                .isEqualTo(resolver.getLoginAccountHash(second));
    }

    private MockHttpServletRequest requestWithId(String id) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("id", id);
        return request;
    }
}
