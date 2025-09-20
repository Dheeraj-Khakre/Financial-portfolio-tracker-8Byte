package com.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockData {
    private String symbol;
    private String companyName;
    private BigDecimal currentPrice;
    private BigDecimal changeAmount;
    private BigDecimal changePercent;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal previousClose;
    private Long volume;
    private LocalDate lastUpdated;
    private List<HistoricalPrice> historicalPrices;
}
