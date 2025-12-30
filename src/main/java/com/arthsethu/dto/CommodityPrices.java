package com.arthsethu.dto;

import java.math.BigDecimal;

/**
 * DTO for commodity prices from government data
 * Used for Requirements 2.5
 */
public class CommodityPrices {
    private BigDecimal milkPricePerLiter;
    private BigDecimal steelPricePerKg;
    private BigDecimal fabricPricePerMeter;
    private BigDecimal electricityPricePerUnit;
    private BigDecimal fuelPricePerLiter;
    
    public CommodityPrices() {}
    
    public CommodityPrices(BigDecimal milkPricePerLiter, BigDecimal steelPricePerKg, 
                          BigDecimal fabricPricePerMeter, BigDecimal electricityPricePerUnit,
                          BigDecimal fuelPricePerLiter) {
        this.milkPricePerLiter = milkPricePerLiter;
        this.steelPricePerKg = steelPricePerKg;
        this.fabricPricePerMeter = fabricPricePerMeter;
        this.electricityPricePerUnit = electricityPricePerUnit;
        this.fuelPricePerLiter = fuelPricePerLiter;
    }
    
    // Getters and setters
    public BigDecimal getMilkPricePerLiter() {
        return milkPricePerLiter;
    }
    
    public void setMilkPricePerLiter(BigDecimal milkPricePerLiter) {
        this.milkPricePerLiter = milkPricePerLiter;
    }
    
    public BigDecimal getSteelPricePerKg() {
        return steelPricePerKg;
    }
    
    public void setSteelPricePerKg(BigDecimal steelPricePerKg) {
        this.steelPricePerKg = steelPricePerKg;
    }
    
    public BigDecimal getFabricPricePerMeter() {
        return fabricPricePerMeter;
    }
    
    public void setFabricPricePerMeter(BigDecimal fabricPricePerMeter) {
        this.fabricPricePerMeter = fabricPricePerMeter;
    }
    
    public BigDecimal getElectricityPricePerUnit() {
        return electricityPricePerUnit;
    }
    
    public void setElectricityPricePerUnit(BigDecimal electricityPricePerUnit) {
        this.electricityPricePerUnit = electricityPricePerUnit;
    }
    
    public BigDecimal getFuelPricePerLiter() {
        return fuelPricePerLiter;
    }
    
    public void setFuelPricePerLiter(BigDecimal fuelPricePerLiter) {
        this.fuelPricePerLiter = fuelPricePerLiter;
    }
}