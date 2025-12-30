package com.arthsethu.model;

public enum BusinessType {
    CAFE("Cafe", "Restaurant with seating capacity"),
    CLOUD_KITCHEN("Cloud Kitchen", "Delivery-only food business"),
    MANUFACTURING("Manufacturing", "Production and manufacturing business"),
    RETAIL("Retail", "Retail store business"),
    SERVICE("Service", "Service-based business");
    
    private final String displayName;
    private final String description;
    
    BusinessType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean requiresSeatingCapacity() {
        return this == CAFE;
    }
    
    public boolean requiresPackagingCosts() {
        return this == CLOUD_KITCHEN;
    }
    
    public boolean requiresPowerConsumption() {
        return this == MANUFACTURING;
    }
}