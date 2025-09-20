package com.portfolio.service;

import com.portfolio.dto.HistoricalPrice;
import com.portfolio.dto.StockData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class StockDataService {

    private static final Logger logger = LoggerFactory.getLogger(StockDataService.class);
    private final Random random = new Random();

    // Mock data for demo purposes
    private final Map<String, String> stockNames = Map.of(
            "AAPL", "Apple Inc.",
            "GOOGL", "Alphabet Inc.",
            "MSFT", "Microsoft Corporation",
            "AMZN", "Amazon.com Inc.",
            "TSLA", "Tesla Inc.",
            "META", "Meta Platforms Inc.",
            "NVDA", "NVIDIA Corporation",
            "NFLX", "Netflix Inc.",
            "AMD", "Advanced Micro Devices Inc.",
            "INTC", "Intel Corporation"
    );
    public StockData getStockData(String symbol) {
        try {
            return getMockStockData(symbol);
        } catch (Exception e) {
            logger.error("Error fetching stock data for symbol: {}", symbol, e);
            return getMockStockData(symbol);
        }
    }

    public List<HistoricalPrice> getHistoricalPrices(String symbol, int days) {
        List<HistoricalPrice> prices = new ArrayList<>();
        BigDecimal basePrice = getRandomPrice();

        for (int i = days; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            BigDecimal price = basePrice.add(BigDecimal.valueOf(random.nextGaussian() * 5))
                    .setScale(2, RoundingMode.HALF_UP);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                price = BigDecimal.valueOf(10.00);
            }
            prices.add(HistoricalPrice.builder().price(price).date(date).build());
            basePrice = price;
        }

        return prices;
    }

    private StockData getMockStockData(String symbol) {
        String companyName = stockNames.getOrDefault(symbol.toUpperCase(), symbol + " Corporation");
        BigDecimal currentPrice = getRandomPrice();
        BigDecimal previousClose = currentPrice.subtract(BigDecimal.valueOf(random.nextGaussian() * 2))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal changeAmount = currentPrice.subtract(previousClose);
        BigDecimal changePercent = changeAmount.divide(previousClose, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        StockData stockData = StockData
                .builder()
                .currentPrice(currentPrice)
                .companyName(companyName)
                .symbol(symbol.toUpperCase())
                .build();
        stockData.setPreviousClose(previousClose);
        stockData.setChangeAmount(changeAmount);
        stockData.setChangePercent(changePercent);
        stockData.setOpenPrice(previousClose.add(BigDecimal.valueOf(random.nextGaussian() * 1)));
        stockData.setHighPrice(currentPrice.add(BigDecimal.valueOf(Math.abs(random.nextGaussian()) * 2)));
        stockData.setLowPrice(currentPrice.subtract(BigDecimal.valueOf(Math.abs(random.nextGaussian()) * 2)));
        stockData.setVolume((long) (1000000 + random.nextInt(10000000)));
        stockData.setLastUpdated(LocalDate.now());
        stockData.setHistoricalPrices(getHistoricalPrices(symbol, 30));

        return stockData;
    }

    private BigDecimal getRandomPrice() {
        return BigDecimal.valueOf(50 + random.nextDouble() * 200)
                .setScale(2, RoundingMode.HALF_UP);
    }

}
