package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import com.example.exeption.NotMarketDateException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler  {

  @ExceptionHandler(value = {NotMarketDateException.class})
  public ResponseEntity handleException(NotMarketDateException ex, WebRequest request) {
    log.info("==========handler==============");
    log.info(ex.getMessage());
    han
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

}
