package com.portfolio.service;

import com.portfolio.dto.AIInsightResponse;
import com.portfolio.dto.AssetAllocation;
import com.portfolio.entity.Asset;
import com.portfolio.entity.AssetType;
import com.portfolio.entity.Portfolio;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIInsightService {

    private final Random random = new Random();

    public AIInsightResponse generatePortfolioInsights(Portfolio portfolio) {
        if (portfolio.getAssets().isEmpty()) {
            return generateEmptyPortfolioInsights();
        }

        BigDecimal diversificationScore = calculateDiversificationScore(portfolio);
        String riskLevel = calculateRiskLevel(portfolio);
        List<String> recommendations = generateRecommendations(portfolio);
        List<AssetAllocation> assetAllocation = calculateAssetAllocation(portfolio);
        String summary = generateSummary(portfolio, diversificationScore, riskLevel);

        return AIInsightResponse
                .builder()
                .diversificationScore(diversificationScore)
                .riskLevel(riskLevel)
                .recommendations(recommendations)
                .summary(summary)
                .assetAllocation(assetAllocation)
                .build();
    }

    private BigDecimal calculateDiversificationScore(Portfolio portfolio) {
        Set<Asset> assets = portfolio.getAssets();

        if (assets.size() <= 1) {
            return BigDecimal.valueOf(20);
        }

        // Calculate sector diversity (simplified)
        Map<AssetType, BigDecimal> typeDistribution = assets.stream()
                .collect(Collectors.groupingBy(
                        Asset::getAssetType,
                        Collectors.reducing(BigDecimal.ZERO, Asset::getTotalValue, BigDecimal::add)
                ));

        // Calculate Herfindahl-Hirschman Index for concentration
        BigDecimal totalValue = portfolio.getTotalValue();
        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(50);
        }

        BigDecimal hhi = typeDistribution.values().stream()
                .map(value -> {
                    BigDecimal share = value.divide(totalValue, 4, RoundingMode.HALF_UP);
                    return share.multiply(share);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Convert HHI to diversification score (0-100)
        BigDecimal diversificationScore = BigDecimal.ONE.subtract(hhi).multiply(BigDecimal.valueOf(100));

        // Add bonus for number of assets
        int assetCount = assets.size();
        BigDecimal assetBonus = BigDecimal.valueOf(Math.min(assetCount * 5, 30));

        return diversificationScore.add(assetBonus).min(BigDecimal.valueOf(100)).max(BigDecimal.ZERO)
                .setScale(1, RoundingMode.HALF_UP);
    }

    private String calculateRiskLevel(Portfolio portfolio) {
        BigDecimal diversificationScore = calculateDiversificationScore(portfolio);
        int assetCount = portfolio.getAssets().size();

        if (diversificationScore.compareTo(BigDecimal.valueOf(70)) >= 0 && assetCount >= 5) {
            return "Low";
        } else if (diversificationScore.compareTo(BigDecimal.valueOf(40)) >= 0 && assetCount >= 3) {
            return "Medium";
        } else {
            return "High";
        }
    }

    private List<String> generateRecommendations(Portfolio portfolio) {
        List<String> recommendations = new ArrayList<>();
        Set<Asset> assets = portfolio.getAssets();

        if (assets.size() < 3) {
            recommendations.add("Consider adding more assets to improve diversification");
        }

        Map<AssetType, Long> typeCount = assets.stream()
                .collect(Collectors.groupingBy(Asset::getAssetType, Collectors.counting()));

        if (typeCount.size() == 1) {
            recommendations.add("Diversify across different asset types (ETFs, bonds, etc.)");
        }

        // Check for concentration risk
        BigDecimal totalValue = portfolio.getTotalValue();
        if (totalValue.compareTo(BigDecimal.ZERO) > 0) {
            assets.stream()
                    .filter(asset -> asset.getTotalValue().divide(totalValue, 4, RoundingMode.HALF_UP)
                            .compareTo(BigDecimal.valueOf(0.4)) > 0)
                    .findFirst()
                    .ifPresent(asset -> recommendations.add("Consider reducing concentration in " + asset.getTickerSymbol()));
        }

        // Add some AI-like recommendations
        String[] aiRecommendations = {
                "Based on market trends, consider adding technology ETFs",
                "Your portfolio could benefit from international exposure",
                "Consider adding defensive stocks for stability",
                "ESG funds are showing strong performance lately",
                "Small-cap stocks could enhance your growth potential"
        };

        if (recommendations.size() < 3) {
            recommendations.add(aiRecommendations[random.nextInt(aiRecommendations.length)]);
        }

        return recommendations;
    }

    private List<AssetAllocation> calculateAssetAllocation(Portfolio portfolio) {
        BigDecimal totalValue = portfolio.getTotalValue();
        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            return new ArrayList<>();
        }

        return portfolio.getAssets().stream()
                .collect(Collectors.groupingBy(
                        Asset::getAssetType,
                        Collectors.reducing(BigDecimal.ZERO, Asset::getTotalValue, BigDecimal::add)
                ))
                .entrySet().stream()
                .map(entry -> {
                    BigDecimal percentage = entry.getValue()
                            .divide(totalValue, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(1, RoundingMode.HALF_UP);
                    return  AssetAllocation
                            .builder()
                            .category(entry.getKey().toString())
                            .value(entry.getValue())
                            .percentage(percentage)
                            .build();
                })
                .sorted((a, b) -> b.getPercentage().compareTo(a.getPercentage()))
                .collect(Collectors.toList());
    }

    private String generateSummary(Portfolio portfolio, BigDecimal diversificationScore, String riskLevel) {
        int assetCount = portfolio.getAssets().size();
        String portfolioSize = assetCount <= 3 ? "small" : assetCount <= 7 ? "medium" : "large";

        return String.format(
                "Your %s portfolio with %d assets has a diversification score of %.1f%% and %s risk level. " +
                        "The AI analysis suggests %s for optimal performance.",
                portfolioSize,
                assetCount,
                diversificationScore,
                riskLevel.toLowerCase(),
                diversificationScore.compareTo(BigDecimal.valueOf(60)) >= 0 ?
                        "maintaining current allocation with minor adjustments" :
                        "significant diversification improvements"
        );
    }

    private AIInsightResponse generateEmptyPortfolioInsights() {
        List<String> recommendations = Arrays.asList(
                "Start by adding 3-5 different stocks or ETFs",
                "Consider diversifying across different sectors",
                "Include both growth and value stocks for balance"
        );

        return AIInsightResponse
                .builder()
                .diversificationScore(BigDecimal.ZERO)
                .recommendations(recommendations)
                .riskLevel("High")
                .recommendations(recommendations)
                .summary("Your portfolio is empty. Start building a diversified portfolio by adding your first assets.")
                .build();
    }
}
