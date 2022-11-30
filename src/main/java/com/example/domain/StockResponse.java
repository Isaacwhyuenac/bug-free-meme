package com.example.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import com.example.domain.AlphaVantageTimeSeriesDailyJsonDaily;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {

  @JsonProperty("annualizedRateOfReturn")
  private BigDecimal annualizedRateOfReturn;

  @JsonProperty("startDate")
  private LocalDate startDate;

  @JsonProperty("endDate")
  private LocalDate endDate;

  @JsonProperty("dayInBetween")
  private long days;

//  @JsonProperty("content")
//  private Map<String, AlphaVantageTimeSeriesDailyJsonDaily> daily;

}
