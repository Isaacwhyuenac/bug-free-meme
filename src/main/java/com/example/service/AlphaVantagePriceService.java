package com.example.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.domain.AlphaVantageTimeSeriesDailyJson;
import com.example.exeption.NotMarketDateException;

import ch.obermuhlner.math.big.BigDecimalMath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.netty.http.client.HttpClient;

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

    System.out.println("from " + from);
    System.out.println("to " + to);

    List<LocalDate> dates = json.getDaily().keySet().stream().map(LocalDate::parse).collect(Collectors.toList());

    LocalDate minLocalDate = StringUtils.hasText(from) ? LocalDate.parse(from) : dates.stream().min(Comparator.comparing(LocalDate::toEpochDay)).get();
    LocalDate maxLocalDate = StringUtils.hasText(to) ? LocalDate.parse(to) : dates.stream().max(Comparator.comparing(LocalDate::toEpochDay)).get();

    long days = ChronoUnit.DAYS.between(minLocalDate, maxLocalDate);

    BigDecimal period = BigDecimal.valueOf(365).divide(BigDecimal.valueOf(days), MathContext.DECIMAL128);

    // Check if the response from the market API contains our day
    if (json.getDaily().get(minLocalDate.toString()) == null) {
      throw new NotMarketDateException(minLocalDate + " is not a market date");
    }
    // Check if the response from the market API contains our day
    if (json.getDaily().get(maxLocalDate.toString()) == null) {
      throw new NotMarketDateException(maxLocalDate + " is not a market date");
    }

    String startClosingPrice = json.getDaily().get(minLocalDate.toString()).getClosingPrice();
    String endClosingPrice = json.getDaily().get(maxLocalDate.toString()).getClosingPrice();
    BigDecimal startClosingPriceBD = new BigDecimal(startClosingPrice);
    BigDecimal endClosingPriceBD = new BigDecimal(endClosingPrice);

    BigDecimal returns = endClosingPriceBD.subtract(startClosingPriceBD).divide(startClosingPriceBD, MathContext.DECIMAL128);

    System.out.println("========================");
    System.out.println(minLocalDate);
    System.out.println(maxLocalDate);
    System.out.println(days);
    System.out.println(startClosingPrice);
    System.out.println(endClosingPrice);
    System.out.println(returns);
    System.out.println("=========================");

    BigDecimal annualizedRateOfReturn = BigDecimalMath.pow(BigDecimal.ONE.add(returns), period, MathContext.DECIMAL128).subtract(BigDecimal.ONE);

    return new StockResponse(annualizedRateOfReturn, minLocalDate, maxLocalDate, days);
//    return annualizedRateOfReturn;
  }

}
