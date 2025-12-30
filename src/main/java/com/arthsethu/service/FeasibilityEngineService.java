package com.arthsethu.service;

import com.arthsethu.dto.CommodityPrices;
import com.arthsethu.dto.CostAnalysis;
import com.arthsethu.dto.LocationData;
import com.arthsethu.model.BusinessProfile;
import com.arthsethu.model.BusinessType;
import com.arthsethu.model.FeasibilityReport;
import com.arthsethu.service.GovernmentDataInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for calculating business feasibility using government data
 * Implements Requirements 2.1, 2.2, 2.3, 2.4, 2.5
 */
@Service
public class FeasibilityEngineService {
    
    private static final Logger logger = LoggerFactory.getLogger(FeasibilityEngineService.class);
    
    private final GovernmentDataInterface governmentDataService;
    private final PdfGenerationService pdfGenerationService;
    
    public FeasibilityEngineService(GovernmentDataInterface governmentDataService, 
                                  PdfGenerationService pdfGenerationService) {
        this.governmentDataService = governmentDataService;
        this.pdfGenerationService = pdfGenerationService;
    }
    
    /**
     * Calculate comprehensive cost analysis for a business profile
     * Implements Requirements 2.1, 2.2, 2.3, 2.4, 2.5
     */
    public CostAnalysis calculateCosts(BusinessProfile businessProfile) {
        logger.info("Calculating costs for business profile: {} in city: {}", 
                   businessProfile.getBusinessType(), businessProfile.getCity());
        
        // Fetch government data for the location and business type
        LocationData locationData;
        try {
            locationData = governmentDataService.fetchLocationData(
                businessProfile.getCity(), businessProfile.getBusinessType());
        } catch (Exception e) {
            logger.warn("Failed to fetch government data, using fallback: {}", e.getMessage());
            locationData = createDefaultLocationData(
                businessProfile.getCity(), businessProfile.getBusinessType());
        }
        
        // Calculate CAPEX based on business type and location data
        Map<String, BigDecimal> capexBreakdown = calculateCapexBreakdown(businessProfile, locationData);
        BigDecimal totalCapex = capexBreakdown.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate OPEX based on business type and location data
        Map<String, BigDecimal> opexBreakdown = calculateOpexBreakdown(businessProfile, locationData);
        BigDecimal monthlyOpex = opexBreakdown.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOpex = monthlyOpex.multiply(new BigDecimal("12")); // Annual OPEX
        
        // Calculate projected revenue based on business type
        BigDecimal projectedRevenue = calculateProjectedRevenue(businessProfile, locationData);
        
        // Calculate break-even analysis
        BigDecimal breakEvenPoint = calculateBreakEvenPoint(totalCapex, monthlyOpex, projectedRevenue);
        Integer breakEvenMonths = calculateBreakEvenMonths(totalCapex, projectedRevenue, monthlyOpex);
        
        CostAnalysis analysis = new CostAnalysis(
            totalCapex, totalOpex, monthlyOpex, breakEvenPoint,
            capexBreakdown, opexBreakdown, projectedRevenue, breakEvenMonths
        );
        
        logger.info("Cost analysis completed for {}: CAPEX={}, Monthly OPEX={}, Break-even months={}", 
                   businessProfile.getBusinessType(), totalCapex, monthlyOpex, breakEvenMonths);
        
        return analysis;
    }
    
    /**
     * Calculate CAPEX breakdown based on business type
     */
    private Map<String, BigDecimal> calculateCapexBreakdown(BusinessProfile businessProfile, LocationData locationData) {
        Map<String, BigDecimal> capexBreakdown = new HashMap<>();
        BusinessType businessType = businessProfile.getBusinessType();
        
        switch (businessType) {
            case CAFE:
                capexBreakdown.putAll(calculateCafeCapex(businessProfile, locationData));
                break;
            case CLOUD_KITCHEN:
                capexBreakdown.putAll(calculateCloudKitchenCapex(businessProfile, locationData));
                break;
            case MANUFACTURING:
                capexBreakdown.putAll(calculateManufacturingCapex(businessProfile, locationData));
                break;
            default:
                capexBreakdown.putAll(calculateDefaultCapex(businessProfile, locationData));
        }
        
        return capexBreakdown;
    }
    
