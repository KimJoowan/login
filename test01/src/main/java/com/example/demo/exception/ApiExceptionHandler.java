package com.example.demo.exception;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>>
            handleDataIntegrityViolation(
                    DataIntegrityViolationException exception) {

        log.warn("데이터 무결성 위반", exception);

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(Map.of(
                "status", HttpStatus.CONFLICT.value(),
                "message", "이미 존재하거나 처리할 수 없는 데이터입니다."
            ));
    }
}