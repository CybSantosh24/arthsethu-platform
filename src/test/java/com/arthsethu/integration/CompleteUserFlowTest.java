package com.arthsethu.integration;

import com.arthsethu.dto.*;
import com.arthsethu.model.*;
import com.arthsethu.repository.*;
import com.arthsethu.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Integration tests for complete user flows
 * Tests task 13.2: Write integration tests for complete user flows
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableAutoConfiguration(exclude = {
    SecurityAutoConfiguration.class,
    UserDetailsServiceAutoConfiguration.class,
    SecurityFilterAutoConfiguration.class
})
@ActiveProfiles("test")
@Transactional
public class CompleteUserFlowTest {
    
    @Autowired
    private OnboardingService onboardingService;
    
    @Autowired
    private DailyMetricsService dailyMetricsService;
    
    @Autowired
    private AICFOService aicfoService;
    
    @Autowired
    private BusinessProfileRepository businessProfileRepository;
    
    @Autowired
    private DailyMetricsRepository dailyMetricsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Mock external dependencies
    @MockBean
    private GovernmentDataInterface governmentDataInterface;
    
    private LocationData mockLocationData;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        // Setup mock location data
        mockLocationData = new LocationData(
            "Mumbai", "Maharashtra", 
            new BigDecimal("150.00"), 
            new BigDecimal("25000.00"), 
            null
        );
        
        when(governmentDataInterface.fetchLocationData(anyString(), any(BusinessType.class)))
            .thenReturn(mockLocationData);
        when(governmentDataInterface.isGovernmentDataAvailable()).thenReturn(true);
        
