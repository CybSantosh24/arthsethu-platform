package com.arthsethu.security;

import com.arthsethu.model.SubscriptionTier;

/**
 * Exception thrown when user's subscription tier is insufficient for accessing a feature
 */
public class InsufficientTierException extends RuntimeException {
    
    private final SubscriptionTier currentTier;
    private final SubscriptionTier requiredTier;
    private final String featureName;
    
    public InsufficientTierException(String message, SubscriptionTier currentTier, SubscriptionTier requiredTier) {
        super(message);
        this.currentTier = currentTier;
        this.requiredTier = requiredTier;
        this.featureName = null;
    }
    
    public InsufficientTierException(String message, SubscriptionTier currentTier, String featureName) {
        super(message);
        this.currentTier = currentTier;
        this.requiredTier = null;
        this.featureName = featureName;
    }
    
    public SubscriptionTier getCurrentTier() {
        return currentTier;
    }
    
    public SubscriptionTier getRequiredTier() {
        return requiredTier;
    }
    
    public String getFeatureName() {
        return featureName;
    }
}