package com.arthsethu.integration;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.arthsethu.config.TestConfig;
import com.arthsethu.dto.QuestionnaireStep;
import com.arthsethu.model.BusinessType;
import com.arthsethu.service.OnboardingService;

/**
 * Integration test for the dynamic questionnaire system
 * Tests the core requirements without mocking
 */
@SpringBootTest
@Import(TestConfig.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.main.allow-bean-definition-overriding=true"
})
class OnboardingIntegrationTest {

    @Autowired
    private OnboardingService onboardingService;

    @Test
    void testDynamicQuestionnaireDecisionTree() {
        // Test Requirements 1.1, 1.2, 1.3, 1.4 - Dynamic questionnaire decision tree logic
        
        // Test 1: Start with business type selection
        Map<String, Object> responses = new HashMap<>();
        QuestionnaireStep step = onboardingService.getNextQuestion((BusinessType) null, responses);
        
        assertNotNull(step);
        assertEquals("business_type", step.getQuestionId());
        assertEquals("SELECT", step.getQuestionType());
        assertTrue(step.isRequired());
        assertNotNull(step.getOptions());
        assertTrue(step.getOptions().contains("CAFE"));
        assertTrue(step.getOptions().contains("CLOUD_KITCHEN"));
        assertTrue(step.getOptions().contains("MANUFACTURING"));
        
        // Test 2: CAFE business type asks for seating capacity (Requirement 1.1)
        responses.put("city", "Mumbai");
        step = onboardingService.getNextQuestion(BusinessType.CAFE, responses);
        
        assertNotNull(step);
        assertEquals("seating_capacity", step.getQuestionId());
        assertEquals("NUMBER", step.getQuestionType());
        assertTrue(step.isRequired());
        assertTrue(step.getQuestionText().toLowerCase().contains("seats"));
        
        // Test 3: CLOUD_KITCHEN skips seating, asks for packaging (Requirement 1.2)
        responses.clear();
        responses.put("city", "Delhi");
        step = onboardingService.getNextQuestion(BusinessType.CLOUD_KITCHEN, responses);
        
        assertNotNull(step);
        assertEquals("packaging_costs", step.getQuestionId());
        assertEquals("NUMBER", step.getQuestionType());
        assertTrue(step.isRequired());
        assertTrue(step.getQuestionText().toLowerCase().contains("packaging"));
        
        // Test 4: MANUFACTURING asks for power consumption (Requirement 1.3)
        responses.clear();
        responses.put("city", "Bangalore");
        step = onboardingService.getNextQuestion(BusinessType.MANUFACTURING, responses);
        
        assertNotNull(step);
        assertEquals("power_consumption", step.getQuestionId());
        assertEquals("NUMBER", step.getQuestionType());
        assertTrue(step.isRequired());
        assertTrue(step.getQuestionText().toLowerCase().contains("power"));
        
        // Test 5: MANUFACTURING then asks for raw material sourcing (Requirement 1.3)
        responses.put("power_consumption", 1000.0);
        step = onboardingService.getNextQuestion(BusinessType.MANUFACTURING, responses);
        
        assertNotNull(step);
        assertEquals("raw_material_sourcing", step.getQuestionId());
        assertEquals("SELECT", step.getQuestionType());
        assertTrue(step.isRequired());
        assertNotNull(step.getOptions());
        assertTrue(step.getOptions().contains("Local Suppliers"));
    }

    @Test
    void testQuestionnaireCompleteness() {
        // Test questionnaire completion logic for different business types
        
        // Test CAFE completeness
        Map<String, Object> cafeResponses = new HashMap<>();
        cafeResponses.put("city", "Mumbai");
        cafeResponses.put("seating_capacity", 20);
        cafeResponses.put("menu_type", "Coffee & Snacks");
        
        assertTrue(onboardingService.isQuestionnaireComplete(BusinessType.CAFE, cafeResponses));
        
        // Test incomplete CAFE (missing seating_capacity)
        cafeResponses.remove("seating_capacity");
        assertFalse(onboardingService.isQuestionnaireComplete(BusinessType.CAFE, cafeResponses));
        
        // Test CLOUD_KITCHEN completeness
        Map<String, Object> cloudKitchenResponses = new HashMap<>();
        cloudKitchenResponses.put("city", "Delhi");
        cloudKitchenResponses.put("packaging_costs", 5000.0);
        cloudKitchenResponses.put("delivery_radius", 10);
        cloudKitchenResponses.put("cuisine_type", "Indian");
        
        assertTrue(onboardingService.isQuestionnaireComplete(BusinessType.CLOUD_KITCHEN, cloudKitchenResponses));
        
        // Test MANUFACTURING completeness
        Map<String, Object> manufacturingResponses = new HashMap<>();
        manufacturingResponses.put("city", "Pune");
        manufacturingResponses.put("power_consumption", 2000.0);
        manufacturingResponses.put("raw_material_sourcing", "Local Suppliers");
        manufacturingResponses.put("production_capacity", 1000);
        
        assertTrue(onboardingService.isQuestionnaireComplete(BusinessType.MANUFACTURING, manufacturingResponses));
    }

