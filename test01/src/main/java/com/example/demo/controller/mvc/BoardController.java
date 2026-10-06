package com.example.demo.controller.mvc;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.domain.BoardDto;
import com.example.demo.exception.BoardNotFoundException;
import com.example.demo.mapper.Criteria;
import com.example.demo.mapper.MemberMapper;
import com.example.demo.mapper.PageDto;
import com.example.demo.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Log4j2
public class BoardController {
	private final BoardService boardService;
	private final MemberMapper memberMapper;

	// 게시글 목록
	@GetMapping({ "", "/", "/list" })
	public String list(Criteria criteria, Model model) {
		// PageDto에서 조회 범위를 보정한 뒤 같은 조건으로 게시글을 조회한다.
		long total = criteria.getKeyword().isEmpty() ? boardService.total() : boardService.total(criteria);
		PageDto page = new PageDto(criteria, total);

		model.addAttribute("page", page);
		model.addAttribute("list", boardService.findAll(criteria));

		return "board/list";
	}

	// 게시글 상세
	@GetMapping("/get")
	public String detail(@RequestParam long bcode, @AuthenticationPrincipal UserDetails user, Model model) {

		BoardDto board = boardService.selectOne(bcode);

		model.addAttribute("BoardDto", board);
		long viewerNumber = user == null ? 0 : memberMapper.getById(user.getUsername());
		model.addAttribute("canEdit", user != null && viewerNumber == board.getIdNumber());
		model.addAttribute("commentViewerNumber", viewerNumber);

		return "board/Get";
	}

	// 글쓰기 화면
	@GetMapping("/register")
	public String registerForm(@AuthenticationPrincipal UserDetails user, Model model) {
		if (user == null) {
			return "redirect:/member/login";
		}

		model.addAttribute("BoardDto", new BoardDto());
		model.addAttribute("editing", false);

		return "board/form";
	}

	// 게시글 등록
	@PostMapping("/register")
	public String register(@AuthenticationPrincipal UserDetails user, BoardDto board, Model model,
			RedirectAttributes redirectAttributes) {

		if (user == null) {
			return "redirect:/member/login";
		}

		if (!isValidBoard(board)) {
			return showValidationError(board, false, model);
		}

		boardService.register(user.getUsername(), board);
		redirectAttributes.addFlashAttribute("message", "새로운 이야기가 등록되었습니다.");

		return "redirect:/board/list";
	}

	// 글 수정 화면
	@GetMapping("/modify")
	public String modifyForm(@AuthenticationPrincipal UserDetails user, @RequestParam long bcode, Model model) {

		if (user == null) {
			return "redirect:/member/login";
		}

		BoardDto board = boardService.selectOne(bcode);
		if (!isAuthor(user, board)) {
			throw new BoardNotFoundException();
		}

		model.addAttribute("BoardDto", board);
		model.addAttribute("editing", true);

		return "board/form";
	}

	// 게시글 수정
	@PostMapping("/modify")
	public String modify(@AuthenticationPrincipal UserDetails user, BoardDto board, Model model,
			RedirectAttributes redirectAttributes) {

		if (user == null) {
			return "redirect:/member/login";
		}

		if (!isValidBoard(board)) {
			return showValidationError(board, true, model);
		}
		
		boardService.updateBoard(user.getUsername(), board);
		redirectAttributes.addFlashAttribute("message", "게시글이 수정되었습니다.");

		return "redirect:/board/get?bcode=" + board.getBcode();
	}

	// 게시글 삭제: 서비스에서 작성자를 확인한다.
	@PostMapping("/remove")
	public String remove(@AuthenticationPrincipal UserDetails user, @RequestParam long bcode, RedirectAttributes redirectAttributes) {

		if (user == null) {
			return "redirect:/member/login";
		}

		boardService.deleteBoard(user.getUsername() , bcode);
		redirectAttributes.addFlashAttribute("message", "게시글이 삭제되었습니다.");

		return "redirect:/board/list";
	}

	@ExceptionHandler(BoardNotFoundException.class)
	public String handleBoardNotFound(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("message", "게시글이 없거나 접근 권한이 없습니다.");

		return "redirect:/board/list";
	}

	private boolean isAuthor(UserDetails user, BoardDto board) {
		if (user == null) {
			return false;
		}

		long memberId = memberMapper.getById(user.getUsername());
		return memberId == board.getIdNumber();
	}

	private boolean isValidBoard(BoardDto board) {
		String title = board.getTitle();
		String content = board.getContent();

		if (title == null || title.isBlank() || title.length() > 200) {
			return false;
		}
		
		if (content == null || content.isBlank() || content.length() > 10_000) {
			return false;
		}

		return content != null && !content.isBlank();
	}

	private String showValidationError(BoardDto board, boolean editing, Model model) {
		model.addAttribute("BoardDto", board);
		model.addAttribute("editing", editing);
		model.addAttribute("error", "제목은 1~200자, 내용은 1~10,000자로 입력해 주세요. 공백만 입력할 수 없습니다.");

		return "board/form";
	}
}
