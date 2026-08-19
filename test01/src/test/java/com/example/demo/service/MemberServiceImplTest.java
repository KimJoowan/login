package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.mapper.AccountLockMapper;
import com.example.demo.mapper.MemberMapper;

class MemberServiceImplTest {

    private final MemberMapper memberMapper = mock(MemberMapper.class);
    private final MemberServiceImpl service = new MemberServiceImpl(
            mock(PasswordEncoder.class),
            memberMapper,
            mock(AccountLockMapper.class)
    );

    @Test
    void existsByIdReturnsTrueWhenMemberExists() {
        when(memberMapper.existsById("existing_id")).thenReturn(1);

        assertThat(service.existsById("existing_id")).isTrue();
    }

    @Test
    void existsByIdReturnsFalseWhenMemberDoesNotExist() {
        when(memberMapper.existsById("available_id")).thenReturn(0);

        assertThat(service.existsById("available_id")).isFalse();
    }
}
