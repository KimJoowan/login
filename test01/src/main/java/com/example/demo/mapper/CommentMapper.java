package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.domain.CommentDto;

@Mapper
public interface CommentMapper {
    int insertComment(CommentDto comment);

    CommentDto selectOne(@Param("ccode") long ccode);

    List<CommentDto> selectList(@Param("bcode") long bcode, @Param("cri") Criteria cri);

    long countByBoard(@Param("bcode") long bcode);

    int updateComment(CommentDto comment);

    int deleteComment(@Param("ccode") long ccode, @Param("idNumber") long idNumber);
}
