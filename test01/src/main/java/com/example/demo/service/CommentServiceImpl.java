package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.CommentDto;
import com.example.demo.exception.BoardNotFoundException;
import com.example.demo.exception.CommentNotFoundException;
import com.example.demo.mapper.BoardMapper;
import com.example.demo.mapper.CommentMapper;
import com.example.demo.mapper.Criteria;
import com.example.demo.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
	
    private final CommentMapper commentMapper;
    private final MemberMapper memberMapper;
    private final BoardMapper boardMapper;

    @Override
    @Transactional
    public void register(String id, CommentDto comment) {
        if (boardMapper.selectOne(comment.getBcode()) == null) {
            throw new BoardNotFoundException();
        }

        comment.setIdNumber(memberMapper.getById(id));
        
        if (commentMapper.insertComment(comment) != 1) {
            throw new IllegalStateException("댓글 등록에 실패했습니다.");
        }
    }

    @Override
    public CommentDto selectOne(long ccode) {
        CommentDto comment = commentMapper.selectOne(ccode);
        
        if (comment == null) {
            throw new CommentNotFoundException();
        }
        return comment;
    }

    @Override
    public List<CommentDto> selectList(long bcode, Criteria criteria) {
        return commentMapper.selectList(bcode, criteria);
    }

    @Override
    public long countByBoard(long bcode) {
        return commentMapper.countByBoard(bcode);
    }

    @Override
    @Transactional
    public void updateComment(String id, CommentDto comment) {   
        comment.setIdNumber(memberMapper.getById(id));
        
        if (commentMapper.updateComment(comment) != 1) {
            throw new CommentNotFoundException();
        }
    }

    @Override
    @Transactional
    public void deleteComment(String id, long ccode) {
        int idNumber = memberMapper.getById(id);
        
        if (commentMapper.deleteComment(ccode, idNumber) != 1) {
            throw new CommentNotFoundException();
        }
    }
}
