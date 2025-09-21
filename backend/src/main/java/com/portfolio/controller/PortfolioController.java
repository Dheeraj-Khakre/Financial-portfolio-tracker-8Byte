package com.portfolio.controller;

import com.portfolio.dto.*;
import com.portfolio.entity.Portfolio;
import com.portfolio.mapper.PortfolioMapper;
import com.portfolio.security.UserPrincipal;
import com.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/portfolios")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping
    public ResponseEntity<List<PortfolioResponse>> getUserPortfolios(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Portfolio> portfolios = portfolioService.getUserPortfolios(userPrincipal.getId());
        List<PortfolioResponse> response = portfolios.stream()
                .map(PortfolioMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioResponse> getPortfolio(@PathVariable Long id,
                                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        PortfolioResponse portfolio = portfolioService.getPortfolioById1(id, userPrincipal.getId());
        return ResponseEntity.ok(portfolio);
    }

    @PostMapping
    public ResponseEntity<PortfolioResponse> createPortfolio(@Valid @RequestBody PortfolioRequest request,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal) {
        PortfolioResponse portfolio = portfolioService.createPortfolio(request, userPrincipal.getId());
        return ResponseEntity.ok(portfolio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortfolioResponse> updatePortfolio(@PathVariable Long id,
                                                             @Valid @RequestBody PortfolioRequest request,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal) {
        PortfolioResponse portfolio = portfolioService.updatePortfolio(id, request, userPrincipal.getId());
        return ResponseEntity.ok(portfolio);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deletePortfolio(@PathVariable Long id,
                                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        portfolioService.deletePortfolio(id, userPrincipal.getId());
        return ResponseEntity.ok(MessageResponse.builder().message("Portfolio deleted successfully").build());
    }

    @PostMapping("/{id}/assets")
    public ResponseEntity<AssetResponse> addAsset(@PathVariable Long id,
                                                  @Valid @RequestBody AssetRequest request,
                                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        AssetResponse asset = portfolioService.addAssetToPortfolio(id, request, userPrincipal.getId());
        return ResponseEntity.ok(asset);
    }

    @PutMapping("/{portfolioId}/assets/{assetId}")
    public ResponseEntity<AssetResponse> updateAsset(@PathVariable Long portfolioId,
                                                     @PathVariable Long assetId,
                                                     @Valid @RequestBody AssetRequest request,
                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) {
        AssetResponse asset = portfolioService.updateAsset(portfolioId, assetId, request, userPrincipal.getId());
        return ResponseEntity.ok(asset);
    }

    @DeleteMapping("/{portfolioId}/assets/{assetId}")
    public ResponseEntity<MessageResponse> removeAsset(@PathVariable Long portfolioId,
                                                       @PathVariable Long assetId,
                                                       @AuthenticationPrincipal UserPrincipal userPrincipal) {
        portfolioService.removeAssetFromPortfolio(portfolioId, assetId, userPrincipal.getId());
        return ResponseEntity.ok(MessageResponse.builder().message("Asset removed successfully").build());
    }

    @PostMapping("/{id}/refresh")
    public ResponseEntity<MessageResponse> refreshPortfolioPrices(@PathVariable Long id,
                                                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        portfolioService.updatePortfolioPrices(id, userPrincipal.getId());
        return ResponseEntity.ok(MessageResponse.builder().message("Portfolio prices updated successfully").build());
    }
}
