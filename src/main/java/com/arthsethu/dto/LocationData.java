package com.arthsethu.dto;

import java.math.BigDecimal;

/**
 * DTO for location-specific government data
 * Used for Requirements 2.1, 2.2, 2.3, 2.4, 2.5
 */
public class LocationData {
    private String city;
    private String state;
    private BigDecimal commercialRentPerSqFt;
    private BigDecimal averageWage;
    private CommodityPrices commodityPrices;
    
    public LocationData() {}
    
    public LocationData(String city, String state, BigDecimal commercialRentPerSqFt, 
                       BigDecimal averageWage, CommodityPrices commodityPrices) {
        this.city = city;
        this.state = state;
        this.commercialRentPerSqFt = commercialRentPerSqFt;
        this.averageWage = averageWage;
        this.commodityPrices = commodityPrices;
    }
    
    // Getters and setters
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public BigDecimal getCommercialRentPerSqFt() {
        return commercialRentPerSqFt;
    }
    
    public void setCommercialRentPerSqFt(BigDecimal commercialRentPerSqFt) {
        this.commercialRentPerSqFt = commercialRentPerSqFt;
    }
    
    public BigDecimal getAverageWage() {
        return averageWage;
    }
    
    public void setAverageWage(BigDecimal averageWage) {
        this.averageWage = averageWage;
    }
    
    public CommodityPrices getCommodityPrices() {
        return commodityPrices;
    }
    
    public void setCommodityPrices(CommodityPrices commodityPrices) {
        this.commodityPrices = commodityPrices;
    }
}