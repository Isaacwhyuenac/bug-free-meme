package com.example.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.domain.AlphaVantageTimeSeriesDailyJson;
import com.example.domain.StockResponse;
import com.example.exeption.NotMarketDateException;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
class AlphaVantagePriceServiceTest {

  @Autowired
  private JacksonTester<AlphaVantageTimeSeriesDailyJson> jacksonTester;

  @Autowired
  private AlphaVantagePriceService alphaVantagePriceService;

//  @BeforeEach
//  public void setup() {
//    ObjectMapper objectMapper = new ObjectMapper();
//    JacksonTester.initFields(this, objectMapper);
//  }

  @Test
  public void testJsonIsNotNull() throws IOException {
    AlphaVantageTimeSeriesDailyJson alphaVantageTimeSeriesDailyJson = jacksonTester.readObject(new ClassPathResource("ibm.json"));

    Assertions.assertNotNull(alphaVantageTimeSeriesDailyJson);
  }

  @Test
  public void testGetLatestClosingPriceWithFrom() throws IOException, NotMarketDateException {
    AlphaVantageTimeSeriesDailyJson alphaVantageTimeSeriesDailyJson = jacksonTester.readObject(new ClassPathResource("ibm.json"));

    Map<String, String> map = new HashMap<>();

    String from = "2011-10-20";
    map.put("from", from);

    StockResponse stockResponse = alphaVantagePriceService.getLatestClosingPrice(alphaVantageTimeSeriesDailyJson, map);

    Assertions.assertEquals(LocalDate.parse(from), stockResponse.getStartDate());
    Assertions.assertNotNull(stockResponse.getAnnualizedRateOfReturn());
  }

  @Test
  public void testGetLatestClosingPriceWithTo() throws IOException, NotMarketDateException {
    AlphaVantageTimeSeriesDailyJson alphaVantageTimeSeriesDailyJson = jacksonTester.readObject(new ClassPathResource("ibm.json"));

    Map<String, String> map = new HashMap<>();

    String to = "2011-10-20";
    map.put("to", to);

    StockResponse stockResponse = alphaVantagePriceService.getLatestClosingPrice(alphaVantageTimeSeriesDailyJson, map);

    Assertions.assertEquals(LocalDate.parse(to), stockResponse.getEndDate());
    Assertions.assertNotNull(stockResponse.getAnnualizedRateOfReturn());
  }

//  @Test
//  public void testGetLatestClosingPriceWithNotMarketDate() throws IOException, NotMarketDateException {
//    AlphaVantageTimeSeriesDailyJson alphaVantageTimeSeriesDailyJson = jacksonTester.readObject(new ClassPathResource("ibm.json"));
//
//    Map<String, String> map = new HashMap<>();
//
//    String to = "2011-10-22";
//    map.put("to", to);
//
//    Exception exception = Assertions.assertThrows(NotMarketDateException.class, () -> {
//      alphaVantagePriceService.getLatestClosingPrice(alphaVantageTimeSeriesDailyJson, map);
//    });
//
//    String expectedMessage = " is not a market date";
//    String actualMessage = exception.getMessage();
//    Assertions.assertTrue(actualMessage.contains(expectedMessage));
//
//  }

}