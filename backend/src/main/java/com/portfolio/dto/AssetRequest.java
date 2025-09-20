package com.portfolio.dto;

import com.portfolio.entity.AssetType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetRequest {
    @NotBlank
    @Size(max = 10)
    private String tickerSymbol;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal quantity;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal purchasePrice;

    private AssetType assetType = AssetType.STOCK;

}