    /**
     * Calculate OPEX breakdown based on business type
     */
    private Map<String, BigDecimal> calculateOpexBreakdown(BusinessProfile businessProfile, LocationData locationData) {
        Map<String, BigDecimal> opexBreakdown = new HashMap<>();
        BusinessType businessType = businessProfile.getBusinessType();
        
        switch (businessType) {
            case CAFE:
                opexBreakdown.putAll(calculateCafeOpex(businessProfile, locationData));
                break;
            case CLOUD_KITCHEN:
                opexBreakdown.putAll(calculateCloudKitchenOpex(businessProfile, locationData));
                break;
            case MANUFACTURING:
                opexBreakdown.putAll(calculateManufacturingOpex(businessProfile, locationData));
                break;
            default:
                opexBreakdown.putAll(calculateDefaultOpex(businessProfile, locationData));
        }
        
        return opexBreakdown;
    }
    
    /**
     * Calculate CAPEX for cafe business
     */
    private Map<String, BigDecimal> calculateCafeCapex(BusinessProfile businessProfile, LocationData locationData) {
        Map<String, BigDecimal> capex = new HashMap<>();
        
        // Calculate space requirements based on seating capacity
        Integer seatingCapacity = businessProfile.getSeatingCapacity() != null ? 
            businessProfile.getSeatingCapacity() : 20;
        BigDecimal spaceRequired = new BigDecimal(seatingCapacity * 15); // 15 sq ft per seat
        
        // Security deposit (3 months rent)
        BigDecimal monthlyRent = locationData.getCommercialRentPerSqFt().multiply(spaceRequired);
        capex.put("Security Deposit", monthlyRent.multiply(new BigDecimal("3")));
        
        // Equipment costs
        capex.put("Kitchen Equipment", new BigDecimal("300000"));
        capex.put("Furniture & Fixtures", new BigDecimal(seatingCapacity * 5000));
        capex.put("Interior Design", spaceRequired.multiply(new BigDecimal("500")));
        capex.put("POS System", new BigDecimal("50000"));
        capex.put("Initial Inventory", new BigDecimal("75000"));
        capex.put("Licenses & Permits", new BigDecimal("25000"));
        
        return capex;
    }
    
    /**
     * Calculate OPEX for cafe business
     */
    private Map<String, BigDecimal> calculateCafeOpex(BusinessProfile businessProfile, LocationData locationData) {
        Map<String, BigDecimal> opex = new HashMap<>();
        
        Integer seatingCapacity = businessProfile.getSeatingCapacity() != null ? 
            businessProfile.getSeatingCapacity() : 20;
        BigDecimal spaceRequired = new BigDecimal(seatingCapacity * 15);
        
        // Monthly rent
        BigDecimal monthlyRent = locationData.getCommercialRentPerSqFt().multiply(spaceRequired);
        opex.put("Rent", monthlyRent);
        
        // Staff costs (2-3 staff members)
        BigDecimal staffCost = locationData.getAverageWage().multiply(new BigDecimal("2.5"));
        opex.put("Staff Salaries", staffCost);
        
        // Raw materials (milk, coffee beans, etc.)
        BigDecimal rawMaterialCost = locationData.getCommodityPrices().getMilkPricePerLiter()
            .multiply(new BigDecimal("200")) // 200 liters per month
            .add(new BigDecimal("30000")); // Other ingredients
        opex.put("Raw Materials", rawMaterialCost);
        
        // Utilities
        BigDecimal utilityCost = locationData.getCommodityPrices().getElectricityPricePerUnit()
            .multiply(new BigDecimal("500")) // 500 units per month
            .add(new BigDecimal("5000")); // Water, gas, internet
        opex.put("Utilities", utilityCost);
        
        // Other expenses
        opex.put("Marketing", new BigDecimal("10000"));
        opex.put("Maintenance", new BigDecimal("8000"));
        opex.put("Insurance", new BigDecimal("5000"));
        
        return opex;
    }
    
