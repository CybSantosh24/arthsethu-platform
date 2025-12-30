package com.arthsethu.repository;

import com.arthsethu.model.User;
import com.arthsethu.model.SubscriptionTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email address
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users by subscription tier
     */
    List<User> findByTier(SubscriptionTier tier);
    
    /**
     * Count users by subscription tier
     */
    long countByTier(SubscriptionTier tier);
    
    /**
     * Find users created after a specific date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find users with active subscriptions (for admin dashboard)
     */
    @Query("SELECT u FROM User u WHERE u.tier != :freeTier")
    List<User> findUsersWithPaidSubscriptions(@Param("freeTier") SubscriptionTier freeTier);
    
    /**
     * Count total active users across all tiers
     */
    @Query("SELECT COUNT(u) FROM User u")
    long countTotalUsers();
    
    /**
     * Find users who have completed business profiles
     */
    @Query("SELECT u FROM User u WHERE u.businessProfile IS NOT NULL")
    List<User> findUsersWithBusinessProfiles();
    
    /**
     * Find users by tier and creation date range (for analytics)
     */
    @Query("SELECT u FROM User u WHERE u.tier = :tier AND u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findByTierAndCreatedAtBetween(
        @Param("tier") SubscriptionTier tier,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}