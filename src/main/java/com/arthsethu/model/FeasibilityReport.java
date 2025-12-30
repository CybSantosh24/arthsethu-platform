package com.arthsethu.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "feasibility_reports",
       indexes = {
           @Index(name = "idx_feasibility_reports_user", columnList = "user_id"),
           @Index(name = "idx_feasibility_reports_generated_at", columnList = "generated_at")
       })
public class FeasibilityReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "executive_summary", columnDefinition = "TEXT")
    private String executiveSummary;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal capex;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal opex;
    
    @Column(name = "break_even_point", precision = 12, scale = 2)
    private BigDecimal breakEvenPoint;
    
    @Column(name = "monthly_revenue_required", precision = 12, scale = 2)
    private BigDecimal monthlyRevenueRequired;
    
    @Column(name = "roi_percentage", precision = 5, scale = 2)
    private BigDecimal roiPercentage;
    
    @Column(name = "pdf_content", columnDefinition = "BYTEA")
    private byte[] pdfContent;
    
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
    
    @Column(name = "city")
    private String city;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "business_type")
    private BusinessType businessType;
    
    @Column(name = "government_data_snapshot", columnDefinition = "TEXT")
    private String governmentDataSnapshot; // JSON format
    
    // Constructors
    public FeasibilityReport() {
        this.generatedAt = LocalDateTime.now();
    }
    
    public FeasibilityReport(User user, String city, BusinessType businessType) {
        this();
        this.user = user;
        this.city = city;
        this.businessType = businessType;
    }
    
    // Business logic methods
    public BigDecimal getTotalInvestment() {
        if (capex == null) return BigDecimal.ZERO;
        return capex;
    }
    
    public BigDecimal getMonthlyOperatingCost() {
        if (opex == null) return BigDecimal.ZERO;
        return opex;
    }
    
    public Integer getBreakEvenMonths() {
        if (monthlyRevenueRequired == null || monthlyRevenueRequired.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        if (breakEvenPoint == null) return null;
        
        return breakEvenPoint.divide(monthlyRevenueRequired, 0, RoundingMode.UP).intValue();
    }
    
    public boolean isPdfGenerated() {
        return pdfContent != null && pdfContent.length > 0;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getExecutiveSummary() {
        return executiveSummary;
    }
    
    public void setExecutiveSummary(String executiveSummary) {
        this.executiveSummary = executiveSummary;
    }
    
    public BigDecimal getCapex() {
        return capex;
    }
    
    public void setCapex(BigDecimal capex) {
        this.capex = capex;
    }
    
    public BigDecimal getOpex() {
        return opex;
    }
    
    public void setOpex(BigDecimal opex) {
        this.opex = opex;
    }
    
    public BigDecimal getBreakEvenPoint() {
        return breakEvenPoint;
    }
    
    public void setBreakEvenPoint(BigDecimal breakEvenPoint) {
        this.breakEvenPoint = breakEvenPoint;
    }
    
    public BigDecimal getMonthlyRevenueRequired() {
        return monthlyRevenueRequired;
    }
    
    public void setMonthlyRevenueRequired(BigDecimal monthlyRevenueRequired) {
        this.monthlyRevenueRequired = monthlyRevenueRequired;
    }
    
    public BigDecimal getRoiPercentage() {
        return roiPercentage;
    }
    
    public void setRoiPercentage(BigDecimal roiPercentage) {
        this.roiPercentage = roiPercentage;
    }
    
    public byte[] getPdfContent() {
        return pdfContent;
    }
    
    public void setPdfContent(byte[] pdfContent) {
        this.pdfContent = pdfContent;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public BusinessType getBusinessType() {
        return businessType;
    }
    
    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }
    
    public String getGovernmentDataSnapshot() {
        return governmentDataSnapshot;
    }
    
    public void setGovernmentDataSnapshot(String governmentDataSnapshot) {
        this.governmentDataSnapshot = governmentDataSnapshot;
    }
}