    /**
     * Calculate CAPEX for cloud kitchen business
     */
    private Map<String, BigDecimal> calculateCloudKitchenCapex(BusinessProfile businessProfile, LocationData locationData) {
        Map<String, BigDecimal> capex = new HashMap<>();
        
        // Smaller space requirement (300-500 sq ft)
        BigDecimal spaceRequired = new BigDecimal("400");
        BigDecimal monthlyRent = locationData.getCommercialRentPerSqFt().multiply(spaceRequired);
        
        capex.put("Security Deposit", monthlyRent.multiply(new BigDecimal("3")));
        capex.put("Kitchen Equipment", new BigDecimal("250000"));
        capex.put("Packaging Equipment", new BigDecimal("50000"));
        capex.put("Delivery Setup", new BigDecimal("30000"));
        capex.put("Initial Inventory", new BigDecimal("40000"));
        capex.put("Licenses & Permits", new BigDecimal("20000"));
        
        // Add packaging costs if specified
        if (businessProfile.getPackagingCosts() != null) {
            capex.put("Initial Packaging Stock", new BigDecimal(businessProfile.getPackagingCosts()).multiply(new BigDecimal("3")));
        }
        
        return capex;
    }
    
    /**
     * Calculate OPEX for cloud kitchen business
     */
    private Map<String, BigDecimal> calculateCloudKitchenOpex(BusinessProfile businessProfile, LocationData locationData) {
        Map<String, BigDecimal> opex = new HashMap<>();
        
        BigDecimal spaceRequired = new BigDecimal("400");
        BigDecimal monthlyRent = locationData.getCommercialRentPerSqFt().multiply(spaceRequired);
        
        opex.put("Rent", monthlyRent);
        opex.put("Staff Salaries", locationData.getAverageWage().multiply(new BigDecimal("1.5"))); // Fewer staff
        
        // Packaging costs
        BigDecimal packagingCost = businessProfile.getPackagingCosts() != null ? 
            new BigDecimal(businessProfile.getPackagingCosts()) : new BigDecimal("15000");
        opex.put("Packaging", packagingCost);
        
        // Raw materials
        opex.put("Raw Materials", new BigDecimal("25000"));
        
        // Delivery costs
        opex.put("Delivery Charges", new BigDecimal("12000"));
        
        // Platform commissions (Swiggy, Zomato)
        opex.put("Platform Commissions", new BigDecimal("18000"));
        
        // Utilities
        BigDecimal utilityCost = locationData.getCommodityPrices().getElectricityPricePerUnit()
            .multiply(new BigDecimal("300"))
            .add(new BigDecimal("3000"));
        opex.put("Utilities", utilityCost);
        
        opex.put("Marketing", new BigDecimal("8000"));
        opex.put("Maintenance", new BigDecimal("5000"));
        
        return opex;
    }
    
    /**
     * Calculate CAPEX for manufacturing business
     */
    private Map<String, BigDecimal> calculateManufacturingCapex(BusinessProfile businessProfile, LocationData locationData) {
        Map<String, BigDecimal> capex = new HashMap<>();
        
        // Larger space requirement (1000-2000 sq ft)
        BigDecimal spaceRequired = new BigDecimal("1500");
        BigDecimal monthlyRent = locationData.getCommercialRentPerSqFt().multiply(spaceRequired);
        
        capex.put("Security Deposit", monthlyRent.multiply(new BigDecimal("6"))); // Higher deposit
        capex.put("Machinery & Equipment", new BigDecimal("800000"));
        capex.put("Raw Material Stock", new BigDecimal("200000"));
        capex.put("Factory Setup", new BigDecimal("150000"));
        capex.put("Licenses & Permits", new BigDecimal("50000"));
        capex.put("Safety Equipment", new BigDecimal("75000"));
        
        // Power connection costs
        if (businessProfile.getPowerConsumption() != null) {
            BigDecimal powerSetupCost = new BigDecimal(businessProfile.getPowerConsumption()).multiply(new BigDecimal("1000"));
            capex.put("Power Connection", powerSetupCost);
        }
        
        return capex;
    }
    
