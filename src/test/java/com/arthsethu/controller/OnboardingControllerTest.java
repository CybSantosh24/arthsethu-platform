package com.arthsethu.controller;

import com.arthsethu.dto.OnboardingRequest;
import com.arthsethu.dto.QuestionnaireResponse;
import com.arthsethu.dto.QuestionnaireStep;
import com.arthsethu.model.BusinessProfile;
import com.arthsethu.model.BusinessType;
import com.arthsethu.model.User;
import com.arthsethu.service.OnboardingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OnboardingController.class)
class OnboardingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OnboardingService onboardingService;

    @Autowired
    private ObjectMapper objectMapper;

    private QuestionnaireStep businessTypeStep;
    private QuestionnaireStep seatingCapacityStep;
    private QuestionnaireResponse questionnaireResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        businessTypeStep = QuestionnaireStep.selectQuestion(
            "business_type",
            "What type of business are you planning to start?",
            java.util.Arrays.asList("CAFE", "CLOUD_KITCHEN", "MANUFACTURING"),
            true
        );

        seatingCapacityStep = QuestionnaireStep.numberQuestion(
            "seating_capacity",
            "How many seats will your cafe have?",
            true
        );

        questionnaireResponse = new QuestionnaireResponse();
        questionnaireResponse.setQuestionId("business_type");
        questionnaireResponse.setAnswer("CAFE");
        questionnaireResponse.setBusinessType(BusinessType.CAFE);
    }

    @Test
    void testStartQuestionnaire() throws Exception {
        // Given: Service returns first question
        when(onboardingService.getNextQuestion(eq((BusinessType) null), any(Map.class)))
            .thenReturn(businessTypeStep);

        // When & Then: GET /onboarding/start should return first question
        mockMvc.perform(get("/onboarding/start"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.questionId").value("business_type"))
            .andExpect(jsonPath("$.questionType").value("SELECT"))
            .andExpect(jsonPath("$.required").value(true))
            .andExpect(jsonPath("$.options").isArray());
    }

    @Test
    void testGetNextQuestion() throws Exception {
        // Given: Service returns next question
        when(onboardingService.getNextQuestion(eq(BusinessType.CAFE), any(Map.class)))
            .thenReturn(seatingCapacityStep);

        // When & Then: POST /onboarding/next-question should return next question
        mockMvc.perform(post("/onboarding/next-question")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questionnaireResponse)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.questionId").value("seating_capacity"))
            .andExpect(jsonPath("$.questionType").value("NUMBER"))
            .andExpect(jsonPath("$.required").value(true));
    }

    @Test
    void testGetNextQuestion_InvalidBusinessType() throws Exception {
        // Given: Invalid business type
        questionnaireResponse.setAnswer("INVALID_TYPE");
        when(onboardingService.getNextQuestion(any(BusinessType.class), any(Map.class)))
            .thenThrow(new IllegalArgumentException("Invalid business type"));

        // When & Then: Should return bad request
        mockMvc.perform(post("/onboarding/next-question")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questionnaireResponse)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testGetNextQuestion_ValidationError() throws Exception {
        // Given: Invalid request (missing required fields)
        QuestionnaireResponse invalidResponse = new QuestionnaireResponse();
        // Missing questionId and businessType

        // When & Then: Should return validation errors
        mockMvc.perform(post("/onboarding/next-question")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidResponse)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void testCompleteOnboarding() throws Exception {
        // Given: Complete onboarding request
        OnboardingRequest request = new OnboardingRequest();
        request.setBusinessType(BusinessType.CAFE);
        request.setCity("Mumbai");
        request.setSeatingCapacity(25);
        
        Map<String, Object> responses = new HashMap<>();
        responses.put("city", "Mumbai");
        responses.put("seating_capacity", 25);
        responses.put("menu_type", "Coffee & Snacks");
        request.setResponses(responses);

        BusinessProfile savedProfile = new BusinessProfile();
        savedProfile.setId(1L);

        when(onboardingService.isQuestionnaireComplete(eq(BusinessType.CAFE), any(Map.class)))
            .thenReturn(true);
        when(onboardingService.createBusinessProfile(any(OnboardingRequest.class), any(User.class)))
            .thenReturn(savedProfile);

        // When & Then: Should complete onboarding successfully
        mockMvc.perform(post("/onboarding/complete")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.profileId").value(1));
    }

    @Test
    void testCompleteOnboarding_MissingUser() throws Exception {
        // Given: Complete onboarding request but no user context
        OnboardingRequest request = new OnboardingRequest();
        request.setBusinessType(BusinessType.CAFE);
        request.setCity("Mumbai");

        // When & Then: Should return unauthorized due to missing authentication
        mockMvc.perform(post("/onboarding/complete")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void testCompleteOnboarding_IncompleteQuestionnaire() throws Exception {
        // Given: Incomplete onboarding request
        OnboardingRequest request = new OnboardingRequest();
        request.setBusinessType(BusinessType.CAFE);
        request.setCity("Mumbai");
        // Missing seating capacity

        when(onboardingService.isQuestionnaireComplete(eq(BusinessType.CAFE), any(Map.class)))
            .thenReturn(false);

        // When & Then: Should return bad request
        mockMvc.perform(post("/onboarding/complete")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testValidateResponse() throws Exception {
        // Given: Valid questionnaire response
        // When & Then: Should validate successfully
        mockMvc.perform(post("/onboarding/validate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questionnaireResponse)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").exists());
    }

    @Test
    void testGetProgress() throws Exception {
        // Given: Business type and responses
        when(onboardingService.isQuestionnaireComplete(eq(BusinessType.CAFE), any(Map.class)))
            .thenReturn(true);

        // When & Then: Should return progress information
        mockMvc.perform(get("/onboarding/progress")
                .param("businessType", "CAFE")
                .param("city", "Mumbai")
                .param("seating_capacity", "25"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.businessType").value("CAFE"))
            .andExpect(jsonPath("$.complete").value(true));
    }

    @Test
    void testGetProgress_InvalidBusinessType() throws Exception {
        // When & Then: Should return bad request for invalid business type
        mockMvc.perform(get("/onboarding/progress")
                .param("businessType", "INVALID_TYPE"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }
}