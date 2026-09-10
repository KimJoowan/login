package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountLockMapper {
	int insertAccountLock(long number);

	void increaseLoginFailCountById(String id);
	
	Boolean findById(long num);
	
	int recordSuccess(String id);
	
	Boolean isLoginAllowed(long number);
}
