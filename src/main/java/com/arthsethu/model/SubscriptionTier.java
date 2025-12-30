package com.arthsethu.model;

public enum SubscriptionTier {
    AARAMBH("Aarambh", 0.0, "Free tier with feasibility analysis and PDF reports"),
    VISTAR("Vistar", 499.0, "Professional tier with operational dashboards and health scoring"),
    SHIKHAR("Shikhar", 999.0, "Enterprise tier with AI CFO and strategic simulations");
    
    private final String displayName;
    private final Double monthlyPrice;
    private final String description;
    
    SubscriptionTier(String displayName, Double monthlyPrice, String description) {
        this.displayName = displayName;
        this.monthlyPrice = monthlyPrice;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Double getMonthlyPrice() {
        return monthlyPrice;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isFreeTier() {
        return this == AARAMBH;
    }
    
    public boolean allowsFeature(String featureName) {
        switch (this) {
            case AARAMBH:
                return featureName.equals("feasibility_report") || featureName.equals("pdf_download");
            case VISTAR:
                return !featureName.equals("ai_cfo") && !featureName.equals("what_if_simulation");
            case SHIKHAR:
                return true;
            default:
                return false;
        }
    }
}