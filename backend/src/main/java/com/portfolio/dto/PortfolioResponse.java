package com.portfolio.dto;

import com.portfolio.entity.Portfolio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PortfolioResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal totalValue;
    private List<AssetResponse> assets;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PortfolioResponse(Portfolio portfolio) {
        this.id = portfolio.getId();
        this.name = portfolio.getName();
        this.description = portfolio.getDescription();
        this.totalValue = portfolio.getTotalValue();
        this.createdAt = portfolio.getCreatedAt();
        this.updatedAt = portfolio.getUpdatedAt();
        this.assets = portfolio.getAssets().stream()
                .map(AssetResponse::new)
                .collect(Collectors.toList());
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public List<AssetResponse> getAssets() { return assets; }
    public void setAssets(List<AssetResponse> assets) { this.assets = assets; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
