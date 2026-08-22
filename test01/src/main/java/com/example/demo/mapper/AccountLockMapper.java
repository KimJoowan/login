package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountLockMapper {
	void increaseLoginFailCountById(String id);
	
	Boolean findById(int num);
	
	int resetIfExpired(String id);
	
	int recordSuccess(String id);
	
	Boolean isLoginAllowed(int number);
}
