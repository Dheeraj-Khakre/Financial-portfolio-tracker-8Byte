package com.portfolio.service;

import com.portfolio.dto.StockData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;

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

    @Value("${stock.api.key}")
    private String apiKey;

    @Value("${stock.api.base-url}")
    private String baseUrl;

//    private final WebClient webClient;
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

//    public StockDataService() {
//        this.webClient = WebClient.builder().build();
//    }

    public StockData getStockData(String symbol) {
        try {
            // For demo purposes, we'll use mock data instead of real API calls
            // In production, you would uncomment the real API call below
            return getMockStockData(symbol);

            // Real API call (commented out for demo)
            // return getRealStockData(symbol);
        } catch (Exception e) {
            logger.error("Error fetching stock data for symbol: {}", symbol, e);
            return getMockStockData(symbol);
        }
    }

    public List<StockData.HistoricalPrice> getHistoricalPrices(String symbol, int days) {
        List<StockData.HistoricalPrice> prices = new ArrayList<>();
        BigDecimal basePrice = getRandomPrice();

        for (int i = days; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            BigDecimal price = basePrice.add(BigDecimal.valueOf(random.nextGaussian() * 5))
                    .setScale(2, RoundingMode.HALF_UP);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                price = BigDecimal.valueOf(10.00);
            }
            prices.add(new StockData.HistoricalPrice(date, price));
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

        StockData stockData = new StockData(symbol.toUpperCase(), companyName, currentPrice);
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

    // Uncomment and implement for real API integration
    /*
    private StockData getRealStockData(String symbol) {
        String url = baseUrl + "?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + apiKey;

        try {
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // Parse Alpha Vantage response
            Map<String, String> quote = (Map<String, String>) response.get("Global Quote");

            if (quote == null) {
                throw new RuntimeException("Invalid response from stock API");
            }

            StockData stockData = new StockData();
            stockData.setSymbol(quote.get("01. symbol"));
            stockData.setCurrentPrice(new BigDecimal(quote.get("05. price")));
            stockData.setChangeAmount(new BigDecimal(quote.get("09. change")));
            stockData.setChangePercent(new BigDecimal(quote.get("10. change percent").replace("%", "")));
            stockData.setOpenPrice(new BigDecimal(quote.get("02. open")));
            stockData.setHighPrice(new BigDecimal(quote.get("03. high")));
            stockData.setLowPrice(new BigDecimal(quote.get("04. low")));
            stockData.setPreviousClose(new BigDecimal(quote.get("08. previous close")));
            stockData.setVolume(Long.parseLong(quote.get("06. volume")));
            stockData.setLastUpdated(LocalDate.parse(quote.get("07. latest trading day")));

            return stockData;
        } catch (Exception e) {
            logger.error("Error calling stock API", e);
            throw new RuntimeException("Failed to fetch stock data", e);
        }
    }
    */
}
