package com.arthsethu.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class HealthScoreDTO {
    private Integer currentHealthScore;
    private Integer previousHealthScore;
    private String trend; // "IMPROVING", "DECLINING", "STABLE"
    private BigDecimal averageMargin;
    private BigDecimal averageWastagePercentage;
    private BigDecimal marginStability; // Standard deviation of margins
    private List<DailyHealthScoreData> last30Days;
    private String recommendation;
    
    // Nested class for daily health score data
    public static class DailyHealthScoreData {
        private LocalDate date;
        private Integer healthScore;
        private BigDecimal sales;
        private BigDecimal expenses;
        private BigDecimal wastage;
        private BigDecimal margin;
        
        public DailyHealthScoreData() {}
        
        public DailyHealthScoreData(LocalDate date, Integer healthScore, BigDecimal sales, 
                                  BigDecimal expenses, BigDecimal wastage, BigDecimal margin) {
            this.date = date;
            this.healthScore = healthScore;
            this.sales = sales;
            this.expenses = expenses;
            this.wastage = wastage;
            this.margin = margin;
        }
        
        // Getters and Setters
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        
        public Integer getHealthScore() { return healthScore; }
        public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }
        
        public BigDecimal getSales() { return sales; }
        public void setSales(BigDecimal sales) { this.sales = sales; }
        
        public BigDecimal getExpenses() { return expenses; }
        public void setExpenses(BigDecimal expenses) { this.expenses = expenses; }
        
        public BigDecimal getWastage() { return wastage; }
        public void setWastage(BigDecimal wastage) { this.wastage = wastage; }
        
        public BigDecimal getMargin() { return margin; }
        public void setMargin(BigDecimal margin) { this.margin = margin; }
    }
    
    // Constructors
    public HealthScoreDTO() {}
    
    public HealthScoreDTO(Integer currentHealthScore, Integer previousHealthScore, String trend,
                         BigDecimal averageMargin, BigDecimal averageWastagePercentage,
                         BigDecimal marginStability, List<DailyHealthScoreData> last30Days,
                         String recommendation) {
        this.currentHealthScore = currentHealthScore;
        this.previousHealthScore = previousHealthScore;
        this.trend = trend;
        this.averageMargin = averageMargin;
        this.averageWastagePercentage = averageWastagePercentage;
        this.marginStability = marginStability;
        this.last30Days = last30Days;
        this.recommendation = recommendation;
    }
    
    // Getters and Setters
    public Integer getCurrentHealthScore() { return currentHealthScore; }
    public void setCurrentHealthScore(Integer currentHealthScore) { this.currentHealthScore = currentHealthScore; }
    
    public Integer getPreviousHealthScore() { return previousHealthScore; }
    public void setPreviousHealthScore(Integer previousHealthScore) { this.previousHealthScore = previousHealthScore; }
    
    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }
    
    public BigDecimal getAverageMargin() { return averageMargin; }
    public void setAverageMargin(BigDecimal averageMargin) { this.averageMargin = averageMargin; }
    
    public BigDecimal getAverageWastagePercentage() { return averageWastagePercentage; }
    public void setAverageWastagePercentage(BigDecimal averageWastagePercentage) { this.averageWastagePercentage = averageWastagePercentage; }
    
    public BigDecimal getMarginStability() { return marginStability; }
    public void setMarginStability(BigDecimal marginStability) { this.marginStability = marginStability; }
    
    public List<DailyHealthScoreData> getLast30Days() { return last30Days; }
    public void setLast30Days(List<DailyHealthScoreData> last30Days) { this.last30Days = last30Days; }
    
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}