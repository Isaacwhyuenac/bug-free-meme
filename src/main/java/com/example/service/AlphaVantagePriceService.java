package com.example.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.domain.AlphaVantageTimeSeriesDailyJson;
import com.example.domain.StockResponse;
import com.example.exception.InvalidDateInputException;
import com.example.utils.Constants;
import com.example.utils.DayUtils;

import ch.obermuhlner.math.big.BigDecimalMath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlphaVantagePriceService {

  @Value("${alphavantage.api.key}")
  private String apiKey;

  @Value("${alphavantage.api.url}")
  private String url;

  private final WebClient webClient;

  public Mono<StockResponse> get(String symbol, Map<String, String> allParams) {

    log.info(symbol);
    log.info(url);

    return webClient.get().uri(url,
        uriBuilder -> uriBuilder
          .queryParam("function", "TIME_SERIES_DAILY_ADJUSTED")
          .queryParam("symbol", symbol)
          .queryParam("apikey", apiKey)
          .queryParam("outputsize", "full")
          .build()
      )
      .retrieve()
      .onStatus(HttpStatus::isError, response -> {
        if (response.statusCode().is5xxServerError()) {
          log.error("error status is 5xx {}", response.bodyToMono(String.class));
          return Mono.error(new Exception("alpha vantage api returns 5xx"));
        }
        log.error("error status is {} response body = {}", response.statusCode(), response.bodyToMono(String.class));

        return Mono.error(new Exception("Error Occurred"));
      })
      .bodyToMono(AlphaVantageTimeSeriesDailyJson.class)
      .handle(new BiConsumer<AlphaVantageTimeSeriesDailyJson, SynchronousSink<StockResponse>>() {
        @Override
        public void accept(AlphaVantageTimeSeriesDailyJson alphaVantageTimeSeriesDailyJson, SynchronousSink<StockResponse> sink) {
          try {
            sink.next(getLatestClosingPrice(alphaVantageTimeSeriesDailyJson, allParams));
          } catch (InvalidDateInputException e) {
            log.error(e.getMessage(), e);

            sink.error(e);
          }
        }
      });
  }

  // package private
  StockResponse getLatestClosingPrice(AlphaVantageTimeSeriesDailyJson json, Map<String, String> allParams) throws InvalidDateInputException {
    String from = allParams.get(Constants.FROM_QUERY_PARAM);
    String to = allParams.get(Constants.TO_QUERY_PARAM);
    log.info("{} {} ", Constants.FROM_QUERY_PARAM, from);
    log.info("{} {}", Constants.TO_QUERY_PARAM, to);

    List<LocalDate> dates = json.getDaily().keySet().stream().map(LocalDate::parse).collect(Collectors.toList());

    LocalDate fromDate = StringUtils.hasText(from) ? LocalDate.parse(from) : dates.stream().min(Comparator.comparing(LocalDate::toEpochDay)).get();
    LocalDate toDate = StringUtils.hasText(to) ? LocalDate.parse(to) : dates.stream().max(Comparator.comparing(LocalDate::toEpochDay)).get();

    if (fromDate.isAfter(toDate)) {
      throw new InvalidDateInputException("from cannot be after to");
    }

    long days = ChronoUnit.DAYS.between(fromDate, toDate);

    BigDecimal period = BigDecimal.valueOf(365).divide(BigDecimal.valueOf(days), MathContext.DECIMAL128);

    LocalDate minLocalDate = DayUtils.shiftBeforeDate(fromDate);
    LocalDate maxLocalDate = DayUtils.shiftBeforeDate(toDate);

    String startClosingPrice = json.getDaily().get(minLocalDate.toString()).getClosingPrice();
    String endClosingPrice = json.getDaily().get(maxLocalDate.toString()).getClosingPrice();

    BigDecimal startClosingPriceBD = new BigDecimal(startClosingPrice);
    BigDecimal endClosingPriceBD = new BigDecimal(endClosingPrice);

    BigDecimal returns = endClosingPriceBD.subtract(startClosingPriceBD).divide(startClosingPriceBD, MathContext.DECIMAL128);

    BigDecimal annualizedRateOfReturn = BigDecimalMath.pow(BigDecimal.ONE.add(returns), period, MathContext.DECIMAL128).subtract(BigDecimal.ONE);

    return new StockResponse(annualizedRateOfReturn, fromDate, toDate, days);
  }


}
