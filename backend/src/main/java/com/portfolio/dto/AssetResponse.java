package com.portfolio.dto;

import com.portfolio.entity.Asset;
import com.portfolio.entity.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

}
