package com.example.demo.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.domain.BoardDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@SpringBootTest
@RequiredArgsConstructor
@ActiveProfiles("test")
//@Transactional
@Log4j2
class BoardMapperTest {

    @Autowired
    private BoardMapper boardMapper;
    
    @Autowired
    private MemberMapper memberMapper;
    
    @Test
    void test() {	
    	for(int i=0; i<10; i++) {
    		insertBoard();
    	}
    }
    
 
    @Test
    void insertBoard() {
        BoardDto board = new BoardDto();
        
        String id = "bbbbbbbb";    
        board.setIdNumber(memberMapper.getById(id)); 
        
        board.setTitle("setTitle");
        board.setContent("test");

        boardMapper.insertBoard(board);

        selectOne();
    }
    
    @Test
    void findAll() {
        Criteria cri = new Criteria();
        log.info("조회 조건: {}", cri);

        boardMapper.findAll(cri)
            .forEach(board -> log.info("게시글: {}", board));
    }
  
    @Test
    void selectList() {
    	BoardDto board = new BoardDto();
    	
    	String id = "bbbbbbbb";    
        board.setIdNumber(memberMapper.getById(id));
        
        long num = board.getIdNumber();
        
    	log.info("등록된 게시글: {}", boardMapper.selectList(num));
    }
    
    @Test
    void selectOne() {    	
    	String id = "bbbbbbbb"; 
    	long idNum = memberMapper.getById(id);
    	
        long bcode = 42;            
    	log.info("등록된 게시글: {}", boardMapper.selectOne(bcode, idNum));
    }
    
    @Test
    void count() {
    	log.info("등록된 게시글 개수: {}", boardMapper.total());
    }
    
    @Test
    void update() {
    	BoardDto board = new BoardDto();
    	board.setTitle("test01");
    	board.setContent("setContent");
    	board.setBcode(42);
    	
    	String id = "bbbbbbbb"; 
    	board.setIdNumber(memberMapper.getById(id));
    	log.info("성공여부: {}", board);
    	
    	int num = boardMapper.updateBoard(board);
    	
    	log.info("성공여부: {}", num);
    	selectOne();
    	
    }
    
    @Test
    void deleteBoard() {
    	String id = "bbbbbbbb"; 
    	long idNum = memberMapper.getById(id);
    	long bcode = 42;
    	
    	log.info("성공여부: {}", boardMapper.deleteBoard(bcode, idNum));
    	selectOne();
    	
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
