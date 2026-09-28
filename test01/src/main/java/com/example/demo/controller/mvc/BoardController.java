package com.example.demo.controller.mvc;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.domain.BoardDto;
import com.example.demo.exception.BoardNotFoundException;
import com.example.demo.mapper.Criteria;
import com.example.demo.mapper.MemberMapper;
import com.example.demo.mapper.PageDto;
import com.example.demo.service.BoardService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final MemberMapper memberMapper;

    @GetMapping({"", "/", "/list"})
    public String list(Criteria cri, Model model) {
        PageDto page = new PageDto(cri, boardService.total());

        model.addAttribute("page", page);
        model.addAttribute("list", boardService.findAll(cri));

        return "board/list";
    }

    @GetMapping("/get")
    public String getlist(@RequestParam long bcode, @AuthenticationPrincipal UserDetails user, Model model) {
        BoardDto board = boardService.selectOne(bcode);
        model.addAttribute("BoardDto", board);
        model.addAttribute("canEdit", owns(user, board));
        return "board/Get";
    }

    @GetMapping("/register")
    public String registerForm(@AuthenticationPrincipal UserDetails user, Model model) {
        if (user == null) return "redirect:/member/login";
        model.addAttribute("BoardDto", new BoardDto());
        model.addAttribute("editing", false);
        return "board/form";
    }

    @PostMapping("/register")
    public String register(@AuthenticationPrincipal UserDetails user, BoardDto board, Model model, RedirectAttributes redirect) {
        if (user == null) return "redirect:/member/login";
        if (!valid(board)) return invalidForm(board, false, model);
        boardService.register(user.getUsername(), board);
        redirect.addFlashAttribute("message", "새로운 이야기가 등록되었습니다.");
        return "redirect:/board/list";
    }

    @GetMapping("/modify")
    public String modifyForm(@AuthenticationPrincipal UserDetails user, @RequestParam long bcode, Model model) {
        if (user == null) return "redirect:/member/login";
        BoardDto board = boardService.selectOne(bcode);
        if (!owns(user, board)) throw new BoardNotFoundException();
        model.addAttribute("BoardDto", board);
        model.addAttribute("editing", true);
        return "board/form";
    }

    @PostMapping("/modify")
    public String modify(@AuthenticationPrincipal UserDetails user, BoardDto board, Model model, RedirectAttributes redirect) {
        if (user == null) return "redirect:/member/login";
        if (!owns(user, boardService.selectOne(board.getBcode()))) throw new BoardNotFoundException();
        if (!valid(board)) return invalidForm(board, true, model);
        boardService.updateBoard(user.getUsername(), board);
        redirect.addFlashAttribute("message", "게시글이 수정되었습니다.");
        return "redirect:/board/get?bcode=" + board.getBcode();
    }

    @PostMapping("/remove")
    public String remove(@AuthenticationPrincipal UserDetails user, @RequestParam long bcode, RedirectAttributes redirect) {
        if (user == null) return "redirect:/member/login";
        boardService.deleteBoard(user.getUsername(), bcode);
        redirect.addFlashAttribute("message", "게시글이 삭제되었습니다.");
        return "redirect:/board/list";
    }

    @ExceptionHandler(BoardNotFoundException.class)
    public String missing(RedirectAttributes redirect) {
        redirect.addFlashAttribute("message", "게시글이 없거나 접근 권한이 없습니다.");
        return "redirect:/board/list";
    }

    private boolean owns(UserDetails user, BoardDto board) {
        return user != null && memberMapper.getById(user.getUsername()) == board.getIdNumber();
    }

    private boolean valid(BoardDto board) {
        return board.getTitle() != null && !board.getTitle().isBlank() && board.getTitle().length() <= 200
                && board.getContent() != null && !board.getContent().isBlank();
    }

    private String invalidForm(BoardDto board, boolean editing, Model model) {
        model.addAttribute("BoardDto", board);
        model.addAttribute("editing", editing);
        model.addAttribute("error", "제목은 1~200자, 내용은 공백이 아닌 글로 입력해 주세요.");
        return "board/form";
    }
}
