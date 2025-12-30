package com.arthsethu.security;

import com.arthsethu.model.SubscriptionTier;
import com.arthsethu.model.User;
import com.arthsethu.service.SubscriptionService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspect to handle tier-based access control using annotations
 */
@Aspect
@Component
public class TierAccessControlAspect {
    
    @Autowired
    private SubscriptionService subscriptionService;
    
    /**
     * Check tier-based access before method execution
     */
    @Before("@annotation(requiresTier)")
    public void checkTierAccess(JoinPoint joinPoint, RequiresTier requiresTier) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new InsufficientTierException("User not authenticated", null, requiresTier.value());
        }
        
        User user = (User) authentication.getPrincipal();
        SubscriptionTier userTier = user.getTier();
        SubscriptionTier requiredTier = requiresTier.value();
        
        // Check if user has active subscription for paid tiers
        if (userTier != SubscriptionTier.AARAMBH && !subscriptionService.hasActiveSubscription(user)) {
            // Downgrade user to free tier if subscription is not active
            user.setTier(SubscriptionTier.AARAMBH);
            userTier = SubscriptionTier.AARAMBH;
        }
        
        // Check if user's tier meets the requirement
        if (userTier.getMonthlyPrice() < requiredTier.getMonthlyPrice()) {
            throw new InsufficientTierException(requiresTier.message(), userTier, requiredTier);
        }
    }
    
    /**
     * Check feature-based access before method execution
     */
    @Before("@annotation(requiresFeature)")
    public void checkFeatureAccess(JoinPoint joinPoint, RequiresFeature requiresFeature) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new InsufficientTierException("User not authenticated", null, requiresFeature.value());
        }
        
        User user = (User) authentication.getPrincipal();
        String featureName = requiresFeature.value();
        
        // Use subscription service to check feature access
        if (!subscriptionService.canAccessFeature(user, featureName)) {
            throw new InsufficientTierException(requiresFeature.message(), user.getTier(), featureName);
        }
    }
    
    /**
     * Check class-level tier requirements
     */
    @Before("@within(requiresTier) && execution(public * *(..))")
    public void checkClassTierAccess(JoinPoint joinPoint, RequiresTier requiresTier) {
        checkTierAccess(joinPoint, requiresTier);
    }
    
    /**
     * Check class-level feature requirements
     */
    @Before("@within(requiresFeature) && execution(public * *(..))")
    public void checkClassFeatureAccess(JoinPoint joinPoint, RequiresFeature requiresFeature) {
        checkFeatureAccess(joinPoint, requiresFeature);
    }
}