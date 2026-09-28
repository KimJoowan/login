package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.domain.BoardDto;

@Mapper
public interface BoardMapper {
	
	public int insertBoard(BoardDto board);
	
	public List<BoardDto> findAll(Criteria cri);

    public BoardDto selectOne(long bcode);

    // Retained for existing callers; detail reads are not restricted to the author.
    default BoardDto selectOne(long bcode, long idNumber) {
        return selectOne(bcode);
    }
    
    public List<BoardDto> selectList(long idNumber);
    
    public int updateBoard(BoardDto board);

    public int deleteBoard(@Param("bcode") long bcode, @Param("idNumber") long idNumber);
    
    public long total();
}
