package com.example.demo.controller.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.domain.CommentCreateRequest;
import com.example.demo.domain.CommentDto;
import com.example.demo.domain.CommentUpdateRequest;
import com.example.demo.exception.BoardNotFoundException;
import com.example.demo.exception.CommentNotFoundException;
import com.example.demo.mapper.Criteria;
import com.example.demo.mapper.PageDto;
import com.example.demo.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentApiController {
    private final CommentService commentService;

    @GetMapping
    public CommentListResponse list(@RequestParam long bcode, Criteria criteria) {
        requirePositive(bcode);
        
        long total = commentService.countByBoard(bcode);
        
        new PageDto(criteria, total);
        
        return new CommentListResponse(commentService.selectList(bcode, criteria), total,
                criteria.getPageNum(), criteria.getAmount());
    }

    @GetMapping("/{ccode}")
    public CommentDto detail(@PathVariable long ccode) {
        requirePositive(ccode);
        return commentService.selectOne(ccode);
    }

    @PostMapping
    public ResponseEntity<CommentCreatedResponse> register(@AuthenticationPrincipal UserDetails user, @Valid @RequestBody CommentCreateRequest request) {
        String id = authenticatedId(user);
        
        CommentDto comment = new CommentDto();    
        	comment.setBcode(request.bcode());
        	comment.setContent(request.content());
        
        commentService.register(id, comment);
        
        return ResponseEntity.created(URI.create("/api/comments/" + comment.getCcode()))
                .body(new CommentCreatedResponse(comment.getCcode()));
    }

    @PutMapping("/{ccode}")
    public ResponseEntity<Void> modify(@AuthenticationPrincipal UserDetails user, @PathVariable long ccode, @Valid @RequestBody CommentUpdateRequest request) {
        String id = authenticatedId(user);
        requirePositive(ccode);
        
        CommentDto comment = new CommentDto();
        	comment.setCcode(ccode);
        	comment.setContent(request.content());
        
        commentService.updateComment(id, comment);
        
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{ccode}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal UserDetails user, @PathVariable long ccode) {
        String id = authenticatedId(user);

        requirePositive(ccode);
        
        commentService.deleteComment(id, ccode);
        
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({BoardNotFoundException.class, CommentNotFoundException.class})
    public ProblemDetail handleNotFound(RuntimeException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("대상을 찾을 수 없습니다.");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "게시글 번호와 댓글 내용을 확인해 주세요.");
        problem.setTitle("입력값 오류");
        problem.setProperty("errors", exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage()).toList());
        return problem;
    }

    private String authenticatedId(UserDetails user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return user.getUsername();
    }

    private void requirePositive(long code) {
        if (code <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "번호는 양수여야 합니다.");
        }
    }

    public record CommentListResponse(List<CommentDto> comments, long total, long pageNum, long amount) {}

    public record CommentCreatedResponse(long ccode) {}
}
