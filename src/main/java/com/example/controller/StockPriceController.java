package com.example.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.AlphaVantageTimeSeriesDailyJson;
import com.example.service.AlphaVantagePriceService;
import com.example.service.StockResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class StockPriceController {

  private final AlphaVantagePriceService alphaVantagePriceService;

  @GetMapping("/stock/{symbol}")
  public Mono<StockResponse> getPositionAndMarketValue(
    @PathVariable String symbol,
    @RequestParam Map<String, String> allParams
  ) {
    return alphaVantagePriceService.get(symbol, allParams);
  }
}
