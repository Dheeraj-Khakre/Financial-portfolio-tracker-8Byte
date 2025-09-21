package com.portfolio.service;

import com.portfolio.dto.*;
import com.portfolio.entity.Asset;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.User;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.mapper.AssetMapper;
import com.portfolio.mapper.PortfolioMapper;
import com.portfolio.repository.AssetRepository;
import com.portfolio.repository.PortfolioRepository;
import com.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final StockDataAlphaService stockDataService;
    private final AIChatService aiChatService;

    public List<Portfolio> getUserPortfolios(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }

    public PortfolioResponse getPortfolioById1(Long portfolioId, Long userId) {
     try {
         Portfolio portfolio = getPortfolioById(portfolioId, userId);
         log.info("getPortfolioById1 called userid {} , and portfolio {}" ,userId,portfolio);
         return PortfolioMapper.toResponse(portfolio);
     }catch (RuntimeException e){
         log.error(" error got getPortfolioById1 {}",e.toString());
         throw new ResourceNotFoundException("getPortfolioById1 getting error " + userId +" portfolio id "+portfolioId);
     }
    }

    public Portfolio getPortfolioById(Long portfolioId, Long userId) {
      return   portfolioRepository.findByIdAndUserId(portfolioId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + portfolioId));
    }

    public PortfolioResponse createPortfolio(PortfolioRequest request, Long userId) {
       try {
           User user = userRepository.findById(userId)
                   .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

           Portfolio portfolio =Portfolio
                   .builder()
                   .name(request.getName())
                   .description(request.getDescription())
                   .user(user)
                   .build();
           log.info("createPortfolio called userid {} , and portfolio {}" ,userId,request);
           Portfolio save = portfolioRepository.save(portfolio);
           return    PortfolioMapper.toResponse(save);
       }catch (RuntimeException e){
           log.error(" error got createPortfolio {}",e.toString());
           throw new ResourceNotFoundException("createPortfolio got error while saving  " + userId +" portfolio id "+request);
       }
    }

    public PortfolioResponse updatePortfolio(Long portfolioId, PortfolioRequest request, Long userId) {
       try {
           Portfolio portfolio = getPortfolioById(portfolioId, userId);
           portfolio.setName(request.getName());
           portfolio.setDescription(request.getDescription());
           Portfolio save = portfolioRepository.save(portfolio);
           return    PortfolioMapper.toResponse(save);
       }catch (RuntimeException e){
           log.error(" error got updatePortfolio {}",e.toString());
           throw new ResourceNotFoundException("updatePortfolio getting  error  " + userId +" portfolio id "+portfolioId);
       }

    }

    public void deletePortfolio(Long portfolioId, Long userId) {
        Portfolio portfolio = getPortfolioById(portfolioId, userId);
        portfolioRepository.delete(portfolio);
    }
    @Transactional
    public AssetResponse addAssetToPortfolio(Long portfolioId, AssetRequest request, Long userId) {
        try {
        // Load and validate portfolio ownership
        Portfolio portfolio = getPortfolioById(portfolioId, userId);

        // Ensure ticker not already present
        assetRepository.findByPortfolioIdAndTickerSymbol(portfolioId, request.getTickerSymbol())
                .ifPresent(a -> {
                    throw new IllegalArgumentException(
                            "Asset with ticker " + request.getTickerSymbol() + " already exists");
                });

            SymbolSuggest closestStockSymbol = aiChatService.findClosestStockSymbol(request.getTickerSymbol());
            // External stock data
        var stockData = stockDataService.getStockData(closestStockSymbol.getSymbol());

        Asset asset = Asset.builder()
                .tickerSymbol(closestStockSymbol.getSymbol())
                .companyName(stockData.getCompanyName())
                .quantity(request.getQuantity())
                .purchasePrice(request.getPurchasePrice())
                .currentPrice(
                        request.getPurchasePrice() != null ? request.getPurchasePrice() : stockData.getCurrentPrice())
                .assetType(request.getAssetType())
                .portfolio(portfolio)
                .build();

        // compute total value for this asset
        asset.setTotalValue(asset.getQuantity().multiply(asset.getCurrentPrice()));

        // Persist the new asset
        Asset saved = assetRepository.save(asset);

        // Update portfolio total directly from DB to avoid ConcurrentModificationException
        updatePortfolioTotalValue(portfolioId);

        return AssetMapper.toResponse(saved);
    }    catch (RuntimeException e){
            log.error(" error got addAssetToPortfolio {}",e.toString());
            throw new ResourceNotFoundException("addAssetToPortfolio getting  error  " + userId +" portfolio id "+portfolioId);
        }

    }

    public AssetResponse updateAsset(Long portfolioId, Long assetId, AssetRequest request, Long userId) {
       try {
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
           SymbolSuggest closestStockSymbol = aiChatService.findClosestStockSymbol(request.getTickerSymbol());
           // External stock data
           var stockData = stockDataService.getStockData(closestStockSymbol.getSymbol());

           asset.setCurrentPrice(stockData.getCurrentPrice());
           asset.setTotalValue(asset.getQuantity().multiply(asset.getCurrentPrice()));

           Asset savedAsset = assetRepository.save(asset);
           updatePortfolioTotalValue(portfolioId);

           return AssetMapper.toResponse(savedAsset);
       }catch (RuntimeException e){
           log.error(" error got updateAsset {}",e.toString());
           throw new ResourceNotFoundException("updateAsset getting  error  " + userId +" portfolio id "+portfolioId);
       }
    }

    public void removeAssetFromPortfolio(Long portfolioId, Long assetId, Long userId) {
        Portfolio portfolio = getPortfolioById(portfolioId, userId);
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + assetId));

        if (!asset.getPortfolio().getId().equals(portfolioId)) {
            throw new IllegalArgumentException("Asset does not belong to the specified portfolio");
        }

        assetRepository.delete(asset);
        updatePortfolioTotalValue(portfolioId);
    }

    public void updatePortfolioPrices(Long portfolioId, Long userId) {
       try{
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

           updatePortfolioTotalValue(portfolioId);
       }catch (RuntimeException e){
           log.error(" error got updatePortfolioPrices {}",e.toString());
           throw new ResourceNotFoundException("updatePortfolioPrices getting  error  " + userId +" portfolio id "+portfolioId);
       }

    }
    @Transactional
    protected void updatePortfolioTotalValue(Long portfolioId) {
        BigDecimal total = assetRepository.sumTotalValue(portfolioId);
        portfolioRepository.findById(portfolioId)
                .ifPresent(p ->
                    p.setTotalValue(total));
    }
}
