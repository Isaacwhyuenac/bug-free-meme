package com.example.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.domain.AlphaVantageTimeSeriesDailyJson;
import com.example.service.AlphaVantagePriceService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WebFluxTest
@ExtendWith(SpringExtension.class)
class StockPriceControllerTest {

//  @Autowired
//  private WebTestClient webTestClient;
//
//  @Test
//  public void testInvalidDateInput() {
//    webTestClient.get().uri(uriBuilder ->
//        uriBuilder.path("/stock/ibm")
//          .queryParam("from", "2011-01-20")
//          .queryParam("to", "2010-01-20")
//          .build()
//      )
//      .exchange()
//      .expectStatus().is4xxClientError();
//
//  }

}