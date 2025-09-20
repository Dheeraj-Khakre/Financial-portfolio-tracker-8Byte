package com.portfolio.dto;

import com.portfolio.entity.Portfolio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortfolioResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal totalValue;
    private List<AssetResponse> assets;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
