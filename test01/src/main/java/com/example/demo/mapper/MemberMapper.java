package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.domain.MemberDto;

@Mapper
public interface MemberMapper {
    MemberDto findById(String id);
    
    int insertMember(MemberDto member);

    int deleteMember(String id);
    
    int updateMember(MemberDto Dto);

}
