package com.example.formproject.exception;

import com.example.formproject.dto.response.ExceptionDto;
import com.example.formproject.exception.EmailConfirmException;
import com.example.formproject.exception.WrongArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHander {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> unExpectedException(Exception e){
        log.error("UnCatchedError",e);
        return new ResponseEntity<>(new ExceptionDto("예상하지 못한 오류가 발생했습니다.",e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(WrongArgumentException.class)
    public ResponseEntity<ExceptionDto> argumentException(WrongArgumentException e){
        return new ResponseEntity<>(new ExceptionDto(e.getMessage(),e.getField()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ParseException.class)
    public ResponseEntity<ExceptionDto> apiParseException(ParseException e){
        return new ResponseEntity<>(new ExceptionDto("정보를 받아오는데 실패했습니다.",e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(EmailConfirmException.class)
    public ResponseEntity<ExceptionDto> handlingAuthExp(EmailConfirmException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ExceptionDto(e.getMessage(),e.getField()));
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionDto> handlingAuthExp(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ExceptionDto(e.getMessage(),e.getField()));
    }
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionDto> validException(MethodArgumentNotValidException ex) {
        ExceptionDto dto = new ExceptionDto("잘못된 입력입니다.", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST); // 2
    }
}
