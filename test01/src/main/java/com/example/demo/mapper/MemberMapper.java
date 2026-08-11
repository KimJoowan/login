package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.domain.MemberDto;

@Mapper
public interface MemberMapper {
    List<MemberDto> selectAll();
    
    MemberDto findById(String id);
    
    int insertMember(MemberDto member);

    int deleteMember(String id);
    
    void updateMember(MemberDto Dto);

	void updatePassword(String id, String password);
}
