package com.arthsethu.service;

import com.arthsethu.dto.HealthScoreDTO;
import com.arthsethu.model.DailyMetrics;
import com.arthsethu.model.User;
import com.arthsethu.repository.DailyMetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DailyMetricsService {
    
    private final DailyMetricsRepository dailyMetricsRepository;
    
    @Autowired
    public DailyMetricsService(DailyMetricsRepository dailyMetricsRepository) {
        this.dailyMetricsRepository = dailyMetricsRepository;
    }
    
    /**
     * Save or update daily metrics for a user
     */
    public DailyMetrics saveDailyMetrics(User user, LocalDate date, BigDecimal sales, 
                                       BigDecimal expenses, BigDecimal wastage) {
        Optional<DailyMetrics> existingMetrics = dailyMetricsRepository.findByUserAndDate(user, date);
        
        DailyMetrics metrics;
        if (existingMetrics.isPresent()) {
            metrics = existingMetrics.get();
            metrics.setSales(sales);
            metrics.setExpenses(expenses);
            metrics.setWastage(wastage);
        } else {
            metrics = new DailyMetrics(user, date, sales, expenses, wastage);
        }
        
        return dailyMetricsRepository.save(metrics);
    }
    
    /**
     * Get comprehensive health score analysis for a user
     */
    @Transactional(readOnly = true)
    public HealthScoreDTO getHealthScoreAnalysis(User user) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<DailyMetrics> last30Days = dailyMetricsRepository.findLast30DaysMetrics(user, thirtyDaysAgo);
        
        if (last30Days.isEmpty()) {
            return createEmptyHealthScoreDTO();
        }
        
        // Get current and previous health scores
        Integer currentHealthScore = last30Days.get(0).getHealthScore();
        Integer previousHealthScore = last30Days.size() > 1 ? last30Days.get(1).getHealthScore() : null;
        
        // Calculate trend
        String trend = calculateTrend(last30Days);
        
        // Calculate average margin and wastage
        BigDecimal averageMargin = calculateAverageMargin(last30Days);
        BigDecimal averageWastagePercentage = calculateAverageWastagePercentage(last30Days);
        
        // Calculate margin stability (standard deviation)
        BigDecimal marginStability = calculateMarginStability(last30Days);
        
        // Convert to DTO format
        List<HealthScoreDTO.DailyHealthScoreData> dailyData = last30Days.stream()
            .map(this::convertToDailyHealthScoreData)
            .collect(Collectors.toList());
        
        // Generate recommendation
        String recommendation = generateRecommendation(currentHealthScore, trend, averageMargin, 
                                                     averageWastagePercentage, marginStability);
        
        return new HealthScoreDTO(currentHealthScore, previousHealthScore, trend, averageMargin,
                                averageWastagePercentage, marginStability, dailyData, recommendation);
    }
    
    /**
     * Calculate health score for given metrics (0-100 range)
     */
    public Integer calculateHealthScore(BigDecimal sales, BigDecimal expenses, BigDecimal wastage) {
        if (sales.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        
        BigDecimal profit = sales.subtract(expenses);
        BigDecimal margin = profit.divide(sales, 4, RoundingMode.HALF_UP);
        BigDecimal wastagePercent = wastage.divide(sales, 4, RoundingMode.HALF_UP);
        
        // Base score from margin (0-70 points)
        // Healthy margin is considered 20% (0.2), so we scale accordingly
        int marginScore = Math.min(70, margin.multiply(BigDecimal.valueOf(350)).intValue());
        
        // Penalty from wastage (0-30 points deduction)
        // Wastage above 10% (0.1) gets maximum penalty
        int wastagePenalty = Math.min(30, wastagePercent.multiply(BigDecimal.valueOf(300)).intValue());
        
        return Math.max(0, Math.min(100, marginScore - wastagePenalty));
    }
    
    /**
     * Get 30-day trend analysis for a user
     */
    @Transactional(readOnly = true)
    public List<DailyMetrics> get30DayTrend(User user) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        return dailyMetricsRepository.findLast30DaysMetrics(user, thirtyDaysAgo);
    }
    
    /**
     * Check if user has logged metrics today
     */
    @Transactional(readOnly = true)
    public boolean hasLoggedToday(User user) {
        return dailyMetricsRepository.existsByUserAndDate(user, LocalDate.now());
    }
    
    /**
     * Get latest metrics for a user
     */
    @Transactional(readOnly = true)
    public Optional<DailyMetrics> getLatestMetrics(User user) {
        return dailyMetricsRepository.findTopByUserOrderByDateDesc(user);
    }
    
    // Private helper methods
    
    private HealthScoreDTO createEmptyHealthScoreDTO() {
        return new HealthScoreDTO(0, null, "NO_DATA", BigDecimal.ZERO, BigDecimal.ZERO,
                                BigDecimal.ZERO, List.of(), "Start logging daily metrics to see your health score analysis.");
    }
    
    private String calculateTrend(List<DailyMetrics> metrics) {
        if (metrics.size() < 7) {
            return "INSUFFICIENT_DATA";
        }
        
        // Compare average of last 7 days with previous 7 days
        List<DailyMetrics> lastWeek = metrics.subList(0, Math.min(7, metrics.size()));
        List<DailyMetrics> previousWeek = metrics.subList(Math.min(7, metrics.size()), 
                                                         Math.min(14, metrics.size()));
        
        if (previousWeek.isEmpty()) {
            return "INSUFFICIENT_DATA";
        }
        
        double lastWeekAvg = lastWeek.stream()
            .mapToInt(DailyMetrics::getHealthScore)
            .average()
            .orElse(0.0);
        
        double previousWeekAvg = previousWeek.stream()
            .mapToInt(DailyMetrics::getHealthScore)
            .average()
            .orElse(0.0);
        
        double difference = lastWeekAvg - previousWeekAvg;
        
        if (difference > 5) {
            return "IMPROVING";
        } else if (difference < -5) {
            return "DECLINING";
        } else {
            return "STABLE";
        }
    }
    
    private BigDecimal calculateAverageMargin(List<DailyMetrics> metrics) {
        return metrics.stream()
            .map(DailyMetrics::getMargin)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(metrics.size()), 4, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateAverageWastagePercentage(List<DailyMetrics> metrics) {
        return metrics.stream()
            .map(DailyMetrics::getWastagePercentage)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(metrics.size()), 4, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateMarginStability(List<DailyMetrics> metrics) {
        if (metrics.size() < 2) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal averageMargin = calculateAverageMargin(metrics);
        
        // Calculate standard deviation
        BigDecimal sumOfSquaredDifferences = metrics.stream()
            .map(DailyMetrics::getMargin)
            .map(margin -> margin.subtract(averageMargin).pow(2))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal variance = sumOfSquaredDifferences.divide(BigDecimal.valueOf(metrics.size()), 6, RoundingMode.HALF_UP);
        
        // Return standard deviation as a measure of stability (lower is more stable)
        return BigDecimal.valueOf(Math.sqrt(variance.doubleValue())).setScale(4, RoundingMode.HALF_UP);
    }
    
    private HealthScoreDTO.DailyHealthScoreData convertToDailyHealthScoreData(DailyMetrics metrics) {
        return new HealthScoreDTO.DailyHealthScoreData(
            metrics.getDate(),
            metrics.getHealthScore(),
            metrics.getSales(),
            metrics.getExpenses(),
            metrics.getWastage(),
            metrics.getMargin()
        );
    }
    
    private String generateRecommendation(Integer currentHealthScore, String trend, 
                                        BigDecimal averageMargin, BigDecimal averageWastagePercentage,
                                        BigDecimal marginStability) {
        StringBuilder recommendation = new StringBuilder();
        
        if (currentHealthScore < 30) {
            recommendation.append("Critical: Your business health needs immediate attention. ");
        } else if (currentHealthScore < 60) {
            recommendation.append("Warning: Your business health is below optimal levels. ");
        } else if (currentHealthScore < 80) {
            recommendation.append("Good: Your business is performing well with room for improvement. ");
        } else {
            recommendation.append("Excellent: Your business is performing exceptionally well. ");
        }
        
        if ("DECLINING".equals(trend)) {
            recommendation.append("Your health score is declining - focus on reducing expenses and wastage. ");
        } else if ("IMPROVING".equals(trend)) {
            recommendation.append("Great job! Your health score is improving. ");
        }
        
        if (averageWastagePercentage.compareTo(BigDecimal.valueOf(0.1)) > 0) {
            recommendation.append("Consider implementing wastage reduction strategies. ");
        }
        
        if (averageMargin.compareTo(BigDecimal.valueOf(0.15)) < 0) {
            recommendation.append("Work on improving your profit margins through cost optimization or pricing adjustments. ");
        }
        
        if (marginStability.compareTo(BigDecimal.valueOf(0.05)) > 0) {
            recommendation.append("Focus on stabilizing your daily performance for consistent results. ");
        }
        
        return recommendation.toString().trim();
    }
}