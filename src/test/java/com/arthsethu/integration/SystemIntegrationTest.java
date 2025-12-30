package com.arthsethu.integration;

import com.arthsethu.dto.CostAnalysis;
import com.arthsethu.dto.LocationData;
import com.arthsethu.model.*;
import com.arthsethu.repository.*;
import com.arthsethu.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * System integration tests for external service integrations
 * Tests government data integration, AI services, and PDF generation
 * Validates: Requirements 2.1-2.5, 6.1-6.5, 3.1-3.5
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SystemIntegrationTest {
    
    @Autowired
    private GovernmentDataService governmentDataService;
    
    @Autowired
    private FeasibilityEngineService feasibilityEngineService;
    
    @Autowired
    private PdfGenerationService pdfGenerationService;
    
    @Autowired
    private AICFOService aicfoService;
    
    @Autowired
    private HealthScoreService healthScoreService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BusinessProfileRepository businessProfileRepository;
    
    @Autowired
    private FeasibilityReportRepository feasibilityReportRepository;
    
    @Autowired
    private DailyMetricsRepository dailyMetricsRepository;
    
    private User testUser;
    private BusinessProfile testBusinessProfile;
    
    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setEmail("integration@test.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setTier(SubscriptionTier.SHIKHAR);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser = userRepository.save(testUser);
        
        // Create test business profile
        testBusinessProfile = new BusinessProfile();
        testBusinessProfile.setUser(testUser);
        testBusinessProfile.setBusinessType(BusinessType.CAFE);
        testBusinessProfile.setCity("Mumbai");
        testBusinessProfile.setSeatingCapacity(25);
        testBusinessProfile = businessProfileRepository.save(testBusinessProfile);
        
        testUser.setBusinessProfile(testBusinessProfile);
        userRepository.save(testUser);
    }
    
    /**
     * Test government data integration service
     * Validates: Requirements 2.1, 2.2, 2.3, 2.4, 2.5
     */
    @Test
    void testGovernmentDataIntegration() {
        // Test fetching location data for different cities and business types
        LocationData mumbaiData = ((GovernmentDataInterface) governmentDataService).fetchLocationData("Mumbai", BusinessType.CAFE);
        assertThat(mumbaiData).isNotNull();
        assertThat(mumbaiData.getCity()).isEqualTo("Mumbai");
        assertThat(mumbaiData.getCommercialRentPerSqFt()).isNotNull();
        assertThat(mumbaiData.getCommercialRentPerSqFt()).isGreaterThan(BigDecimal.ZERO);
        assertThat(mumbaiData.getAverageWage()).isNotNull();
        assertThat(mumbaiData.getAverageWage()).isGreaterThan(BigDecimal.ZERO);
        assertThat(mumbaiData.getCommodityPrices()).isNotNull();
        
        // Test commodity prices are populated
        assertThat(mumbaiData.getCommodityPrices().getMilkPricePerLiter()).isGreaterThan(BigDecimal.ZERO);
        assertThat(mumbaiData.getCommodityPrices().getSteelPricePerKg()).isGreaterThan(BigDecimal.ZERO);
        assertThat(mumbaiData.getCommodityPrices().getFabricPricePerMeter()).isGreaterThan(BigDecimal.ZERO);
        assertThat(mumbaiData.getCommodityPrices().getElectricityPricePerUnit()).isGreaterThan(BigDecimal.ZERO);
        
        // Test different cities
        LocationData delhiData = ((GovernmentDataInterface) governmentDataService).fetchLocationData("Delhi", BusinessType.MANUFACTURING);
        assertThat(delhiData).isNotNull();
        assertThat(delhiData.getCity()).isEqualTo("Delhi");
        
        // Test caching - second call should be faster
        long startTime = System.currentTimeMillis();
        LocationData cachedData = ((GovernmentDataInterface) governmentDataService).fetchLocationData("Mumbai", BusinessType.CAFE);
        long endTime = System.currentTimeMillis();
        
        assertThat(cachedData).isNotNull();
        assertThat(endTime - startTime).isLessThan(100); // Should be very fast due to caching
        assertThat(cachedData.getCommercialRentPerSqFt()).isEqualTo(mumbaiData.getCommercialRentPerSqFt());
    }
    
    /**
     * Test feasibility engine integration with government data
     * Validates: Requirements 2.1-2.5, 3.1-3.4
     */
    @Test
    void testFeasibilityEngineIntegration() {
        // Test cost calculation for different business types
        
        // Test Cafe business
        CostAnalysis cafeAnalysis = feasibilityEngineService.calculateCosts(testBusinessProfile);
        assertThat(cafeAnalysis).isNotNull();
        assertThat(cafeAnalysis.getTotalCapex()).isGreaterThan(BigDecimal.ZERO);
        assertThat(cafeAnalysis.getMonthlyOpex()).isGreaterThan(BigDecimal.ZERO);
        assertThat(cafeAnalysis.getCapexBreakdown()).isNotEmpty();
        assertThat(cafeAnalysis.getOpexBreakdown()).isNotEmpty();
        assertThat(cafeAnalysis.getProjectedRevenue()).isGreaterThan(BigDecimal.ZERO);
        
        // Verify cafe-specific costs are included
        assertThat(cafeAnalysis.getCapexBreakdown()).containsKey("Kitchen Equipment");
        assertThat(cafeAnalysis.getCapexBreakdown()).containsKey("Furniture & Fixtures");
        assertThat(cafeAnalysis.getOpexBreakdown()).containsKey("Raw Materials");
        assertThat(cafeAnalysis.getOpexBreakdown()).containsKey("Staff Salaries");
        
        // Test Cloud Kitchen business
        testBusinessProfile.setBusinessType(BusinessType.CLOUD_KITCHEN);
        testBusinessProfile.setPackagingCosts(12000.0);
        businessProfileRepository.save(testBusinessProfile);
        
        CostAnalysis cloudKitchenAnalysis = feasibilityEngineService.calculateCosts(testBusinessProfile);
        assertThat(cloudKitchenAnalysis).isNotNull();
        assertThat(cloudKitchenAnalysis.getCapexBreakdown()).containsKey("Packaging Equipment");
        assertThat(cloudKitchenAnalysis.getOpexBreakdown()).containsKey("Packaging");
        assertThat(cloudKitchenAnalysis.getOpexBreakdown()).containsKey("Platform Commissions");
        
        // Test Manufacturing business
        testBusinessProfile.setBusinessType(BusinessType.MANUFACTURING);
        testBusinessProfile.setPowerConsumption(800.0);
        testBusinessProfile.setRawMaterialSourcing("steel,fabric");
        businessProfileRepository.save(testBusinessProfile);
        
        CostAnalysis manufacturingAnalysis = feasibilityEngineService.calculateCosts(testBusinessProfile);
        assertThat(manufacturingAnalysis).isNotNull();
        assertThat(manufacturingAnalysis.getCapexBreakdown()).containsKey("Machinery & Equipment");
        assertThat(manufacturingAnalysis.getOpexBreakdown()).containsKey("Power");
        assertThat(manufacturingAnalysis.getOpexBreakdown()).containsKey("Raw Materials");
        
        // Verify break-even calculations
        assertThat(cafeAnalysis.getBreakEvenMonths()).isNotNull();
        assertThat(cafeAnalysis.getBreakEvenMonths()).isGreaterThan(0);
        assertThat(cafeAnalysis.getBreakEvenPoint()).isGreaterThan(BigDecimal.ZERO);
    }
    
    /**
     * Test PDF generation integration
     * Validates: Requirements 3.1, 3.2, 3.3, 3.4, 3.5
     */
    @Test
    void testPdfGenerationIntegration() {
        // Create feasibility report
        FeasibilityReport report = new FeasibilityReport();
        report.setUser(testUser);
        report.setBusinessType(testBusinessProfile.getBusinessType());
        report.setCity(testBusinessProfile.getCity());
        report.setExecutiveSummary("Test executive summary for PDF generation");
        report.setCapex(new BigDecimal("500000"));
        report.setOpex(new BigDecimal("50000"));
        report.setBreakEvenPoint(new BigDecimal("75000"));
        report.setGeneratedAt(LocalDateTime.now());
        report = feasibilityReportRepository.save(report);
        
        // Test PDF generation
        byte[] pdfContent = feasibilityEngineService.generatePDFReport(report);
        
        assertThat(pdfContent).isNotNull();
        assertThat(pdfContent.length).isGreaterThan(1000); // PDF should have substantial content
        
        // Verify PDF header (PDF files start with %PDF)
        String pdfHeader = new String(pdfContent, 0, 4);
        assertThat(pdfHeader).isEqualTo("%PDF");
        
        // Test that PDF content is saved to report
        FeasibilityReport updatedReport = feasibilityReportRepository.findById(report.getId()).orElse(null);
        assertThat(updatedReport).isNotNull();
        assertThat(updatedReport.isPdfGenerated()).isTrue();
        assertThat(updatedReport.getPdfContent()).isNotNull();
        assertThat(updatedReport.getPdfContent().length).isEqualTo(pdfContent.length);
    }
    
    /**
     * Test AI CFO service integration
     * Validates: Requirements 6.1, 6.2, 6.3, 6.4, 6.5
     */
    @Test
    void testAICFOServiceIntegration() {
        // Add historical metrics for context
        addHistoricalMetricsData();
        
        // Test basic query processing
        String response = aicfoService.processQuery(
            "What are my current profit margins?", 
            testBusinessProfile, 
            dailyMetricsRepository.findByUser(testUser)
        );
        
        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();
        assertThat(response.length()).isGreaterThan(50); // Should be a substantial response
        
        // Test contextual awareness
        String contextualResponse = aicfoService.processQuery(
            "Based on my recent performance, should I expand?", 
            testBusinessProfile, 
            dailyMetricsRepository.findByUser(testUser)
        );
        
        assertThat(contextualResponse).isNotNull();
        assertThat(contextualResponse).isNotEmpty();
        // Response should be different from generic query
        assertThat(contextualResponse).isNotEqualTo(response);
        
        // Test what-if simulation
        com.arthsethu.dto.WhatIfAnalysis simulationResult = aicfoService.performSimulation(
            "increase prices by 10%", 
            testBusinessProfile
        );
        
        assertThat(simulationResult).isNotNull();
        assertThat(simulationResult.getScenario()).isEqualTo("increase prices by 10%");
        assertThat(simulationResult.getAnalysis()).isNotNull();
        assertThat(simulationResult.getAnalysis()).isNotEmpty();
    }
    
    /**
     * Test health score calculation integration
     * Validates: Requirements 4.2, 4.3
     */
    @Test
    void testHealthScoreIntegration() {
        // Add metrics data
        addHistoricalMetricsData();
        
        // Test health score calculation with specific metrics
        BigDecimal sales = new BigDecimal("15000");
        BigDecimal expenses = new BigDecimal("8000");
        BigDecimal wastage = new BigDecimal("500");
        
        Integer healthScore = healthScoreService.calculateHealthScore(sales, expenses, wastage);
        
        assertThat(healthScore).isNotNull();
        assertThat(healthScore).isBetween(0, 100);
        
        // Test health score with different scenarios
        
        // Scenario 1: High wastage should lower health score
        DailyMetrics highWastageMetrics = new DailyMetrics();
        highWastageMetrics.setUser(testUser);
        highWastageMetrics.setDate(java.time.LocalDate.now());
        highWastageMetrics.setSales(new BigDecimal("15000"));
        highWastageMetrics.setExpenses(new BigDecimal("8000"));
        highWastageMetrics.setWastage(new BigDecimal("3000")); // High wastage
        dailyMetricsRepository.save(highWastageMetrics);
        
        Integer lowHealthScore = healthScoreService.calculateHealthScore(
            new BigDecimal("15000"), new BigDecimal("8000"), new BigDecimal("3000"));
        assertThat(lowHealthScore).isLessThan(healthScore);
        
        // Scenario 2: Low wastage should improve health score
        DailyMetrics lowWastageMetrics = new DailyMetrics();
        lowWastageMetrics.setUser(testUser);
        lowWastageMetrics.setDate(java.time.LocalDate.now().plusDays(1));
        lowWastageMetrics.setSales(new BigDecimal("15000"));
        lowWastageMetrics.setExpenses(new BigDecimal("8000"));
        lowWastageMetrics.setWastage(new BigDecimal("200")); // Low wastage
        dailyMetricsRepository.save(lowWastageMetrics);
        
        Integer highHealthScore = healthScoreService.calculateHealthScore(
            new BigDecimal("15000"), new BigDecimal("8000"), new BigDecimal("200"));
        assertThat(highHealthScore).isGreaterThan(lowHealthScore);
        
        // Test trend analysis using repository methods
        List<DailyMetrics> trendData = dailyMetricsRepository.findByUser(testUser);
        assertThat(trendData).isNotEmpty();
        assertThat(trendData.size()).isLessThanOrEqualTo(32); // 30 historical + 2 new ones
        
        // Test health score analysis
        com.arthsethu.dto.HealthScoreDTO analysis = healthScoreService.getHealthScoreAnalysis(testUser);
        assertThat(analysis).isNotNull();
        assertThat(analysis.getCurrentHealthScore()).isNotNull();
        assertThat(analysis.getCurrentHealthScore()).isBetween(0, 100);
    }
    
    /**
     * Test complete system integration flow
     * Validates: All requirements - end-to-end system functionality
     */
    @Test
    void testCompleteSystemIntegration() {
        // Step 1: Government data integration
        LocationData locationData = ((GovernmentDataInterface) governmentDataService).fetchLocationData(
            testBusinessProfile.getCity(), testBusinessProfile.getBusinessType());
        assertThat(locationData).isNotNull();
        
        // Step 2: Cost analysis using government data
        CostAnalysis costAnalysis = feasibilityEngineService.calculateCosts(testBusinessProfile);
        assertThat(costAnalysis).isNotNull();
        assertThat(costAnalysis.getTotalCapex()).isGreaterThan(BigDecimal.ZERO);
        
        // Step 3: Create feasibility report
        FeasibilityReport report = new FeasibilityReport();
        report.setUser(testUser);
        report.setBusinessType(testBusinessProfile.getBusinessType());
        report.setCity(testBusinessProfile.getCity());
        report.setExecutiveSummary("Integrated system test report");
        report.setCapex(costAnalysis.getTotalCapex());
        report.setOpex(costAnalysis.getMonthlyOpex());
        report.setBreakEvenPoint(costAnalysis.getBreakEvenPoint());
        report.setGeneratedAt(LocalDateTime.now());
        report = feasibilityReportRepository.save(report);
        
        // Step 4: Generate PDF report
        byte[] pdfContent = feasibilityEngineService.generatePDFReport(report);
        assertThat(pdfContent).isNotNull();
        assertThat(pdfContent.length).isGreaterThan(1000);
        
        // Step 5: Add operational metrics
        addHistoricalMetricsData();
        
        // Step 6: Calculate health score
        Integer healthScore = healthScoreService.calculateHealthScore(
            new BigDecimal("15000"), new BigDecimal("8000"), new BigDecimal("500"));
        assertThat(healthScore).isNotNull();
        assertThat(healthScore).isBetween(0, 100);
        
        // Step 7: AI CFO analysis
        String aiAnalysis = aicfoService.processQuery(
            "Analyze my business performance and provide recommendations", 
            testBusinessProfile, 
            dailyMetricsRepository.findByUser(testUser)
        );
        assertThat(aiAnalysis).isNotNull();
        assertThat(aiAnalysis).isNotEmpty();
        
        // Step 8: Verify all data is properly linked
        Optional<BusinessProfile> profileOpt = businessProfileRepository.findByUser(testUser);
        assertThat(profileOpt).isPresent();
        
        Optional<FeasibilityReport> reportOpt = feasibilityReportRepository.findTopByUserOrderByGeneratedAtDesc(testUser);
        assertThat(reportOpt).isPresent();
        
        List<DailyMetrics> metrics = dailyMetricsRepository.findByUser(testUser);
        assertThat(metrics).isNotEmpty();
        
        // Verify system maintains data integrity
        assertThat(profileOpt.get().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(reportOpt.get().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(metrics.get(0).getUser().getId()).isEqualTo(testUser.getId());
    }
    
    /**
     * Test system resilience and error handling
     */
    @Test
    void testSystemResilienceAndErrorHandling() {
        // Test government data service with invalid city
        LocationData fallbackData = ((GovernmentDataInterface) governmentDataService).fetchLocationData("InvalidCity", BusinessType.CAFE);
        assertThat(fallbackData).isNotNull(); // Should return fallback data
        assertThat(fallbackData.getCity()).isEqualTo("InvalidCity");
        assertThat(fallbackData.getCommercialRentPerSqFt()).isGreaterThan(BigDecimal.ZERO);
        
        // Test feasibility engine with minimal data
        BusinessProfile minimalProfile = new BusinessProfile();
        minimalProfile.setUser(testUser);
        minimalProfile.setBusinessType(BusinessType.CAFE);
        minimalProfile.setCity("TestCity");
        // No seating capacity or other optional fields
        
        CostAnalysis minimalAnalysis = feasibilityEngineService.calculateCosts(minimalProfile);
        assertThat(minimalAnalysis).isNotNull();
        assertThat(minimalAnalysis.getTotalCapex()).isGreaterThan(BigDecimal.ZERO);
        assertThat(minimalAnalysis.getMonthlyOpex()).isGreaterThan(BigDecimal.ZERO);
        
        // Test AI CFO with no historical data
        String responseWithoutHistory = aicfoService.processQuery(
            "What should I do?", 
            testBusinessProfile, 
            List.of() // Empty history
        );
        assertThat(responseWithoutHistory).isNotNull();
        assertThat(responseWithoutHistory).isNotEmpty();
        
        // Test health score with minimal data
        Integer healthScoreNoData = healthScoreService.calculateHealthScore(
            new BigDecimal("10000"), new BigDecimal("5000"), new BigDecimal("100"));
        assertThat(healthScoreNoData).isNotNull();
        assertThat(healthScoreNoData).isBetween(0, 100);
    }
    
    // Helper methods
    
    private void addHistoricalMetricsData() {
        for (int i = 1; i <= 30; i++) {
            DailyMetrics metrics = new DailyMetrics();
            metrics.setUser(testUser);
            metrics.setDate(java.time.LocalDate.now().minusDays(i));
            metrics.setSales(new BigDecimal(15000 + (i * 100)));
            metrics.setExpenses(new BigDecimal(8000 + (i * 50)));
            metrics.setWastage(new BigDecimal(500 + (i * 10)));
            metrics.setHealthScore(75 + (i % 20));
            dailyMetricsRepository.save(metrics);
        }
    }
}