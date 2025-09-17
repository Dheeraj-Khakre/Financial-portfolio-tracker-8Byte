package com.portfolio.dto;

import com.portfolio.entity.Asset;
import com.portfolio.entity.AssetType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AssetResponse {
    private Long id;
    private String tickerSymbol;
    private String companyName;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private BigDecimal currentPrice;
    private BigDecimal totalValue;
    private AssetType assetType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AssetResponse(Asset asset) {
        this.id = asset.getId();
        this.tickerSymbol = asset.getTickerSymbol();
        this.companyName = asset.getCompanyName();
        this.quantity = asset.getQuantity();
        this.purchasePrice = asset.getPurchasePrice();
        this.currentPrice = asset.getCurrentPrice();
        this.totalValue = asset.getTotalValue();
        this.assetType = asset.getAssetType();
        this.createdAt = asset.getCreatedAt();
        this.updatedAt = asset.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTickerSymbol() { return tickerSymbol; }
    public void setTickerSymbol(String tickerSymbol) { this.tickerSymbol = tickerSymbol; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public AssetType getAssetType() { return assetType; }
    public void setAssetType(AssetType assetType) { this.assetType = assetType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