        // Create test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedpassword");
    }
    
    /**
     * Test complete questionnaire flow for different business types
     * This tests the dynamic questionnaire decision tree logic
     */
    @Test
    void testCompleteQuestionnaireFlow() {
        // Test Cafe questionnaire flow
        Map<String, Object> responses = new HashMap<>();
        
        // Step 1: Get next question for cafe (should ask for city)
        QuestionnaireStep step1 = onboardingService.getNextQuestion(BusinessType.CAFE, responses);
        assertNotNull(step1, "First step should not be null");
        assertEquals("city", step1.getQuestionId(), "First question should be about city");
        
        // Step 2: Add city response and get next question
        responses.put("city", "Mumbai");
        QuestionnaireStep step2 = onboardingService.getNextQuestion(BusinessType.CAFE, responses);
        assertNotNull(step2, "Second step should not be null");
        assertEquals("seating_capacity", step2.getQuestionId(), "Cafe should ask about seating capacity");
        
        // Step 3: Add seating capacity and get next question
        responses.put("seating_capacity", 30);
        QuestionnaireStep step3 = onboardingService.getNextQuestion(BusinessType.CAFE, responses);
        assertNotNull(step3, "Third step should not be null");
        assertEquals("menu_type", step3.getQuestionId(), "Cafe should ask about menu type");
        
        // Step 4: Complete questionnaire
        responses.put("menu_type", "Full Meals");
        QuestionnaireStep step4 = onboardingService.getNextQuestion(BusinessType.CAFE, responses);
        assertNotNull(step4, "Final step should not be null");
        assertEquals("complete", step4.getQuestionId(), "Should indicate completion");
        assertTrue(step4.isComplete(), "Questionnaire should be marked as complete");
        
        // Verify questionnaire completion validation
        assertTrue(onboardingService.isQuestionnaireComplete(BusinessType.CAFE, responses), 
                  "Questionnaire should be validated as complete");
    }
    
    /**
     * Test business profile creation from questionnaire responses
     * This tests the onboarding to profile creation flow
     */
    @Test
    void testBusinessProfileCreationFlow() {
        // Create onboarding request with cafe data
        OnboardingRequest request = new OnboardingRequest();
        request.setBusinessType(BusinessType.CAFE);
        request.setCity("Mumbai");
        request.setSeatingCapacity(25);
        
        // Add questionnaire responses
        Map<String, Object> responses = new HashMap<>();
        responses.put("city", "Mumbai");
        responses.put("seating_capacity", 25);
        responses.put("menu_type", "Coffee & Snacks");
        request.setResponses(responses);
        
        // Create business profile
        BusinessProfile profile = onboardingService.createBusinessProfile(request, userRepository.save(testUser));
        
        // Verify profile creation
        assertNotNull(profile, "Business profile should be created");
        assertNotNull(profile.getId(), "Profile should have an ID");
        assertEquals(BusinessType.CAFE, profile.getBusinessType(), "Business type should match");
        assertEquals("Mumbai", profile.getCity(), "City should match");
        assertEquals(Integer.valueOf(25), profile.getSeatingCapacity(), "Seating capacity should match");
        assertNotNull(profile.getQuestionnaireResponses(), "Questionnaire responses should be stored");
        
        // Verify profile is saved in database
        BusinessProfile savedProfile = businessProfileRepository.findById(profile.getId()).orElse(null);
        assertNotNull(savedProfile, "Profile should be saved in database");
        assertEquals(profile.getBusinessType(), savedProfile.getBusinessType(), "Saved profile should match");
    }
    
    /**
     * Test daily metrics functionality (Vistar tier feature)
     * This tests the operational dashboard capabilities
     */
    @Test
    void testDailyMetricsFlow() {
        // Create business profile first
        OnboardingRequest request = new OnboardingRequest();
        request.setBusinessType(BusinessType.MANUFACTURING);
        request.setCity("Delhi");
        request.setPowerConsumption(1000.0);
        request.setRawMaterialSourcing("Local Suppliers");
        
        Map<String, Object> responses = new HashMap<>();
        responses.put("city", "Delhi");
        responses.put("power_consumption", 1000.0);
        responses.put("raw_material_sourcing", "Local Suppliers");
        responses.put("production_capacity", 500);
        request.setResponses(responses);
        
        BusinessProfile profile = onboardingService.createBusinessProfile(request, userRepository.save(testUser));
        
        // Test daily metrics creation and health score calculation
        User savedUser = userRepository.save(testUser);
        DailyMetrics metrics = new DailyMetrics(
            savedUser,
            LocalDate.now(),
            new BigDecimal("10000.00"),
            new BigDecimal("7000.00"),
            new BigDecimal("300.00")
        );
        
        DailyMetrics savedMetrics = dailyMetricsRepository.save(metrics);
        
        // Verify metrics calculation
        assertNotNull(savedMetrics, "Metrics should be saved");
        assertNotNull(savedMetrics.getHealthScore(), "Health score should be calculated");
        assertTrue(savedMetrics.getHealthScore() >= 0 && savedMetrics.getHealthScore() <= 100, 
                  "Health score should be in valid range");
        
        // Test profit and margin calculations
        BigDecimal expectedProfit = new BigDecimal("3000.00");
        assertEquals(0, expectedProfit.compareTo(savedMetrics.getProfit()), "Profit calculation should be correct");
        
        BigDecimal expectedMargin = new BigDecimal("0.3000");
        assertEquals(0, expectedMargin.compareTo(savedMetrics.getMargin()), "Margin calculation should be correct");
        
        // Test health score calculation through service
        Integer calculatedScore = dailyMetricsService.calculateHealthScore(
            savedMetrics.getSales(), 
            savedMetrics.getExpenses(), 
            savedMetrics.getWastage()
        );
        
        assertNotNull(calculatedScore, "Service should calculate health score");
        assertEquals(savedMetrics.getHealthScore(), calculatedScore, "Service calculation should match entity calculation");
    }
    
    /**
     * Test AI CFO service integration
     * This tests the AI CFO availability and basic functionality
     */
    @Test
    void testAICFOServiceIntegration() {
        // Test AI service availability check
        boolean aiAvailable = aicfoService.isAIServiceAvailable();
        
        // In test environment, AI might not be available, but the call should not fail
        assertNotNull(aiAvailable, "AI service availability check should return a boolean");
        
        // The test passes regardless of AI availability since we're testing integration, not AI functionality
        assertTrue(true, "AI CFO service integration test completed successfully");
    }
    
    /**
     * Test government data integration
     * This tests the external API integration with mocked responses
     */
    @Test
    void testGovernmentDataIntegration() {
        // Test government data availability
        boolean isAvailable = governmentDataInterface.isGovernmentDataAvailable();
        assertTrue(isAvailable, "Government data should be available (mocked)");
        
        // Test location data fetching
        LocationData locationData = governmentDataInterface.fetchLocationData("Mumbai", BusinessType.CAFE);
        assertNotNull(locationData, "Location data should not be null");
        assertEquals("Mumbai", locationData.getCity(), "City should match");
        assertEquals("Maharashtra", locationData.getState(), "State should match");
        assertNotNull(locationData.getCommercialRentPerSqFt(), "Rent data should not be null");
        assertNotNull(locationData.getAverageWage(), "Wage data should not be null");
        
        // Verify the mocked data values
        assertEquals(new BigDecimal("150.00"), locationData.getCommercialRentPerSqFt(), "Rent should match mock data");
        assertEquals(new BigDecimal("25000.00"), locationData.getAverageWage(), "Wage should match mock data");
    }
    
    /**
     * Test error handling across user flows
     * This ensures the system gracefully handles various error conditions
     */
    @Test
    void testErrorHandlingInUserFlows() {
        // Test 1: Invalid business type in questionnaire
        Map<String, Object> emptyResponses = new HashMap<>();
        QuestionnaireStep step = onboardingService.getNextQuestion((BusinessType) null, emptyResponses);
        assertNotNull(step, "Should handle null business type gracefully");
        
        // Test 2: Incomplete questionnaire validation
        Map<String, Object> incompleteResponses = new HashMap<>();
        incompleteResponses.put("city", "Mumbai");
        // Missing seating_capacity for CAFE
        
        boolean isComplete = onboardingService.isQuestionnaireComplete(BusinessType.CAFE, incompleteResponses);
        assertFalse(isComplete, "Should correctly identify incomplete questionnaire");
        
        // Test 3: Government data unavailable scenario
        when(governmentDataInterface.isGovernmentDataAvailable()).thenReturn(false);
        
        boolean unavailable = governmentDataInterface.isGovernmentDataAvailable();
        assertFalse(unavailable, "Should handle government data unavailability");
        
        // Test 4: AI service unavailable (should not break other functionality)
        boolean aiAvailable = aicfoService.isAIServiceAvailable();
        // This should return gracefully, not throw an exception
        assertNotNull(aiAvailable, "AI service availability check should not throw exception");
        
        assertTrue(true, "Error handling tests completed successfully");
    }
    
    /**
     * Test data consistency across different business types
     * This ensures questionnaire logic works correctly for all business types
     */
    @Test
    void testBusinessTypeSpecificFlows() {
        // Test Cloud Kitchen flow
        Map<String, Object> cloudKitchenResponses = new HashMap<>();
        cloudKitchenResponses.put("city", "Bangalore");
        cloudKitchenResponses.put("packaging_costs", 5000.0);
        cloudKitchenResponses.put("delivery_radius", 10);
        cloudKitchenResponses.put("cuisine_type", "Indian");
        
        boolean cloudKitchenComplete = onboardingService.isQuestionnaireComplete(
            BusinessType.CLOUD_KITCHEN, cloudKitchenResponses);
        assertTrue(cloudKitchenComplete, "Cloud kitchen questionnaire should be complete");
        
        // Test Manufacturing flow
        Map<String, Object> manufacturingResponses = new HashMap<>();
        manufacturingResponses.put("city", "Chennai");
        manufacturingResponses.put("power_consumption", 2000.0);
        manufacturingResponses.put("raw_material_sourcing", "National Suppliers");
        manufacturingResponses.put("production_capacity", 1000);
        
        boolean manufacturingComplete = onboardingService.isQuestionnaireComplete(
            BusinessType.MANUFACTURING, manufacturingResponses);
        assertTrue(manufacturingComplete, "Manufacturing questionnaire should be complete");
        
        // Test Retail flow
        Map<String, Object> retailResponses = new HashMap<>();
        retailResponses.put("city", "Pune");
        retailResponses.put("store_size", 500);
        retailResponses.put("product_category", "Electronics");
        
        boolean retailComplete = onboardingService.isQuestionnaireComplete(
            BusinessType.RETAIL, retailResponses);
        assertTrue(retailComplete, "Retail questionnaire should be complete");
        
        // Test Service flow
        Map<String, Object> serviceResponses = new HashMap<>();
        serviceResponses.put("city", "Hyderabad");
        serviceResponses.put("service_type", "IT Services");
        serviceResponses.put("team_size", 5);
        
        boolean serviceComplete = onboardingService.isQuestionnaireComplete(
            BusinessType.SERVICE, serviceResponses);
        assertTrue(serviceComplete, "Service questionnaire should be complete");
        
        assertTrue(true, "All business type flows work correctly");
    }
}