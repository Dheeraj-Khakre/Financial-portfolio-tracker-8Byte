package com.portfolio.mapper;

import com.portfolio.dto.PortfolioResponse;
import com.portfolio.dto.AssetResponse;
import com.portfolio.entity.Portfolio;
import java.util.List;
import java.util.stream.Collectors;

public final class PortfolioMapper {

    private PortfolioMapper() {
    }

    /**
     * Entity → DTO (with nested AssetResponse list)
     */
    public static PortfolioResponse toResponse(Portfolio portfolio) {
        if (portfolio == null) return null;

        // convert Set<Asset> to List<AssetResponse>
        List<AssetResponse> assetResponses = portfolio.getAssets() == null ? List.of()
                : portfolio.getAssets().stream()
                .map(AssetMapper::toResponse)
                .toList();

        return PortfolioResponse.builder()
                .id(portfolio.getId())
                .name(portfolio.getName())
                .description(portfolio.getDescription())
                .totalValue(portfolio.getTotalValue())
                .assets(assetResponses)
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .build();
    }
}