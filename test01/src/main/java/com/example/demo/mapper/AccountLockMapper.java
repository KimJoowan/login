package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.domain.AccountLockDto;

@Mapper
public interface AccountLockMapper {
	void increaseLoginFailCount(AccountLockDto dto);
	
	int findById(int num);
}
