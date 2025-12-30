package com.arthsethu.service;

import com.arthsethu.model.*;
import com.arthsethu.repository.SubscriptionRepository;
import com.arthsethu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SubscriptionService {
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Create a new subscription for a user
     */
    public Subscription createSubscription(User user, SubscriptionTier tier) {
        // Check if user already has a subscription
        Optional<Subscription> existingSubscription = subscriptionRepository.findByUser(user);
        if (existingSubscription.isPresent()) {
            throw new IllegalStateException("User already has a subscription");
        }
        
        Subscription subscription = new Subscription(user, tier);
        
        // For free tier, activate immediately
        if (tier == SubscriptionTier.AARAMBH) {
            subscription.activate();
        }
        
        subscription = subscriptionRepository.save(subscription);
        
        // Update user's tier
        user.setTier(tier);
        user.setSubscription(subscription);
        userRepository.save(user);
        
        return subscription;
    }
    
    /**
     * Upgrade user's subscription to a higher tier
     */
    public Subscription upgradeSubscription(User user, SubscriptionTier newTier) {
        Optional<Subscription> currentSubscriptionOpt = subscriptionRepository.findByUser(user);
        
        if (currentSubscriptionOpt.isEmpty()) {
            // Create new subscription if none exists
            return createSubscription(user, newTier);
        }
        
        Subscription currentSubscription = currentSubscriptionOpt.get();
        
        // Validate upgrade
        if (!currentSubscription.canUpgradeTo(newTier)) {
            throw new IllegalArgumentException("Cannot upgrade to the specified tier");
        }
        
        // Cancel current subscription
        currentSubscription.cancel();
        subscriptionRepository.save(currentSubscription);
        
        // Create new subscription with higher tier
        Subscription newSubscription = new Subscription(user, newTier);
        newSubscription.setStatus(SubscriptionStatus.PENDING); // Will be activated after payment
        newSubscription = subscriptionRepository.save(newSubscription);
        
        // Update user's subscription reference
        user.setSubscription(newSubscription);
        userRepository.save(user);
        
        return newSubscription;
    }
    
    /**
     * Simulate payment processing and activate subscription
     */
    public boolean processPayment(Long subscriptionId, String paymentMethod) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
        
        if (subscriptionOpt.isEmpty()) {
            return false;
        }
        
        Subscription subscription = subscriptionOpt.get();
        
        // Simulate payment processing
        boolean paymentSuccess = simulatePayment(subscription.getAmount(), paymentMethod);
        
        if (paymentSuccess) {
            // Generate payment reference
            String paymentReference = generatePaymentReference();
            subscription.setPaymentReference(paymentReference);
            
            // Activate subscription
            subscription.activate();
            
            // Update user's tier
            User user = subscription.getUser();
            user.setTier(subscription.getTier());
            userRepository.save(user);
            
            subscriptionRepository.save(subscription);
            return true;
        }
        
        return false;
    }
    
    /**
     * Simulate payment processing (for demo purposes)
     */
    private boolean simulatePayment(BigDecimal amount, String paymentMethod) {
        // Simulate payment processing delay
        try {
            Thread.sleep(1000); // 1 second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate 95% success rate
        return Math.random() < 0.95;
    }
    
    /**
     * Generate unique payment reference
     */
    private String generatePaymentReference() {
        return "PAY_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    
    /**
     * Cancel subscription
     */
    public void cancelSubscription(User user) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUser(user);
        
        if (subscriptionOpt.isPresent()) {
            Subscription subscription = subscriptionOpt.get();
            subscription.cancel();
            subscriptionRepository.save(subscription);
            
            // Downgrade user to free tier
            user.setTier(SubscriptionTier.AARAMBH);
            userRepository.save(user);
        }
    }
    
    /**
     * Suspend subscription
     */
    public void suspendSubscription(User user) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUser(user);
        
        if (subscriptionOpt.isPresent()) {
            Subscription subscription = subscriptionOpt.get();
            subscription.suspend();
            subscriptionRepository.save(subscription);
            
            // Downgrade user to free tier
            user.setTier(SubscriptionTier.AARAMBH);
            userRepository.save(user);
        }
    }
    
    /**
     * Reactivate suspended subscription
     */
    public boolean reactivateSubscription(User user) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUser(user);
        
        if (subscriptionOpt.isPresent()) {
            Subscription subscription = subscriptionOpt.get();
            
            if (subscription.getStatus().canBeActivated()) {
                subscription.activate();
                subscriptionRepository.save(subscription);
                
                // Update user's tier
                user.setTier(subscription.getTier());
                userRepository.save(user);
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get user's current subscription
     */
    public Optional<Subscription> getUserSubscription(User user) {
        return subscriptionRepository.findByUser(user);
    }
    
    /**
     * Check if user has active subscription
     */
    public boolean hasActiveSubscription(User user) {
        return subscriptionRepository.hasActiveSubscription(user, SubscriptionStatus.ACTIVE, LocalDateTime.now());
    }
    
    /**
     * Check if user can access a specific feature
     */
    public boolean canAccessFeature(User user, String featureName) {
        // Check user's current tier
        SubscriptionTier userTier = user.getTier();
        
        // Verify subscription is active for paid tiers
        if (userTier != SubscriptionTier.AARAMBH) {
            if (!hasActiveSubscription(user)) {
                // Downgrade to free tier if subscription is not active
                user.setTier(SubscriptionTier.AARAMBH);
                userRepository.save(user);
                userTier = SubscriptionTier.AARAMBH;
            }
        }
        
        return userTier.allowsFeature(featureName);
    }
    
    /**
     * Get subscription pricing for tier
     */
    public BigDecimal getTierPrice(SubscriptionTier tier) {
        return BigDecimal.valueOf(tier.getMonthlyPrice());
    }
    
    /**
     * Get all available tiers with pricing
     */
    public SubscriptionTier[] getAvailableTiers() {
        return SubscriptionTier.values();
    }
    
    /**
     * Process subscription renewals (for scheduled tasks)
     */
    public void processRenewals() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime renewalThreshold = now.plusDays(3); // 3 days before expiry
        
        List<Subscription> expiringSoon = subscriptionRepository.findSubscriptionsExpiringSoon(
            SubscriptionStatus.ACTIVE, now, renewalThreshold);
        
        for (Subscription subscription : expiringSoon) {
            if (subscription.getAutoRenew()) {
                // Process auto-renewal
                boolean renewed = processPayment(subscription.getId(), "auto_renewal");
                if (renewed) {
                    // Extend subscription by 30 days
                    subscription.setEndDate(subscription.getEndDate().plusDays(30));
                    subscriptionRepository.save(subscription);
                }
            }
        }
    }
    
    /**
     * Get subscription statistics for admin dashboard
     */
    public SubscriptionStats getSubscriptionStats() {
        long aarambhCount = subscriptionRepository.countActiveSubscriptionsByTier(
            SubscriptionTier.AARAMBH, SubscriptionStatus.ACTIVE);
        long vistarCount = subscriptionRepository.countActiveSubscriptionsByTier(
            SubscriptionTier.VISTAR, SubscriptionStatus.ACTIVE);
        long shikharCount = subscriptionRepository.countActiveSubscriptionsByTier(
            SubscriptionTier.SHIKHAR, SubscriptionStatus.ACTIVE);
        
        BigDecimal monthlyRevenue = subscriptionRepository.getMonthlyRecurringRevenue(
            SubscriptionStatus.ACTIVE, SubscriptionTier.AARAMBH);
        
        return new SubscriptionStats(aarambhCount, vistarCount, shikharCount, 
                                   monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO);
    }
    
    /**
     * Get revenue for date range
     */
    public BigDecimal getRevenueForDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal revenue = subscriptionRepository.getTotalRevenueByDateRange(
            SubscriptionStatus.ACTIVE, startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
    
    /**
     * Inner class for subscription statistics
     */
    public static class SubscriptionStats {
        private final long aarambhCount;
        private final long vistarCount;
        private final long shikharCount;
        private final BigDecimal monthlyRevenue;
        
        public SubscriptionStats(long aarambhCount, long vistarCount, long shikharCount, BigDecimal monthlyRevenue) {
            this.aarambhCount = aarambhCount;
            this.vistarCount = vistarCount;
            this.shikharCount = shikharCount;
            this.monthlyRevenue = monthlyRevenue;
        }
        
        public long getAarambhCount() { return aarambhCount; }
        public long getVistarCount() { return vistarCount; }
        public long getShikharCount() { return shikharCount; }
        public BigDecimal getMonthlyRevenue() { return monthlyRevenue; }
        public long getTotalUsers() { return aarambhCount + vistarCount + shikharCount; }
        public long getPaidUsers() { return vistarCount + shikharCount; }
    }
}