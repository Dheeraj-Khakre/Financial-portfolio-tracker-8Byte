package com.portfolio.controller;

import com.portfolio.dto.HistoricalPrice;
import com.portfolio.dto.StockData;
import com.portfolio.service.StockDataAlphaService;
import com.portfolio.service.StockDataService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@Slf4j
public class StockController {


    private final StockDataService stockDataService;
    private final StockDataAlphaService stockDataAlphaService;

    @GetMapping("/{symbol}")
    @CircuitBreaker(name = "stockApi", fallbackMethod = "fallbackStock")
    @Retry(name = "stockApi")
    public ResponseEntity<StockData> getStockData(@PathVariable String symbol) {
        log.info("response from Alpha Vantage for symbol {}",symbol);
        StockData stockData = stockDataAlphaService.getStockData(symbol);
        return ResponseEntity.ok(stockData);
    }

    public ResponseEntity<StockData> fallbackStock( String symbol, Throwable ex) {
        log.error("got error in getStockData while fetching data {}",ex.getMessage());
        StockData stockData = stockDataService.getStockData(symbol);
        return ResponseEntity.ok(stockData);
    }

    @GetMapping("/{symbol}/history")
    public ResponseEntity<List<HistoricalPrice>> getHistoricalPrices(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "30") int days) {
        List<HistoricalPrice> prices = stockDataService.getHistoricalPrices(symbol, days);
        return ResponseEntity.ok(prices);
    }
}
