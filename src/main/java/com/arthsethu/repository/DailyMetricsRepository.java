package com.arthsethu.repository;

import com.arthsethu.model.DailyMetrics;
import com.arthsethu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyMetricsRepository extends JpaRepository<DailyMetrics, Long> {
    
    /**
     * Find metrics by user
     */
    List<DailyMetrics> findByUser(User user);
    
    /**
     * Find metrics by user ID
     */
    List<DailyMetrics> findByUserId(Long userId);
    
    /**
     * Find metrics by user and date
     */
    Optional<DailyMetrics> findByUserAndDate(User user, LocalDate date);
    
    /**
     * Find metrics by user ID and date
     */
    Optional<DailyMetrics> findByUserIdAndDate(Long userId, LocalDate date);
    
    /**
     * Find metrics by user within date range (for trend analysis)
     */
    List<DailyMetrics> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find metrics by user ID within date range
     */
    List<DailyMetrics> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get last 30 days metrics for a user (for health score trending)
     */
    @Query("SELECT dm FROM DailyMetrics dm WHERE dm.user = :user AND dm.date >= :thirtyDaysAgo ORDER BY dm.date DESC")
    List<DailyMetrics> findLast30DaysMetrics(@Param("user") User user, @Param("thirtyDaysAgo") LocalDate thirtyDaysAgo);
    
    /**
     * Get average health score for a user over a period
     */
    @Query("SELECT AVG(dm.healthScore) FROM DailyMetrics dm WHERE dm.user = :user AND dm.date BETWEEN :startDate AND :endDate")
    Double getAverageHealthScore(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Get total sales for a user over a period
     */
    @Query("SELECT SUM(dm.sales) FROM DailyMetrics dm WHERE dm.user = :user AND dm.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalSales(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Get total expenses for a user over a period
     */
    @Query("SELECT SUM(dm.expenses) FROM DailyMetrics dm WHERE dm.user = :user AND dm.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalExpenses(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Get total wastage for a user over a period
     */
    @Query("SELECT SUM(dm.wastage) FROM DailyMetrics dm WHERE dm.user = :user AND dm.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalWastage(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Find metrics with health score below threshold (for alerts)
     */
    @Query("SELECT dm FROM DailyMetrics dm WHERE dm.user = :user AND dm.healthScore < :threshold ORDER BY dm.date DESC")
    List<DailyMetrics> findMetricsWithLowHealthScore(@Param("user") User user, @Param("threshold") Integer threshold);
    
    /**
     * Check if metrics exist for user on specific date
     */
    boolean existsByUserAndDate(User user, LocalDate date);
    
    /**
     * Get latest metrics for a user
     */
    Optional<DailyMetrics> findTopByUserOrderByDateDesc(User user);
    
    /**
     * Count total metrics entries for a user
     */
    long countByUser(User user);
}