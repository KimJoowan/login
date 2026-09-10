package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.domain.MemberDto;
import com.example.demo.domain.MemberUpdateRequest;

@Mapper
public interface MemberMapper {
	MemberDto findById(String id);

	int insertMember(MemberDto member);

	int existsById(String id);

	int withdrawMember(String id);

	int updateMember(@Param("id") String id, @Param("request") MemberUpdateRequest request);

}
