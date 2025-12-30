package com.arthsethu.service;

import com.arthsethu.dto.WhatIfAnalysis;
import com.arthsethu.model.BusinessProfile;
import com.arthsethu.model.DailyMetrics;
import com.arthsethu.repository.DailyMetricsRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AICFOService {
    
    private final ChatModel chatModel;
    private final DailyMetricsRepository dailyMetricsRepository;
    
    @Autowired
    public AICFOService(ChatModel chatModel, DailyMetricsRepository dailyMetricsRepository) {
        this.chatModel = chatModel;
        this.dailyMetricsRepository = dailyMetricsRepository;
    }
    
    /**
     * Process a user query with contextual awareness of their business data
     */
    public String processQuery(String query, BusinessProfile profile, List<DailyMetrics> history) {
        String context = buildBusinessContext(profile, history);
        
        String promptContent = String.format("""
            You are an AI CFO for ArthSethu, a financial intelligence platform. 
            You provide strategic financial guidance based on real business data.
            
            Business Context:
            %s
            
            User Query: %s
            
            Provide a detailed, quantitative analysis with specific recommendations. 
            Focus on actionable insights based on the business data provided.
            Include relevant financial metrics and calculations where appropriate.
            """, context, query);
        
        Prompt prompt = new Prompt(promptContent);
        
        try {
            var result = chatModel.call(prompt);
            return result.getResult().getOutput().toString();
        } catch (Exception e) {
            return "I apologize, but I'm currently unable to process your request. " +
                   "Please try again later or contact support if the issue persists. " +
                   "Error: " + e.getMessage();
        }
    }
    
    /**
     * Perform what-if simulation analysis
     */
    public WhatIfAnalysis performSimulation(String scenario, BusinessProfile profile) {
        List<DailyMetrics> recentMetrics = dailyMetricsRepository
            .findLast30DaysMetrics(profile.getUser(), java.time.LocalDate.now().minusDays(30));
        
        String context = buildBusinessContext(profile, recentMetrics);
        
        String promptContent = String.format("""
            You are an AI CFO performing a what-if analysis for ArthSethu.
            
            Business Context:
            %s
            
            Scenario to Analyze: %s
            
            Provide a structured analysis including:
            1. Impact assessment on key metrics (sales, expenses, profit margin)
            2. Quantitative projections where possible
            3. Risk factors and mitigation strategies
            4. Specific recommendations
            5. Overall impact score (0-100 scale)
            
            Format your response as a detailed financial analysis.
            """, context, scenario);
        
        Prompt prompt = new Prompt(promptContent);
        
        WhatIfAnalysis analysis = new WhatIfAnalysis();
        analysis.setScenario(scenario);
        
        try {
            var result = chatModel.call(prompt);
            String response = result.getResult().getOutput().toString();
            analysis.setAnalysis(response);
            
            // Extract projected metrics and impact score from response
            analysis.setProjectedMetrics(extractProjectedMetrics(response, recentMetrics));
            analysis.setImpactScore(extractImpactScore(response));
            analysis.setRecommendations(extractRecommendations(response));
            
        } catch (Exception e) {
            analysis.setAnalysis("Unable to perform simulation analysis at this time. " +
                               "Please try again later. Error: " + e.getMessage());
            analysis.setImpactScore(BigDecimal.ZERO);
        }
        
        return analysis;
    }
    
    /**
     * Build comprehensive business context for AI queries
     */
    private String buildBusinessContext(BusinessProfile profile, List<DailyMetrics> history) {
        StringBuilder context = new StringBuilder();
        
        // Business profile information
        context.append("Business Type: ").append(profile.getBusinessType()).append("\n");
        context.append("Location: ").append(profile.getCity()).append("\n");
        
        if (profile.getSeatingCapacity() != null) {
            context.append("Seating Capacity: ").append(profile.getSeatingCapacity()).append("\n");
        }
        if (profile.getPackagingCosts() != null) {
            context.append("Packaging Costs: ₹").append(profile.getPackagingCosts()).append("\n");
        }
        if (profile.getPowerConsumption() != null) {
            context.append("Power Consumption: ").append(profile.getPowerConsumption()).append(" kWh\n");
        }
        if (profile.getRawMaterialSourcing() != null) {
            context.append("Raw Material Sourcing: ").append(profile.getRawMaterialSourcing()).append("\n");
        }
        
        // Recent performance metrics
        if (!history.isEmpty()) {
            context.append("\nRecent Performance (Last 30 days):\n");
            
            BigDecimal avgSales = history.stream()
                .map(DailyMetrics::getSales)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(history.size()), 2, RoundingMode.HALF_UP);
            
            BigDecimal avgExpenses = history.stream()
                .map(DailyMetrics::getExpenses)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(history.size()), 2, RoundingMode.HALF_UP);
            
            BigDecimal avgWastage = history.stream()
                .map(DailyMetrics::getWastage)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(history.size()), 2, RoundingMode.HALF_UP);
            
            Double avgHealthScore = history.stream()
                .mapToInt(DailyMetrics::getHealthScore)
                .average()
                .orElse(0.0);
            
            context.append("Average Daily Sales: ₹").append(avgSales).append("\n");
            context.append("Average Daily Expenses: ₹").append(avgExpenses).append("\n");
            context.append("Average Daily Wastage: ₹").append(avgWastage).append("\n");
            context.append("Average Health Score: ").append(String.format("%.1f", avgHealthScore)).append("/100\n");
            
            BigDecimal avgProfit = avgSales.subtract(avgExpenses);
            BigDecimal profitMargin = avgSales.compareTo(BigDecimal.ZERO) > 0 ? 
                avgProfit.divide(avgSales, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) : 
                BigDecimal.ZERO;
            
            context.append("Average Profit Margin: ").append(profitMargin).append("%\n");
        }
        
        return context.toString();
    }
    
    /**
     * Extract projected metrics from AI response (simplified implementation)
     */
    private Map<String, BigDecimal> extractProjectedMetrics(String response, List<DailyMetrics> recentMetrics) {
        Map<String, BigDecimal> metrics = new HashMap<>();
        
        // This is a simplified implementation - in a real system, you might use
        // more sophisticated NLP or structured prompts to extract specific metrics
        if (!recentMetrics.isEmpty()) {
            BigDecimal avgSales = recentMetrics.stream()
                .map(DailyMetrics::getSales)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(recentMetrics.size()), 2, RoundingMode.HALF_UP);
            
            // Apply a simple projection based on response sentiment
            BigDecimal projectionMultiplier = response.toLowerCase().contains("increase") || 
                                            response.toLowerCase().contains("improve") ? 
                                            BigDecimal.valueOf(1.1) : BigDecimal.valueOf(0.95);
            
            metrics.put("projected_sales", avgSales.multiply(projectionMultiplier));
            metrics.put("projected_expenses", avgSales.multiply(BigDecimal.valueOf(0.7)));
        }
        
        return metrics;
    }
    
    /**
     * Extract impact score from AI response (simplified implementation)
     */
    private BigDecimal extractImpactScore(String response) {
        // Simplified scoring based on response content
        String lowerResponse = response.toLowerCase();
        
        if (lowerResponse.contains("high impact") || lowerResponse.contains("significant")) {
            return BigDecimal.valueOf(85);
        } else if (lowerResponse.contains("moderate") || lowerResponse.contains("medium")) {
            return BigDecimal.valueOf(60);
        } else if (lowerResponse.contains("low") || lowerResponse.contains("minimal")) {
            return BigDecimal.valueOf(30);
        }
        
        return BigDecimal.valueOf(50); // Default moderate impact
    }
    
    /**
     * Extract recommendations from AI response (simplified implementation)
     */
    private String extractRecommendations(String response) {
        // Look for recommendation sections in the response
        String[] lines = response.split("\n");
        StringBuilder recommendations = new StringBuilder();
        
        boolean inRecommendationSection = false;
        for (String line : lines) {
            if (line.toLowerCase().contains("recommendation") || 
                line.toLowerCase().contains("suggest") ||
                line.toLowerCase().contains("action")) {
                inRecommendationSection = true;
            }
            
            if (inRecommendationSection && !line.trim().isEmpty()) {
                recommendations.append(line.trim()).append("\n");
            }
        }
        
        return recommendations.length() > 0 ? recommendations.toString() : 
               "Continue monitoring your metrics and consider the analysis provided.";
    }
    
    /**
     * Check if AI service is available
     */
    public boolean isAIServiceAvailable() {
        try {
            Prompt testPrompt = new Prompt("Health check");
            var result = chatModel.call(testPrompt);
            String response = result.getResult().getOutput().toString();
            return response != null && !response.trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}