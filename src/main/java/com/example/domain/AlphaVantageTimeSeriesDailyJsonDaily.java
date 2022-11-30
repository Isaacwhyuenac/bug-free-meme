package com.example.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlphaVantageTimeSeriesDailyJsonDaily {
  @JsonProperty("1. open")
  private String openingPrice;
  @JsonProperty("2. high")
  private String highPrice;
  @JsonProperty("3. low")
  private String lowPrice;
  @JsonProperty("4. close")
  private String closingPrice;
  @JsonProperty("5. adjusted close")
  private String adjustedClose;
  @JsonProperty("6. volume")
  private String volume;
  @JsonProperty("7. dividend amount")
  private String dividendAmount;
  @JsonProperty("8. split coefficient")
  private String splitCoefficient;
}
