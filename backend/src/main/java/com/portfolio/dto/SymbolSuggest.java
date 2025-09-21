package com.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymbolSuggest {
    private String symbol;
    private String companyName;
    private Double stockPrice;
}
