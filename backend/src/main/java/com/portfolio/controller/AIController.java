package com.portfolio.controller;

import com.portfolio.dto.AIInsightResponse;
import com.portfolio.entity.Portfolio;
import com.portfolio.security.UserPrincipal;
import com.portfolio.service.AIInsightService;
import com.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AIController {


    private final AIInsightService aiInsightService;

    private final PortfolioService portfolioService;

    @GetMapping("/insights/{portfolioId}")
    public ResponseEntity<AIInsightResponse> getPortfolioInsights(
            @PathVariable Long portfolioId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId, userPrincipal.getId());
        AIInsightResponse insights = aiInsightService.generatePortfolioInsights(portfolio);

        return ResponseEntity.ok(insights);
    }
}
