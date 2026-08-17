package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountLockMapper {
	void increaseLoginFailCountById(String id);
	
	boolean findById(int num);
	
	int recordSuccess(String id);
	
	boolean isLoginAllowed(int number);
}
