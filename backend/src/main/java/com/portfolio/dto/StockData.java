package com.portfolio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    // Constructors
    public StockData() {}

    public StockData(String symbol, String companyName, BigDecimal currentPrice) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
    }

    // Getters and Setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public BigDecimal getChangeAmount() { return changeAmount; }
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }

    public BigDecimal getChangePercent() { return changePercent; }
    public void setChangePercent(BigDecimal changePercent) { this.changePercent = changePercent; }

    public BigDecimal getOpenPrice() { return openPrice; }
    public void setOpenPrice(BigDecimal openPrice) { this.openPrice = openPrice; }

    public BigDecimal getHighPrice() { return highPrice; }
    public void setHighPrice(BigDecimal highPrice) { this.highPrice = highPrice; }

    public BigDecimal getLowPrice() { return lowPrice; }
    public void setLowPrice(BigDecimal lowPrice) { this.lowPrice = lowPrice; }

    public BigDecimal getPreviousClose() { return previousClose; }
    public void setPreviousClose(BigDecimal previousClose) { this.previousClose = previousClose; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }

    public List<HistoricalPrice> getHistoricalPrices() { return historicalPrices; }
    public void setHistoricalPrices(List<HistoricalPrice> historicalPrices) { this.historicalPrices = historicalPrices; }

    public static class HistoricalPrice {
        private LocalDate date;
        private BigDecimal price;

        public HistoricalPrice() {}

        public HistoricalPrice(LocalDate date, BigDecimal price) {
            this.date = date;
            this.price = price;
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }
}
