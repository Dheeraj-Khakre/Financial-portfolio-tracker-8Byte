package com.portfolio.controller;

import com.portfolio.dto.StockData;
import com.portfolio.service.StockDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StockController {

    @Autowired
    private StockDataService stockDataService;

    @GetMapping("/{symbol}")
    public ResponseEntity<StockData> getStockData(@PathVariable String symbol) {
        StockData stockData = stockDataService.getStockData(symbol);
        return ResponseEntity.ok(stockData);
    }

    @GetMapping("/{symbol}/history")
    public ResponseEntity<List<StockData.HistoricalPrice>> getHistoricalPrices(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "30") int days) {
        List<StockData.HistoricalPrice> prices = stockDataService.getHistoricalPrices(symbol, days);
        return ResponseEntity.ok(prices);
    }
}
