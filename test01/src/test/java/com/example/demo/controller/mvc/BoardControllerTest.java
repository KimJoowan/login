package com.example.demo.controller.mvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import com.example.demo.domain.BoardDto;
import com.example.demo.exception.BoardNotFoundException;
import com.example.demo.mapper.Criteria;
import com.example.demo.mapper.MemberMapper;
import com.example.demo.mapper.PageDto;
import com.example.demo.service.BoardService;

class BoardControllerTest {
    private BoardService service;
    private MemberMapper members;
    private BoardController controller;
    private ExtendedModelMap model;
    private RedirectAttributesModelMap redirect;
    private final UserDetails user = User.withUsername("writer").password("unused").roles("USER").build();

    @BeforeEach
    void setup() {
        service = mock(BoardService.class);
        members = mock(MemberMapper.class);
        controller = new BoardController(service, members);
        model = new ExtendedModelMap();
        redirect = new RedirectAttributesModelMap();
    }

    @Test
    void firstPageDoesNotJumpToEndOfBlock() {
        when(service.total()).thenReturn(120L);
        Criteria criteria = new Criteria();
        assertEquals("board/list", controller.list(criteria, model));
        assertEquals(1, criteria.getPageNum());
        assertTrue(((PageDto) model.get("page")).isNext());
        verify(service, times(1)).findAll(criteria);
    }

    @Test
    void outOfRangePageAndAmountAreClamped() {
        when(service.total()).thenReturn(12L);
        Criteria criteria = new Criteria();
        criteria.setPageNum(Long.MAX_VALUE);
        criteria.setAmount(0);
        controller.list(criteria, model);
        assertEquals(1, criteria.getAmount());
        assertEquals(12, criteria.getPageNum());
        assertEquals(11, criteria.getPageStart());
    }

    @Test
    void emptyListKeepsFirstPage() {
        Criteria criteria = new Criteria(-5, 10);
        controller.list(criteria, model);
        assertEquals(1, criteria.getPageNum());
        assertEquals(0, ((PageDto) model.get("page")).getTotal());
    }

    @Test
    void invalidPostRetainsInputWithoutSaving() {
        BoardDto board = new BoardDto();
        board.setTitle(" ");
        board.setContent("작성 중인 내용");
        assertEquals("board/form", controller.register(user, board, model, redirect));
        assertSame(board, model.get("BoardDto"));
        assertNotNull(model.get("error"));
        verifyNoInteractions(service);
    }

    @Test
    void validPostUsesAuthenticatedWriter() {
        BoardDto board = new BoardDto();
        board.setTitle("새 이야기");
        board.setContent("안녕하세요.");
        assertEquals("redirect:/board/list", controller.register(user, board, model, redirect));
        verify(service).register("writer", board);
        assertFalse(redirect.getFlashAttributes().isEmpty());
    }

    @Test
    void otherMemberCannotOpenOrSubmitEdit() {
        BoardDto board = new BoardDto();
        board.setBcode(7);
        board.setIdNumber(10);
        when(service.selectOne(7)).thenReturn(board);
        when(members.getById("writer")).thenReturn(20);
        assertThrows(BoardNotFoundException.class, () -> controller.modifyForm(user, 7, model));
        assertThrows(BoardNotFoundException.class, () -> controller.modify(user, board, model, redirect));
        verify(service, never()).updateBoard(anyString(), any());
        controller.getlist(7, user, model);
        assertEquals(false, model.get("canEdit"));
    }

    @Test
    void anonymousMutationRedirectsToLogin() {
        assertEquals("redirect:/member/login", controller.register(null, new BoardDto(), model, redirect));
        assertEquals("redirect:/member/login", controller.modify(null, new BoardDto(), model, redirect));
        assertEquals("redirect:/member/login", controller.remove(null, 7, redirect));
        verifyNoInteractions(service);
    }

    @Test
    void deletionUsesPostOnlyAndAuthenticatedWriter() throws Exception {
        var method = BoardController.class.getMethod("remove", UserDetails.class, long.class,
                org.springframework.web.servlet.mvc.support.RedirectAttributes.class);
        assertNotNull(method.getAnnotation(org.springframework.web.bind.annotation.PostMapping.class));
        assertNull(method.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class));
        assertEquals("redirect:/board/list", controller.remove(user, 7, redirect));
        verify(service).deleteBoard("writer", 7);
    }
}
