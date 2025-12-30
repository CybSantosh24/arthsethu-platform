package com.arthsethu.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_metrics", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "metric_date"}),
       indexes = @Index(name = "idx_daily_metrics_user_date", columnList = "user_id, metric_date"))
public class DailyMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "metric_date", nullable = false)
    private LocalDate date;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal sales;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal expenses;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal wastage;
    
    @Column(name = "health_score")
    private Integer healthScore;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public DailyMetrics() {
        this.createdAt = LocalDateTime.now();
    }
    
    public DailyMetrics(User user, LocalDate date, BigDecimal sales, BigDecimal expenses, BigDecimal wastage) {
        this();
        this.user = user;
        this.date = date;
        this.sales = sales;
        this.expenses = expenses;
        this.wastage = wastage;
        this.healthScore = calculateHealthScore();
    }
    
    // Business logic methods
    public BigDecimal getProfit() {
        return sales.subtract(expenses);
    }
    
    public BigDecimal getMargin() {
        if (sales.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getProfit().divide(sales, 4, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getWastagePercentage() {
        if (sales.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return wastage.divide(sales, 4, RoundingMode.HALF_UP);
    }
    
    private Integer calculateHealthScore() {
        // Basic health score calculation based on margin and wastage
        BigDecimal margin = getMargin();
        BigDecimal wastagePercent = getWastagePercentage();
        
        // Base score from margin (0-70 points)
        int marginScore = Math.min(70, margin.multiply(BigDecimal.valueOf(350)).intValue());
        
        // Penalty from wastage (0-30 points deduction)
        int wastagePenalty = Math.min(30, wastagePercent.multiply(BigDecimal.valueOf(300)).intValue());
        
        return Math.max(0, Math.min(100, marginScore - wastagePenalty));
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public BigDecimal getSales() {
        return sales;
    }
    
    public void setSales(BigDecimal sales) {
        this.sales = sales;
        this.healthScore = calculateHealthScore();
    }
    
    public BigDecimal getExpenses() {
        return expenses;
    }
    
    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
        this.healthScore = calculateHealthScore();
    }
    
    public BigDecimal getWastage() {
        return wastage;
    }
    
    public void setWastage(BigDecimal wastage) {
        this.wastage = wastage;
        this.healthScore = calculateHealthScore();
    }
    
    public Integer getHealthScore() {
        return healthScore;
    }
    
    public void setHealthScore(Integer healthScore) {
        this.healthScore = healthScore;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}