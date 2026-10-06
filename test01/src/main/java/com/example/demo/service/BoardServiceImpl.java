package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.BoardDto;
import com.example.demo.exception.BoardNotFoundException;
import com.example.demo.mapper.BoardMapper;
import com.example.demo.mapper.Criteria;
import com.example.demo.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {
    private final BoardMapper boardMapper;
    private final MemberMapper memberMapper;

    @Override
    @Transactional
    public void register(String id, BoardDto board) {
        int num = memberMapper.getById(id);
        board.setIdNumber(num);
        
        if (boardMapper.insertBoard(board) != 1) {
            throw new IllegalStateException("게시글 등록에 실패했습니다.");
        }
    }

    @Override
    public List<BoardDto> findAll(Criteria criteria) {
        return boardMapper.findAll(criteria);
    }

    @Override
    public BoardDto selectOne(long bcode) {
        BoardDto board = boardMapper.selectOne(bcode);
        
        if (board == null) {
            throw new BoardNotFoundException();
        }
        
        return board;
    }

    @Override
    public List<BoardDto> selectList(String id) {
        int num = memberMapper.getById(id);
        return boardMapper.selectList(num);
    }

    @Override
    @Transactional
    public void updateBoard(String id, BoardDto board) {
        // Ignore the author supplied in the request; enforce ownership in the UPDATE.
        int num = memberMapper.getById(id);
        board.setIdNumber(num);
        if (boardMapper.updateBoard(board) != 1) {
            throw new BoardNotFoundException();
        }
    }

    @Override
    @Transactional
    public void deleteBoard(String id, long bcode) {
        int num = memberMapper.getById(id);

        if (boardMapper.deleteBoard(bcode, num) != 1) {
            throw new BoardNotFoundException();
        }
    }

    @Override
    public long total() {
        return boardMapper.total();
    }

    @Override
    public long total(Criteria criteria) {
        return boardMapper.countMatching(criteria);
    }

}
