package com.portfolio.service;

import com.portfolio.dto.StockData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Service
@Slf4j
public class StockDataAlphaService {



     @Value("${stock.api.key}")
    private String apiKey;

    @Value("${stock.api.base-url}")
    private String baseUrl;

    private final WebClient webClient;

    public StockDataAlphaService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public StockData getStockData(String symbol) {
        // ---- call the real API ----
        String url = baseUrl + "?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + apiKey;

        Map<String, Object> response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        Map<String, String> quote = (Map<String, String>) response.get("Global Quote");
        if (quote == null || quote.isEmpty()) {
            throw new IllegalStateException("Invalid response from Alpha Vantage for symbol " + symbol);
        }

        return StockData.builder()
                .symbol(quote.get("01. symbol"))
                .companyName(symbol.toUpperCase())  // Alpha Vantage doesn’t return name; you can enrich later
                .currentPrice(new BigDecimal(quote.get("05. price")))
                .previousClose(new BigDecimal(quote.get("08. previous close")))
                .openPrice(new BigDecimal(quote.get("02. open")))
                .highPrice(new BigDecimal(quote.get("03. high")))
                .lowPrice(new BigDecimal(quote.get("04. low")))
                .changeAmount(new BigDecimal(quote.get("09. change")))
                .changePercent(new BigDecimal(
                        quote.get("10. change percent").replace("%", "")
                ))
                .volume(Long.parseLong(quote.get("06. volume")))
                .lastUpdated(LocalDate.parse(quote.get("07. latest trading day")))
                .build();
    }
}
