package com.arthsethu.service;

import com.arthsethu.dto.HealthScoreDTO;
import com.arthsethu.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class HealthScoreService {
    
    private final DailyMetricsService dailyMetricsService;
    
    @Autowired
    public HealthScoreService(DailyMetricsService dailyMetricsService) {
        this.dailyMetricsService = dailyMetricsService;
    }
    
    /**
     * Get comprehensive health score analysis for a user
     */
    public HealthScoreDTO getHealthScoreAnalysis(User user) {
        return dailyMetricsService.getHealthScoreAnalysis(user);
    }
    
    /**
     * Calculate health score for given metrics
     */
    public Integer calculateHealthScore(BigDecimal sales, BigDecimal expenses, BigDecimal wastage) {
        return dailyMetricsService.calculateHealthScore(sales, expenses, wastage);
    }
    
    /**
     * Check if user has logged metrics today
     */
    public boolean hasLoggedToday(User user) {
        return dailyMetricsService.hasLoggedToday(user);
    }
}