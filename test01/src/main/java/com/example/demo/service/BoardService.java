package com.example.demo.service;

import java.util.List;

import com.example.demo.domain.BoardDto;
import com.example.demo.mapper.Criteria;

public interface BoardService {
    // id must come from the authenticated principal, never from a request body.
    public void register(String id, BoardDto board);

    List<BoardDto> findAll(Criteria criteria);

    BoardDto selectOne(long bcode);

    List<BoardDto> selectList(String id);

    // id must come from the authenticated principal, never from a request body.
    void updateBoard(String id, BoardDto board);

    void deleteBoard(String id, long bcode);

    long total();

    long total(Criteria criteria);
}
