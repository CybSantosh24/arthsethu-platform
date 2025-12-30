package com.arthsethu.repository;

import com.arthsethu.model.Subscription;
import com.arthsethu.model.SubscriptionStatus;
import com.arthsethu.model.SubscriptionTier;
import com.arthsethu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    /**
     * Find subscription by user
     */
    Optional<Subscription> findByUser(User user);
    
    /**
     * Find subscription by user ID
     */
    Optional<Subscription> findByUserId(Long userId);
    
    /**
     * Find subscriptions by tier
     */
    List<Subscription> findByTier(SubscriptionTier tier);
    
    /**
     * Find subscriptions by status
     */
    List<Subscription> findByStatus(SubscriptionStatus status);
    
    /**
     * Find active subscriptions
     */
    List<Subscription> findByStatusAndEndDateAfter(SubscriptionStatus status, LocalDateTime currentDate);
    
    /**
     * Find expired subscriptions
     */
    List<Subscription> findByStatusAndEndDateBefore(SubscriptionStatus status, LocalDateTime currentDate);
    
    /**
     * Find subscriptions expiring soon (for renewal notifications)
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = :status AND s.endDate BETWEEN :now AND :expiryThreshold")
    List<Subscription> findSubscriptionsExpiringSoon(
        @Param("status") SubscriptionStatus status,
        @Param("now") LocalDateTime now,
        @Param("expiryThreshold") LocalDateTime expiryThreshold
    );
    
    /**
     * Count active subscriptions by tier
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.tier = :tier AND s.status = :status")
    long countActiveSubscriptionsByTier(@Param("tier") SubscriptionTier tier, @Param("status") SubscriptionStatus status);
    
    /**
     * Get total revenue by tier within date range
     */
    @Query("SELECT SUM(s.amount) FROM Subscription s WHERE s.tier = :tier AND s.status = :status AND s.startDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueByTierAndDateRange(
        @Param("tier") SubscriptionTier tier,
        @Param("status") SubscriptionStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Get total revenue within date range (for admin dashboard)
     */
    @Query("SELECT SUM(s.amount) FROM Subscription s WHERE s.status = :status AND s.startDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueByDateRange(
        @Param("status") SubscriptionStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Find subscriptions with auto-renewal enabled
     */
    List<Subscription> findByAutoRenewTrue();
    
    /**
     * Find subscriptions by payment reference
     */
    Optional<Subscription> findByPaymentReference(String paymentReference);
    
    /**
     * Find subscriptions created within date range
     */
    List<Subscription> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count total paid subscriptions (excluding free tier)
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.tier != :freeTier AND s.status = :status")
    long countPaidSubscriptions(@Param("freeTier") SubscriptionTier freeTier, @Param("status") SubscriptionStatus status);
    
    /**
     * Get monthly recurring revenue (MRR)
     */
    @Query("SELECT SUM(s.amount) FROM Subscription s WHERE s.status = :status AND s.tier != :freeTier")
    BigDecimal getMonthlyRecurringRevenue(@Param("status") SubscriptionStatus status, @Param("freeTier") SubscriptionTier freeTier);
    
    /**
     * Find subscriptions by tier and status
     */
    List<Subscription> findByTierAndStatus(SubscriptionTier tier, SubscriptionStatus status);
    
    /**
     * Check if user has active subscription
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Subscription s WHERE s.user = :user AND s.status = :status AND (s.endDate IS NULL OR s.endDate > :currentDate)")
    boolean hasActiveSubscription(@Param("user") User user, @Param("status") SubscriptionStatus status, @Param("currentDate") LocalDateTime currentDate);
}