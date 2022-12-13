package com.example.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.domain.ApiError;
import com.example.exception.InvalidDateInputException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

  @ExceptionHandler(value = {InvalidDateInputException.class})
  public ResponseEntity<ApiError> handleException(Exception ex) {
    return ResponseEntity.badRequest()
      .body(new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
  }

  @ExceptionHandler(value={Exception.class, NullPointerException.class})
  public ResponseEntity<ApiError> showError(Exception ex){
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
  }

//  @ExceptionHandler(value = NullPointerException.class)
//  public ResponseEntity<ApiError> handle

//  @ExceptionHandler(value = HttpClientErrorException..css)
//  public ResponseEntity<ApiError> handlTimeoutException(Exception ex) {
////    HttpStatus.BAD_GATEWAY.class
//    return ResponseEntity.badRequest().body(new ApiError(HttpStatus.BAD_REQUEST.value(), "Alpha Vantage not responding"))
//  }

}
