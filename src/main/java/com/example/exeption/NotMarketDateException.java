package com.example.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NotMarketDateException extends Exception {

  public NotMarketDateException(String s) {
    super(s);
  }
}
