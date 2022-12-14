package com.example.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.AlphaVantagePriceService;
import com.example.domain.StockResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class StockPriceController {

  private final AlphaVantagePriceService alphaVantagePriceService;

  /**
   *
   * @param symbol
   * @param allParams
   * @return
   */
  @GetMapping("/stock/{symbol}")
  public Mono<StockResponse> getPositionAndMarketValue(
    @PathVariable String symbol,
    @RequestParam Map<String, String> allParams
  ) {
    return alphaVantagePriceService.get(symbol, allParams);
  }
}