    /**
     * Calculate OPEX for manufacturing business
     */
    private Map<String, BigDecimal> calculateManufacturingOpex(BusinessProfile businessProfile, LocationData locationData) {
        Map<String, BigDecimal> opex = new HashMap<>();
        
        BigDecimal spaceRequired = new BigDecimal("1500");
        BigDecimal monthlyRent = locationData.getCommercialRentPerSqFt().multiply(spaceRequired);
        
        opex.put("Rent", monthlyRent);
        opex.put("Staff Salaries", locationData.getAverageWage().multiply(new BigDecimal("4"))); // More staff
        
        // Raw materials based on business type
        BigDecimal rawMaterialCost = calculateManufacturingRawMaterialCost(businessProfile, locationData);
        opex.put("Raw Materials", rawMaterialCost);
        
        // Power consumption
        if (businessProfile.getPowerConsumption() != null) {
            BigDecimal powerCost = locationData.getCommodityPrices().getElectricityPricePerUnit()
                .multiply(new BigDecimal(businessProfile.getPowerConsumption()));
            opex.put("Power", powerCost);
        } else {
            opex.put("Power", new BigDecimal("25000"));
        }
        
        opex.put("Maintenance", new BigDecimal("15000"));
        opex.put("Transportation", new BigDecimal("10000"));
        opex.put("Insurance", new BigDecimal("8000"));
        opex.put("Quality Control", new BigDecimal("5000"));
        
        return opex;
    }
    
    /**
     * Calculate raw material costs for manufacturing
     */
    private BigDecimal calculateManufacturingRawMaterialCost(BusinessProfile businessProfile, LocationData locationData) {
        // Base cost
        BigDecimal baseCost = new BigDecimal("50000");
        
        // Add costs based on raw material sourcing
        if (businessProfile.getRawMaterialSourcing() != null) {
            String sourcing = businessProfile.getRawMaterialSourcing().toLowerCase();
            if (sourcing.contains("steel")) {
                baseCost = baseCost.add(locationData.getCommodityPrices().getSteelPricePerKg()
                    .multiply(new BigDecimal("100"))); // 100 kg steel
            }
            if (sourcing.contains("fabric")) {
                baseCost = baseCost.add(locationData.getCommodityPrices().getFabricPricePerMeter()
                    .multiply(new BigDecimal("50"))); // 50 meters fabric
            }
        }
        
        return baseCost;
    }
    
    /**
     * Calculate default CAPEX for unknown business types
     */
    private Map<String, BigDecimal> calculateDefaultCapex(BusinessProfile businessProfile, LocationData locationData) {
        Map<String, BigDecimal> capex = new HashMap<>();
        
        BigDecimal spaceRequired = new BigDecimal("500");
        BigDecimal monthlyRent = locationData.getCommercialRentPerSqFt().multiply(spaceRequired);
        
        capex.put("Security Deposit", monthlyRent.multiply(new BigDecimal("3")));
        capex.put("Equipment", new BigDecimal("200000"));
        capex.put("Setup Costs", new BigDecimal("100000"));
        capex.put("Initial Inventory", new BigDecimal("50000"));
        capex.put("Licenses & Permits", new BigDecimal("25000"));
        
        return capex;
    }
    
    /**
     * Calculate default OPEX for unknown business types
     */
    private Map<String, BigDecimal> calculateDefaultOpex(BusinessProfile businessProfile, LocationData locationData) {
        Map<String, BigDecimal> opex = new HashMap<>();
        
        BigDecimal spaceRequired = new BigDecimal("500");
        BigDecimal monthlyRent = locationData.getCommercialRentPerSqFt().multiply(spaceRequired);
        
        opex.put("Rent", monthlyRent);
        opex.put("Staff Salaries", locationData.getAverageWage().multiply(new BigDecimal("2")));
        opex.put("Operating Costs", new BigDecimal("20000"));
        opex.put("Utilities", new BigDecimal("8000"));
        opex.put("Maintenance", new BigDecimal("5000"));
        
        return opex;
    }
    
    /**
     * Calculate projected revenue based on business type and location
     */
    private BigDecimal calculateProjectedRevenue(BusinessProfile businessProfile, LocationData locationData) {
        BusinessType businessType = businessProfile.getBusinessType();
        
        switch (businessType) {
            case CAFE:
                Integer seatingCapacity = businessProfile.getSeatingCapacity() != null ? 
                    businessProfile.getSeatingCapacity() : 20;
                // Assume 3 table turns per day, average bill of ₹200
                return new BigDecimal(seatingCapacity * 3 * 200 * 30); // Monthly revenue
                
            case CLOUD_KITCHEN:
                // Assume 50 orders per day, average order value ₹300
                return new BigDecimal("450000"); // 50 * 300 * 30
                
            case MANUFACTURING:
                // Conservative estimate based on investment
                return new BigDecimal("200000");
                
            default:
                return new BigDecimal("100000");
        }
    }
    
