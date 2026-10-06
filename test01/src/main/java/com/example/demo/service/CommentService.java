package com.example.demo.service;

import java.util.List;

import com.example.demo.domain.CommentDto;
import com.example.demo.mapper.Criteria;

public interface CommentService {
    // id must come from the authenticated principal, never from a request body.
    void register(String id, CommentDto comment);

    CommentDto selectOne(long ccode);

    List<CommentDto> selectList(long bcode, Criteria criteria);

    long countByBoard(long bcode);

    // id must come from the authenticated principal, never from a request body.
    void updateComment(String id, CommentDto comment);

    void deleteComment(String id, long ccode);
}
