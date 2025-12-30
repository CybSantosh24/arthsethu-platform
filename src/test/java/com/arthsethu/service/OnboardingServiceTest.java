package com.arthsethu.service;

import com.arthsethu.dto.OnboardingRequest;
import com.arthsethu.dto.QuestionnaireStep;
import com.arthsethu.model.BusinessProfile;
import com.arthsethu.model.BusinessType;
import com.arthsethu.model.User;
import com.arthsethu.repository.BusinessProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    @Mock
    private BusinessProfileRepository businessProfileRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OnboardingService onboardingService;

    private User testUser;
    private Map<String, Object> responses;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        
        responses = new HashMap<>();
    }

    @Test
    void testGetNextQuestion_StartWithBusinessType() {
        // When no business type is provided, should return business type selection
        QuestionnaireStep step = onboardingService.getNextQuestion((BusinessType) null, responses);
        
        assertNotNull(step);
        assertEquals("business_type", step.getQuestionId());
        assertEquals("SELECT", step.getQuestionType());
        assertTrue(step.isRequired());
        assertNotNull(step.getOptions());
        assertTrue(step.getOptions().contains("CAFE"));
        assertTrue(step.getOptions().contains("CLOUD_KITCHEN"));
        assertTrue(step.getOptions().contains("MANUFACTURING"));
    }

    @Test
    void testGetNextQuestion_CafeBusinessType_AsksForSeatingCapacity() {
        // Given: Business type is CAFE and city is provided
        responses.put("city", "Mumbai");
        
        // When: Getting next question for CAFE
        QuestionnaireStep step = onboardingService.getNextQuestion(BusinessType.CAFE, responses);
        
        // Then: Should ask for seating capacity (Requirements 1.1)
        assertNotNull(step);
        assertEquals("seating_capacity", step.getQuestionId());
        assertEquals("NUMBER", step.getQuestionType());
        assertTrue(step.isRequired());
        assertTrue(step.getQuestionText().toLowerCase().contains("seats"));
    }

    @Test
    void testGetNextQuestion_CloudKitchen_SkipsSeatingAsksPackaging() {
        // Given: Business type is CLOUD_KITCHEN and city is provided
        responses.put("city", "Delhi");
        
        // When: Getting next question for CLOUD_KITCHEN
        QuestionnaireStep step = onboardingService.getNextQuestion(BusinessType.CLOUD_KITCHEN, responses);
        
        // Then: Should ask for packaging costs, not seating (Requirements 1.2)
        assertNotNull(step);
        assertEquals("packaging_costs", step.getQuestionId());
        assertEquals("NUMBER", step.getQuestionType());
        assertTrue(step.isRequired());
        assertTrue(step.getQuestionText().toLowerCase().contains("packaging"));
    }

    @Test
    void testGetNextQuestion_Manufacturing_AsksPowerConsumption() {
        // Given: Business type is MANUFACTURING and city is provided
        responses.put("city", "Bangalore");
        
        // When: Getting next question for MANUFACTURING
        QuestionnaireStep step = onboardingService.getNextQuestion(BusinessType.MANUFACTURING, responses);
        
        // Then: Should ask for power consumption (Requirements 1.3)
        assertNotNull(step);
        assertEquals("power_consumption", step.getQuestionId());
        assertEquals("NUMBER", step.getQuestionType());
        assertTrue(step.isRequired());
        assertTrue(step.getQuestionText().toLowerCase().contains("power"));
    }

    @Test
    void testGetNextQuestion_Manufacturing_AsksRawMaterialSourcing() {
        // Given: Business type is MANUFACTURING with power consumption provided
        responses.put("city", "Chennai");
        responses.put("power_consumption", 1000.0);
        
        // When: Getting next question for MANUFACTURING
        QuestionnaireStep step = onboardingService.getNextQuestion(BusinessType.MANUFACTURING, responses);
        
        // Then: Should ask for raw material sourcing (Requirements 1.3)
        assertNotNull(step);
        assertEquals("raw_material_sourcing", step.getQuestionId());
        assertEquals("SELECT", step.getQuestionType());
        assertTrue(step.isRequired());
        assertNotNull(step.getOptions());
        assertTrue(step.getOptions().contains("Local Suppliers"));
    }

    @Test
    void testGetNextQuestion_RequiresCityFirst() {
        // Given: Business type is provided but no city
        // When: Getting next question
        QuestionnaireStep step = onboardingService.getNextQuestion(BusinessType.CAFE, responses);
        
        // Then: Should ask for city first
        assertNotNull(step);
        assertEquals("city", step.getQuestionId());
        assertEquals("TEXT", step.getQuestionType());
        assertTrue(step.isRequired());
    }

    @Test
    void testIsQuestionnaireComplete_Cafe() {
        // Given: Complete responses for CAFE
        responses.put("city", "Mumbai");
        responses.put("seating_capacity", 20);
        responses.put("menu_type", "Coffee & Snacks");
        
        // When: Checking completeness
        boolean isComplete = onboardingService.isQuestionnaireComplete(BusinessType.CAFE, responses);
        
        // Then: Should be complete
        assertTrue(isComplete);
    }

    @Test
    void testIsQuestionnaireComplete_CloudKitchen() {
        // Given: Complete responses for CLOUD_KITCHEN
        responses.put("city", "Delhi");
        responses.put("packaging_costs", 5000.0);
        responses.put("delivery_radius", 10);
        responses.put("cuisine_type", "Indian");
        
        // When: Checking completeness
        boolean isComplete = onboardingService.isQuestionnaireComplete(BusinessType.CLOUD_KITCHEN, responses);
        
        // Then: Should be complete
        assertTrue(isComplete);
    }

    @Test
    void testIsQuestionnaireComplete_Manufacturing() {
        // Given: Complete responses for MANUFACTURING
        responses.put("city", "Pune");
        responses.put("power_consumption", 2000.0);
        responses.put("raw_material_sourcing", "Local Suppliers");
        responses.put("production_capacity", 1000);
        
        // When: Checking completeness
        boolean isComplete = onboardingService.isQuestionnaireComplete(BusinessType.MANUFACTURING, responses);
        
        // Then: Should be complete
        assertTrue(isComplete);
    }

    @Test
    void testIsQuestionnaireComplete_Incomplete() {
        // Given: Incomplete responses for CAFE (missing seating_capacity)
        responses.put("city", "Mumbai");
        
        // When: Checking completeness
        boolean isComplete = onboardingService.isQuestionnaireComplete(BusinessType.CAFE, responses);
        
        // Then: Should not be complete
        assertFalse(isComplete);
    }

    @Test
    void testCreateBusinessProfile() throws Exception {
        // Given: Complete onboarding request
        OnboardingRequest request = new OnboardingRequest();
        request.setBusinessType(BusinessType.CAFE);
        request.setCity("Mumbai");
        request.setSeatingCapacity(25);
        request.addResponse("menu_type", "Full Meals");
        
        BusinessProfile savedProfile = new BusinessProfile(testUser, BusinessType.CAFE, "Mumbai");
        savedProfile.setId(1L);
        
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"menu_type\":\"Full Meals\"}");
        when(businessProfileRepository.save(any(BusinessProfile.class))).thenReturn(savedProfile);
        
        // When: Creating business profile
        BusinessProfile result = onboardingService.createBusinessProfile(request, testUser);
        
        // Then: Should create profile with correct data (Requirements 1.5)
        assertNotNull(result);
        assertEquals(BusinessType.CAFE, result.getBusinessType());
        assertEquals("Mumbai", result.getCity());
        assertEquals(Integer.valueOf(25), result.getSeatingCapacity());
        assertEquals(testUser, result.getUser());
    }
}