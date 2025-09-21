package com.portfolio.controller;

import com.portfolio.dto.AIInsightResponse;
import com.portfolio.dto.SymbolSuggest;
import com.portfolio.entity.Portfolio;
import com.portfolio.security.UserPrincipal;
import com.portfolio.service.AIChatService;
import com.portfolio.service.AIInsightService;
import com.portfolio.service.PortfolioService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@Slf4j
public class AIController {


    private final AIInsightService aiInsightService;
    private  final AIChatService aiChatService;
    private final PortfolioService portfolioService;


    @CircuitBreaker(name = "AIApi", fallbackMethod = "fallbackPortfolioInsights")
    @Retry(name = "AIApi")
    @GetMapping("/insights/{portfolioId}")
    public ResponseEntity<AIInsightResponse> getPortfolioInsights(
            @PathVariable Long portfolioId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId, userPrincipal.getId());
        AIInsightResponse insights = aiChatService.getAiInsightResponse(portfolio, userPrincipal.getUsername());

        return ResponseEntity.ok(insights);
    }

    public ResponseEntity<AIInsightResponse> fallbackPortfolioInsights(
             Long portfolioId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,Throwable ex) {
        log.error("got error in getStockData while fetching data {}",ex.getMessage());

        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId, userPrincipal.getId());
        AIInsightResponse insights = aiInsightService.generatePortfolioInsights(portfolio);

        return ResponseEntity.ok(insights);
    }

    // testing insights on postman ...
    @GetMapping("/insights/ai/{portfolioId}")
    public ResponseEntity<AIInsightResponse> getPortfolioInsightsfromAi(
            @PathVariable Long portfolioId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId, userPrincipal.getId());
        AIInsightResponse insights = aiChatService.getAiInsightResponse(portfolio, userPrincipal.getUsername());

        return ResponseEntity.ok(insights);
    }

    @GetMapping("/symbol-suggest")
    public ResponseEntity<SymbolSuggest> suggestSymbol(@RequestParam String q) {
        SymbolSuggest suggestion = aiChatService.findClosestStockSymbol(q);
        return ResponseEntity.ok(suggestion);
    }


    // this controller only for testing...
//    @GetMapping("/chat")
//    public ResponseEntity<String> getResponse(@RequestParam("q") String q, @AuthenticationPrincipal UserPrincipal userPrincipal ){
//       String response=  aiChatService.getResponse(q, userPrincipal.getUsername());
//       return  ResponseEntity.ok(response);
//    }



}
