package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountLockMapper {
	void increaseLoginFailCount(int number);
	
	boolean findById(int num);
	
	int recordSuccess(int num);
}