    /**
     * Create default location data when no government data is available
     */
    private LocationData createDefaultLocationData(String city, BusinessType businessType) {
        // Create conservative default values
        BigDecimal defaultRent = new BigDecimal("75.00");
        BigDecimal defaultWage = new BigDecimal("15000.00");
        CommodityPrices defaultPrices = new CommodityPrices(
            new BigDecimal("45.00"), // milk
            new BigDecimal("40.00"), // steel
            new BigDecimal("150.00"), // fabric
            new BigDecimal("5.50"), // electricity
            new BigDecimal("95.00") // fuel
        );
        
        return new LocationData(city, "Unknown", defaultRent, defaultWage, defaultPrices);
    }
    
    /**
     * Calculate break-even point in revenue terms
     */
    private BigDecimal calculateBreakEvenPoint(BigDecimal totalCapex, BigDecimal monthlyOpex, BigDecimal projectedRevenue) {
        // Break-even point = (CAPEX + Monthly OPEX) / Gross Margin
        // Assuming 30% gross margin
        BigDecimal grossMargin = new BigDecimal("0.30");
        BigDecimal monthlyBreakEven = monthlyOpex.divide(grossMargin, 2, RoundingMode.HALF_UP);
        
        return monthlyBreakEven;
    }
    
    /**
     * Calculate break-even period in months
     */
    private Integer calculateBreakEvenMonths(BigDecimal totalCapex, BigDecimal projectedRevenue, BigDecimal monthlyOpex) {
        // Monthly profit = Revenue - OPEX
        BigDecimal monthlyProfit = projectedRevenue.subtract(monthlyOpex);
        
        if (monthlyProfit.compareTo(BigDecimal.ZERO) <= 0) {
            return null; // Business is not profitable
        }
        
        // Break-even months = CAPEX / Monthly Profit
        BigDecimal breakEvenMonths = totalCapex.divide(monthlyProfit, 0, RoundingMode.HALF_UP);
        
        return breakEvenMonths.intValue();
    }
    
    /**
     * Generate PDF report for feasibility analysis
     * Implements Requirements 3.1, 3.2, 3.3, 3.4, 3.5
     */
    public byte[] generatePDFReport(FeasibilityReport report) {
        logger.info("Generating PDF report for feasibility report ID: {}", report.getId());
        
        try {
            // Calculate cost analysis for the report
            CostAnalysis costAnalysis = calculateCostsFromReport(report);
            
            // Generate PDF using the PDF generation service
            byte[] pdfContent = pdfGenerationService.generateFeasibilityReportPdf(report, costAnalysis);
            
            logger.info("PDF report generated successfully for report ID: {}", report.getId());
            return pdfContent;
            
        } catch (Exception e) {
            logger.error("Failed to generate PDF report for report ID: {}", report.getId(), e);
            throw new RuntimeException("PDF report generation failed", e);
        }
    }
    
    /**
     * Calculate cost analysis from existing feasibility report data
     */
    private CostAnalysis calculateCostsFromReport(FeasibilityReport report) {
        // Create a temporary business profile from report data
        BusinessProfile tempProfile = new BusinessProfile();
        tempProfile.setBusinessType(report.getBusinessType());
        tempProfile.setCity(report.getCity());
        
        // Set default values for missing profile data
        if (report.getBusinessType() == BusinessType.CAFE) {
            tempProfile.setSeatingCapacity(20); // Default seating capacity
        } else if (report.getBusinessType() == BusinessType.CLOUD_KITCHEN) {
            tempProfile.setPackagingCosts(15000.0); // Default packaging costs
        } else if (report.getBusinessType() == BusinessType.MANUFACTURING) {
            tempProfile.setPowerConsumption(500.0); // Default power consumption
            tempProfile.setRawMaterialSourcing("steel,fabric"); // Default materials
        }
        
        // Calculate costs using the existing method
        return calculateCosts(tempProfile);
    }
}