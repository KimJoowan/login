package com.example.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.BoardDto;
import com.example.demo.mapper.Criteria;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Log4j2
class BoardServiceTest {

    @Autowired
    private BoardService service;

    @Test
    void register() {
        String id = "bbbbbbbb";

        BoardDto board = new BoardDto();
        board.setTitle("setTitle");
        board.setContent("test");

        service.register(id, board);
    }

    @Test
    void findAll() {
        Criteria criteria = new Criteria();

        service.findAll(criteria)
                .forEach(board -> log.info("게시글: {}", board));
    }

    @Test
    void selectOne() {
        register();
        String id = "bbbbbbbb";
        long bcode = service.selectList(id).get(0).getBcode();

        log.info("게시글: {}", service.selectOne(bcode));
    }

    @Test
    void selectList() {
        String id = "bbbbbbbb";

        service.selectList(id)
                .forEach(board -> log.info("작성한 게시글: {}", board));
    }

    @Test
    void updateBoard() {
        register();
        String id = "bbbbbbbb";
        BoardDto board = service.selectList(id).get(0);
        board.setTitle("수정 제목");
        board.setContent("수정 내용");

        service.updateBoard(id, board);

        log.info("수정된 게시글: {}", service.selectOne(board.getBcode()));
    }

    @Test
    void deleteBoard() {
        register();
        String id = "bbbbbbbb";
        long bcode = service.selectList(id).get(0).getBcode();

        service.deleteBoard(id, bcode);

        log.info("삭제한 게시글 번호: {}", bcode);
    }

    @Test
    void total() {
        log.info("전체 게시글 개수: {}", service.total());
    }
}
