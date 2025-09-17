package com.portfolio.service;

import com.portfolio.dto.AssetRequest;
import com.portfolio.dto.PortfolioRequest;
import com.portfolio.entity.Asset;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.User;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.repository.AssetRepository;
import com.portfolio.repository.PortfolioRepository;
import com.portfolio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockDataService stockDataService;

    public List<Portfolio> getUserPortfolios(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }

    public Portfolio getPortfolioById(Long portfolioId, Long userId) {
        return portfolioRepository.findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + portfolioId));
    }

    public Portfolio createPortfolio(PortfolioRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Portfolio portfolio = new Portfolio(request.getName(), request.getDescription(), user);
        return portfolioRepository.save(portfolio);
    }

    public Portfolio updatePortfolio(Long portfolioId, PortfolioRequest request, Long userId) {
        Portfolio portfolio = getPortfolioById(portfolioId, userId);
        portfolio.setName(request.getName());
        portfolio.setDescription(request.getDescription());
        return portfolioRepository.save(portfolio);
    }

    public void deletePortfolio(Long portfolioId, Long userId) {
        Portfolio portfolio = getPortfolioById(portfolioId, userId);
        portfolioRepository.delete(portfolio);
    }

    public Asset addAssetToPortfolio(Long portfolioId, AssetRequest request, Long userId) {
        Portfolio portfolio = getPortfolioById(portfolioId, userId);

        // Check if asset already exists in portfolio
        assetRepository.findByPortfolioIdAndTickerSymbol(portfolioId, request.getTickerSymbol())
                .ifPresent(existingAsset -> {
                    throw new IllegalArgumentException("Asset with ticker " + request.getTickerSymbol() +
                            " already exists in portfolio");
                });

        // Get stock data
        var stockData = stockDataService.getStockData(request.getTickerSymbol());

        Asset asset = new Asset(
                request.getTickerSymbol(),
                stockData.getCompanyName(),
                request.getQuantity(),
                request.getPurchasePrice() != null ? request.getPurchasePrice() : stockData.getCurrentPrice(),
                portfolio
        );

        asset.setCurrentPrice(stockData.getCurrentPrice());
        asset.setAssetType(request.getAssetType());
        asset.setTotalValue(asset.getQuantity().multiply(asset.getCurrentPrice()));

        Asset savedAsset = assetRepository.save(asset);
        updatePortfolioTotalValue(portfolio);

        return savedAsset;
    }

    public Asset updateAsset(Long portfolioId, Long assetId, AssetRequest request, Long userId) {
        Portfolio portfolio = getPortfolioById(portfolioId, userId);
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + assetId));

        if (!asset.getPortfolio().getId().equals(portfolioId)) {
            throw new IllegalArgumentException("Asset does not belong to the specified portfolio");
        }

        asset.setQuantity(request.getQuantity());
        if (request.getPurchasePrice() != null) {
            asset.setPurchasePrice(request.getPurchasePrice());
        }
        asset.setAssetType(request.getAssetType());

        // Update current price and total value
        var stockData = stockDataService.getStockData(asset.getTickerSymbol());
        asset.setCurrentPrice(stockData.getCurrentPrice());
        asset.setTotalValue(asset.getQuantity().multiply(asset.getCurrentPrice()));

        Asset savedAsset = assetRepository.save(asset);
        updatePortfolioTotalValue(portfolio);

        return savedAsset;
    }

    public void removeAssetFromPortfolio(Long portfolioId, Long assetId, Long userId) {
        Portfolio portfolio = getPortfolioById(portfolioId, userId);
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + assetId));

        if (!asset.getPortfolio().getId().equals(portfolioId)) {
            throw new IllegalArgumentException("Asset does not belong to the specified portfolio");
        }

        assetRepository.delete(asset);
        updatePortfolioTotalValue(portfolio);
    }

    public void updatePortfolioPrices(Long portfolioId, Long userId) {
        Portfolio portfolio = portfolioRepository.findByIdWithAssets(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + portfolioId));

        if (!portfolio.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Portfolio does not belong to the specified user");
        }

        for (Asset asset : portfolio.getAssets()) {
            var stockData = stockDataService.getStockData(asset.getTickerSymbol());
            asset.setCurrentPrice(stockData.getCurrentPrice());
            asset.setTotalValue(asset.getQuantity().multiply(asset.getCurrentPrice()));
            assetRepository.save(asset);
        }

        updatePortfolioTotalValue(portfolio);
    }

    private void updatePortfolioTotalValue(Portfolio portfolio) {
        BigDecimal totalValue = portfolio.getAssets().stream()
                .map(Asset::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        portfolio.setTotalValue(totalValue);
        portfolioRepository.save(portfolio);
    }
}
