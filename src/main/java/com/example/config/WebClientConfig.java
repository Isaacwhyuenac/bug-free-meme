package com.example.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
  @Bean
  public WebClient webClient() {
    HttpClient httpClient = HttpClient.create()
      .responseTimeout(Duration.ofMinutes(1));

    WebClient webClient = WebClient.builder()
      .clientConnector(new ReactorClientHttpConnector(httpClient))
      .exchangeStrategies(ExchangeStrategies
        .builder()
        .codecs(codecs -> codecs
          .defaultCodecs()
          .maxInMemorySize(1000 * 1024 * 1024))
        .build())
      .build();

    return webClient;
  }
}
