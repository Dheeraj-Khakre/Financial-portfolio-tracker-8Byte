package com.portfolio.mapper;
import com.portfolio.entity.Asset;
import com.portfolio.entity.AssetType;
import com.portfolio.dto.AssetResponse;   // adjust the package to where AssetResponse resides

/**
 * Utility class for converting Asset entities to AssetResponse DTOs.
 */
public final class AssetMapper {

    private AssetMapper() {
        // prevent instantiation
    }

    public static AssetResponse toResponse(Asset asset) {
        if (asset == null) {
            return null;
        }

        return AssetResponse.builder()
                .id(asset.getId())
                .tickerSymbol(asset.getTickerSymbol())
                .companyName(asset.getCompanyName())
                .quantity(asset.getQuantity())
                .purchasePrice(asset.getPurchasePrice())
                .currentPrice(asset.getCurrentPrice())
                .totalValue(asset.getTotalValue())
                .assetType(asset.getAssetType())
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }
}
