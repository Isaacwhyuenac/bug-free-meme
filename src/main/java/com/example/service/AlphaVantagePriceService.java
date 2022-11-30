package com.example.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.domain.AlphaVantageTimeSeriesDailyJson;
import com.example.domain.StockResponse;
import com.example.exeption.NotMarketDateException;
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

  private final WebClient webClient;

  public Mono<StockResponse> get(String symbol, Map<String, String> allParams) {
    log.info(symbol);

    return webClient.get().uri("https://www.alphavantage.co/query",
        uriBuilder -> uriBuilder
          .queryParam("function", "TIME_SERIES_DAILY_ADJUSTED")
          .queryParam("symbol", symbol)
          .queryParam("apikey", apiKey)
          .queryParam("outputsize", "full")
          .build()
      )
      .retrieve()
      .bodyToMono(AlphaVantageTimeSeriesDailyJson.class)
      .handle(new BiConsumer<AlphaVantageTimeSeriesDailyJson, SynchronousSink<StockResponse>>() {
        @Override
        public void accept(AlphaVantageTimeSeriesDailyJson alphaVantageTimeSeriesDailyJson, SynchronousSink<StockResponse> sink) {
          try {
            sink.next(getLatestClosingPrice(alphaVantageTimeSeriesDailyJson, allParams));
          } catch (NotMarketDateException e) {
            System.out.println("======services=========");
            sink.error(e);
          }
        }
      });
  }

  StockResponse getLatestClosingPrice(AlphaVantageTimeSeriesDailyJson json, Map<String, String> allParams) throws NotMarketDateException {
    String from = allParams.get("from");
    String to = allParams.get("to");

    log.info("from " + from);
    log.info("to " + to);

    List<LocalDate> dates = json.getDaily().keySet().stream().map(LocalDate::parse).collect(Collectors.toList());

    LocalDate fromDate = StringUtils.hasText(from) ? LocalDate.parse(from) : dates.stream().min(Comparator.comparing(LocalDate::toEpochDay)).get();
    LocalDate toDate = StringUtils.hasText(to) ? LocalDate.parse(to) : dates.stream().max(Comparator.comparing(LocalDate::toEpochDay)).get();

    long days = ChronoUnit.DAYS.between(fromDate, toDate);

    BigDecimal period = BigDecimal.valueOf(365).divide(BigDecimal.valueOf(days), MathContext.DECIMAL128);

    LocalDate minLocalDate = DayUtils.shiftDate(fromDate);
    LocalDate maxLocalDate = DayUtils.shiftDate(toDate);

//    // Check if the response from the market API contains our day
//    if (json.getDaily().get(minLocalDate.toString()) == null) {
//      throw new NotMarketDateException(minLocalDate + " is not a market date");
//    }
//    // Check if the response from the market API contains our day
//    if (json.getDaily().get(maxLocalDate.toString()) == null) {
//      throw new NotMarketDateException(maxLocalDate + " is not a market date");
//    }

    String startClosingPrice = json.getDaily().get(minLocalDate.toString()).getClosingPrice();
    String endClosingPrice = json.getDaily().get(maxLocalDate.toString()).getClosingPrice();

    BigDecimal startClosingPriceBD = new BigDecimal(startClosingPrice);
    BigDecimal endClosingPriceBD = new BigDecimal(endClosingPrice);

    BigDecimal returns = endClosingPriceBD.subtract(startClosingPriceBD).divide(startClosingPriceBD, MathContext.DECIMAL128);

    BigDecimal annualizedRateOfReturn = BigDecimalMath.pow(BigDecimal.ONE.add(returns), period, MathContext.DECIMAL128).subtract(BigDecimal.ONE);

    return new StockResponse(annualizedRateOfReturn, fromDate, toDate, days);
  }


}
