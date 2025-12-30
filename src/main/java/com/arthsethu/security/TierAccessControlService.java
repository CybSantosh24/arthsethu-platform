package com.arthsethu.security;

import com.arthsethu.model.SubscriptionTier;
import com.arthsethu.model.User;
import com.arthsethu.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service to provide tier-based access control utilities
 */
@Service
public class TierAccessControlService {
    
    @Autowired
    private SubscriptionService subscriptionService;
    
    /**
     * Check if current user has access to a specific tier
     */
    public boolean hasAccessToTier(SubscriptionTier requiredTier) {
        User user = getCurrentUser();
        if (user == null) {
            return false;
        }
        
        return user.getTier().getMonthlyPrice() >= requiredTier.getMonthlyPrice() &&
               subscriptionService.hasActiveSubscription(user);
    }
    
    /**
     * Check if current user has access to a specific feature
     */
    public boolean hasAccessToFeature(String featureName) {
        User user = getCurrentUser();
        if (user == null) {
            return false;
        }
        
        return subscriptionService.canAccessFeature(user, featureName);
    }
    
    /**
     * Get current authenticated user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        
        return null;
    }
    
    /**
     * Get current user's tier
     */
    public SubscriptionTier getCurrentUserTier() {
        User user = getCurrentUser();
        return user != null ? user.getTier() : SubscriptionTier.AARAMBH;
    }
    
    /**
     * Check if current user can upgrade to a specific tier
     */
    public boolean canUpgradeToTier(SubscriptionTier targetTier) {
        User user = getCurrentUser();
        if (user == null) {
            return false;
        }
        
        return targetTier.getMonthlyPrice() > user.getTier().getMonthlyPrice();
    }
    
    /**
     * Get suggested upgrade tier for a feature
     */
    public SubscriptionTier getSuggestedTierForFeature(String featureName) {
        switch (featureName) {
            case "ai_cfo":
            case "what_if_simulation":
                return SubscriptionTier.SHIKHAR;
            case "health_score":
            case "daily_metrics":
            case "operational_dashboard":
                return SubscriptionTier.VISTAR;
            case "feasibility_report":
            case "pdf_download":
                return SubscriptionTier.AARAMBH;
            default:
                return SubscriptionTier.VISTAR; // Default to Vistar for unknown features
        }
    }
    
    /**
     * Create access denied response for API endpoints
     */
    public AccessDeniedResponse createAccessDeniedResponse(String featureName) {
        User user = getCurrentUser();
        SubscriptionTier currentTier = user != null ? user.getTier() : SubscriptionTier.AARAMBH;
        SubscriptionTier suggestedTier = getSuggestedTierForFeature(featureName);
        
        return new AccessDeniedResponse(
            "Feature not available in your current subscription tier",
            currentTier,
            suggestedTier,
            featureName
        );
    }
    
    /**
     * Inner class for access denied response
     */
    public static class AccessDeniedResponse {
        private final String message;
        private final SubscriptionTier currentTier;
        private final SubscriptionTier suggestedTier;
        private final String featureName;
        
        public AccessDeniedResponse(String message, SubscriptionTier currentTier, 
                                  SubscriptionTier suggestedTier, String featureName) {
            this.message = message;
            this.currentTier = currentTier;
            this.suggestedTier = suggestedTier;
            this.featureName = featureName;
        }
        
        public String getMessage() { return message; }
        public SubscriptionTier getCurrentTier() { return currentTier; }
        public SubscriptionTier getSuggestedTier() { return suggestedTier; }
        public String getFeatureName() { return featureName; }
    }
}