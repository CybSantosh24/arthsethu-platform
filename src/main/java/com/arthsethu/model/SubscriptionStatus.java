package com.arthsethu.model;

public enum SubscriptionStatus {
    PENDING("Pending", "Subscription is pending activation"),
    ACTIVE("Active", "Subscription is currently active"),
    SUSPENDED("Suspended", "Subscription is temporarily suspended"),
    CANCELLED("Cancelled", "Subscription has been cancelled"),
    EXPIRED("Expired", "Subscription has expired");
    
    private final String displayName;
    private final String description;
    
    SubscriptionStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isActive() {
        return this == ACTIVE;
    }
    
    public boolean canBeActivated() {
        return this == PENDING || this == SUSPENDED;
    }
    
    public boolean canBeCancelled() {
        return this == ACTIVE || this == PENDING || this == SUSPENDED;
    }
}