    @Test
    void testCityRequiredFirst() {
        // Test that city is always required before business-specific questions
        Map<String, Object> responses = new HashMap<>();
        
        // For CAFE without city
        QuestionnaireStep step = onboardingService.getNextQuestion(BusinessType.CAFE, responses);
        assertNotNull(step);
        assertEquals("city", step.getQuestionId());
        assertEquals("TEXT", step.getQuestionType());
        assertTrue(step.isRequired());
        
        // For CLOUD_KITCHEN without city
        step = onboardingService.getNextQuestion(BusinessType.CLOUD_KITCHEN, responses);
        assertNotNull(step);
        assertEquals("city", step.getQuestionId());
        
        // For MANUFACTURING without city
        step = onboardingService.getNextQuestion(BusinessType.MANUFACTURING, responses);
        assertNotNull(step);
        assertEquals("city", step.getQuestionId());
    }

    @Test
    void testQuestionnaireFlow() {
        // Test complete questionnaire flow for CAFE
        Map<String, Object> responses = new HashMap<>();
        
        // Step 1: Business type selection (would be handled by controller)
        // Step 2: City required
        QuestionnaireStep step = onboardingService.getNextQuestion(BusinessType.CAFE, responses);
        assertEquals("city", step.getQuestionId());
        
        // Step 3: Add city, get seating capacity question
        responses.put("city", "Mumbai");
        step = onboardingService.getNextQuestion(BusinessType.CAFE, responses);
        assertEquals("seating_capacity", step.getQuestionId());
        
        // Step 4: Add seating capacity, get menu type question
        responses.put("seating_capacity", 25);
        step = onboardingService.getNextQuestion(BusinessType.CAFE, responses);
        assertEquals("menu_type", step.getQuestionId());
        
        // Step 5: Add menu type, questionnaire should be complete
        responses.put("menu_type", "Full Meals");
        step = onboardingService.getNextQuestion(BusinessType.CAFE, responses);
        assertEquals("complete", step.getQuestionId());
        assertTrue(step.isComplete());
        
        // Verify questionnaire is complete
        assertTrue(onboardingService.isQuestionnaireComplete(BusinessType.CAFE, responses));
    }

    @Test
    void testBusinessTypeSpecificQuestions() {
        // Test that different business types get different questions
        Map<String, Object> responses = new HashMap<>();
        responses.put("city", "TestCity");
        
        // CAFE gets seating capacity
        QuestionnaireStep cafeStep = onboardingService.getNextQuestion(BusinessType.CAFE, responses);
        assertEquals("seating_capacity", cafeStep.getQuestionId());
        
        // CLOUD_KITCHEN gets packaging costs
        QuestionnaireStep cloudKitchenStep = onboardingService.getNextQuestion(BusinessType.CLOUD_KITCHEN, responses);
        assertEquals("packaging_costs", cloudKitchenStep.getQuestionId());
        
        // MANUFACTURING gets power consumption
        QuestionnaireStep manufacturingStep = onboardingService.getNextQuestion(BusinessType.MANUFACTURING, responses);
        assertEquals("power_consumption", manufacturingStep.getQuestionId());
        
        // RETAIL gets store size
        QuestionnaireStep retailStep = onboardingService.getNextQuestion(BusinessType.RETAIL, responses);
        assertEquals("store_size", retailStep.getQuestionId());
        
        // SERVICE gets service type
        QuestionnaireStep serviceStep = onboardingService.getNextQuestion(BusinessType.SERVICE, responses);
        assertEquals("service_type", serviceStep.getQuestionId());
    }
}