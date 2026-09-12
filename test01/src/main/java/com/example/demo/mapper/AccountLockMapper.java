package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountLockMapper {
	void increaseLoginFailCountById(String id);
	
	int recordSuccess(String id);
	
	Boolean isLoginAllowed(long number);

	void resetIfExpired(String id);
}
