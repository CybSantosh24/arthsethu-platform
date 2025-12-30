package com.arthsethu.dto;

import java.math.BigDecimal;
import java.util.Map;

public class WhatIfAnalysis {
    private String scenario;
    private String analysis;
    private Map<String, BigDecimal> projectedMetrics;
    private String recommendations;
    private BigDecimal impactScore; // 0-100 scale
    
    // Constructors
    public WhatIfAnalysis() {}
    
    public WhatIfAnalysis(String scenario, String analysis) {
        this.scenario = scenario;
        this.analysis = analysis;
    }
    
    // Getters and Setters
    public String getScenario() {
        return scenario;
    }
    
    public void setScenario(String scenario) {
        this.scenario = scenario;
    }
    
    public String getAnalysis() {
        return analysis;
    }
    
    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }
    
    public Map<String, BigDecimal> getProjectedMetrics() {
        return projectedMetrics;
    }
    
    public void setProjectedMetrics(Map<String, BigDecimal> projectedMetrics) {
        this.projectedMetrics = projectedMetrics;
    }
    
    public String getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }
    
    public BigDecimal getImpactScore() {
        return impactScore;
    }
    
    public void setImpactScore(BigDecimal impactScore) {
        this.impactScore = impactScore;
    }
}