package com.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;

public class AIInsightResponse {
    private BigDecimal diversificationScore;
    private String riskLevel;
    private List<String> recommendations;
    private List<AssetAllocation> assetAllocation;
    private String summary;

    public AIInsightResponse() {}

    public AIInsightResponse(BigDecimal diversificationScore, String riskLevel,
                             List<String> recommendations, String summary) {
        this.diversificationScore = diversificationScore;
        this.riskLevel = riskLevel;
        this.recommendations = recommendations;
        this.summary = summary;
    }

    // Getters and Setters
    public BigDecimal getDiversificationScore() { return diversificationScore; }
    public void setDiversificationScore(BigDecimal diversificationScore) { this.diversificationScore = diversificationScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }

    public List<AssetAllocation> getAssetAllocation() { return assetAllocation; }
    public void setAssetAllocation(List<AssetAllocation> assetAllocation) { this.assetAllocation = assetAllocation; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public static class AssetAllocation {
        private String category;
        private BigDecimal percentage;
        private BigDecimal value;

        public AssetAllocation() {}

        public AssetAllocation(String category, BigDecimal percentage, BigDecimal value) {
            this.category = category;
            this.percentage = percentage;
            this.value = value;
        }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public BigDecimal getPercentage() { return percentage; }
        public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }

        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
    }
}
