package com.arthsethu.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO for cost analysis results
 * Used for Requirements 2.1, 2.2, 2.3, 2.4, 2.5
 */
public class CostAnalysis {
    private BigDecimal totalCapex;
    private BigDecimal totalOpex;
    private BigDecimal monthlyOpex;
    private BigDecimal breakEvenPoint;
    private Map<String, BigDecimal> capexBreakdown;
    private Map<String, BigDecimal> opexBreakdown;
    private BigDecimal projectedRevenue;
    private Integer breakEvenMonths;
    
    public CostAnalysis() {}
    
    public CostAnalysis(BigDecimal totalCapex, BigDecimal totalOpex, BigDecimal monthlyOpex,
                       BigDecimal breakEvenPoint, Map<String, BigDecimal> capexBreakdown,
                       Map<String, BigDecimal> opexBreakdown, BigDecimal projectedRevenue,
                       Integer breakEvenMonths) {
        this.totalCapex = totalCapex;
        this.totalOpex = totalOpex;
        this.monthlyOpex = monthlyOpex;
        this.breakEvenPoint = breakEvenPoint;
        this.capexBreakdown = capexBreakdown;
        this.opexBreakdown = opexBreakdown;
        this.projectedRevenue = projectedRevenue;
        this.breakEvenMonths = breakEvenMonths;
    }
    
    // Getters and setters
    public BigDecimal getTotalCapex() {
        return totalCapex;
    }
    
    public void setTotalCapex(BigDecimal totalCapex) {
        this.totalCapex = totalCapex;
    }
    
    public BigDecimal getTotalOpex() {
        return totalOpex;
    }
    
    public void setTotalOpex(BigDecimal totalOpex) {
        this.totalOpex = totalOpex;
    }
    
    public BigDecimal getMonthlyOpex() {
        return monthlyOpex;
    }
    
    public void setMonthlyOpex(BigDecimal monthlyOpex) {
        this.monthlyOpex = monthlyOpex;
    }
    
    public BigDecimal getBreakEvenPoint() {
        return breakEvenPoint;
    }
    
    public void setBreakEvenPoint(BigDecimal breakEvenPoint) {
        this.breakEvenPoint = breakEvenPoint;
    }
    
    public Map<String, BigDecimal> getCapexBreakdown() {
        return capexBreakdown;
    }
    
    public void setCapexBreakdown(Map<String, BigDecimal> capexBreakdown) {
        this.capexBreakdown = capexBreakdown;
    }
    
    public Map<String, BigDecimal> getOpexBreakdown() {
        return opexBreakdown;
    }
    
    public void setOpexBreakdown(Map<String, BigDecimal> opexBreakdown) {
        this.opexBreakdown = opexBreakdown;
    }
    
    public BigDecimal getProjectedRevenue() {
        return projectedRevenue;
    }
    
    public void setProjectedRevenue(BigDecimal projectedRevenue) {
        this.projectedRevenue = projectedRevenue;
    }
    
    public Integer getBreakEvenMonths() {
        return breakEvenMonths;
    }
    
    public void setBreakEvenMonths(Integer breakEvenMonths) {
        this.breakEvenMonths = breakEvenMonths;
    }
}