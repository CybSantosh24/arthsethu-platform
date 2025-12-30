package com.arthsethu.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arthsethu.dto.OnboardingRequest;
import com.arthsethu.dto.QuestionnaireStep;
import com.arthsethu.model.BusinessProfile;
import com.arthsethu.model.BusinessType;
import com.arthsethu.model.User;
import com.arthsethu.repository.BusinessProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OnboardingService {
    
    @Autowired
    private BusinessProfileRepository businessProfileRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Gets the next question in the questionnaire based on business type and previous responses
     * Implements decision tree logic as per Requirements 1.1, 1.2, 1.3, 1.4
     */
    public QuestionnaireStep getNextQuestion(String businessType, Map<String, Object> responses) {
        BusinessType type = null;
        if (businessType != null && !businessType.isEmpty()) {
            try {
                type = BusinessType.valueOf(businessType.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid business type, treat as null
            }
        }
        return getNextQuestion(type, responses);
    }
    
    public QuestionnaireStep getNextQuestion(BusinessType businessType, Map<String, Object> responses) {
        if (responses == null) {
            responses = new HashMap<>();
        }
        
        // Start with business type selection if not provided
        if (businessType == null) {
            QuestionnaireStep step = QuestionnaireStep.selectQuestion(
                "business_type",
                "What type of business are you planning to start?",
                Arrays.asList("CAFE", "CLOUD_KITCHEN", "MANUFACTURING", "RETAIL", "SERVICE"),
                true
            );
            step.setQuestionText("What type of business are you planning to start?");
            return step;
        }
        
        // City is always required after business type
        if (!responses.containsKey("city")) {
            QuestionnaireStep step = QuestionnaireStep.textQuestion(
                "city",
                "In which city are you planning to start your business?",
                true
            );
            return step;
        }
        
        // Business-specific questions based on decision tree logic
        switch (businessType) {
            case CAFE:
                return getCafeQuestions(responses);
            case CLOUD_KITCHEN:
                return getCloudKitchenQuestions(responses);
            case MANUFACTURING:
                return getManufacturingQuestions(responses);
            case RETAIL:
                return getRetailQuestions(responses);
            case SERVICE:
                return getServiceQuestions(responses);
            default:
                return createCompletionStep();
        }
    }
    
    /**
     * Decision tree for Cafe business type - asks about seating capacity
     */
    private QuestionnaireStep getCafeQuestions(Map<String, Object> responses) {
        if (!responses.containsKey("seating_capacity")) {
            return QuestionnaireStep.numberQuestion(
                "seating_capacity",
                "How many seats will your cafe have?",
                true
            );
        }
        
        if (!responses.containsKey("menu_type")) {
            return QuestionnaireStep.selectQuestion(
                "menu_type",
                "What type of menu will you offer?",
                Arrays.asList("Coffee & Snacks", "Full Meals", "Beverages Only", "Mixed Menu"),
                true
            );
        }
        
        return createCompletionStep();
    }
    
    /**
     * Decision tree for Cloud Kitchen - skips seating questions, asks about packaging
     */
    private QuestionnaireStep getCloudKitchenQuestions(Map<String, Object> responses) {
        if (!responses.containsKey("packaging_costs")) {
            return QuestionnaireStep.numberQuestion(
                "packaging_costs",
                "What is your estimated monthly packaging cost (in ₹)?",
                true
            );
        }
        
        if (!responses.containsKey("delivery_radius")) {
            return QuestionnaireStep.numberQuestion(
                "delivery_radius",
                "What will be your delivery radius (in km)?",
                true
            );
        }
        
        if (!responses.containsKey("cuisine_type")) {
            return QuestionnaireStep.selectQuestion(
                "cuisine_type",
                "What type of cuisine will you specialize in?",
                Arrays.asList("Indian", "Chinese", "Continental", "Italian", "Multi-cuisine"),
                true
            );
        }
        
        return createCompletionStep();
    }
    
    /**
     * Decision tree for Manufacturing - asks about power consumption and raw materials
     */
    private QuestionnaireStep getManufacturingQuestions(Map<String, Object> responses) {
        if (!responses.containsKey("power_consumption")) {
            return QuestionnaireStep.numberQuestion(
                "power_consumption",
                "What is your estimated monthly power consumption (in kWh)?",
                true
            );
        }
        
        if (!responses.containsKey("raw_material_sourcing")) {
            return QuestionnaireStep.selectQuestion(
                "raw_material_sourcing",
                "Where will you source your raw materials from?",
                Arrays.asList("Local Suppliers", "National Suppliers", "International Import", "Mixed Sources"),
                true
            );
        }
        
        if (!responses.containsKey("production_capacity")) {
            return QuestionnaireStep.numberQuestion(
                "production_capacity",
                "What is your planned monthly production capacity (in units)?",
                true
            );
        }
        
        return createCompletionStep();
    }
    
    /**
     * Decision tree for Retail business
     */
    private QuestionnaireStep getRetailQuestions(Map<String, Object> responses) {
        if (!responses.containsKey("store_size")) {
            return QuestionnaireStep.numberQuestion(
                "store_size",
                "What is your planned store size (in sq ft)?",
                true
            );
        }
        
        if (!responses.containsKey("product_category")) {
            return QuestionnaireStep.selectQuestion(
                "product_category",
                "What category of products will you sell?",
                Arrays.asList("Clothing", "Electronics", "Groceries", "Books", "General Merchandise"),
                true
            );
        }
        
        return createCompletionStep();
    }
    
    /**
     * Decision tree for Service business
     */
    private QuestionnaireStep getServiceQuestions(Map<String, Object> responses) {
        if (!responses.containsKey("service_type")) {
            return QuestionnaireStep.selectQuestion(
                "service_type",
                "What type of service will you provide?",
                Arrays.asList("Consulting", "IT Services", "Healthcare", "Education", "Financial Services"),
                true
            );
        }
        
        if (!responses.containsKey("team_size")) {
            return QuestionnaireStep.numberQuestion(
                "team_size",
                "How many team members do you plan to hire initially?",
                true
            );
        }
        
        return createCompletionStep();
    }
    
    /**
     * Creates a completion step indicating questionnaire is finished
     */
    private QuestionnaireStep createCompletionStep() {
        QuestionnaireStep step = new QuestionnaireStep();
        step.setQuestionId("complete");
        step.setQuestionText("Questionnaire completed successfully!");
        step.setQuestionType("COMPLETION");
        step.setComplete(true);
        return step;
    }
    
    /**
     * Creates a business profile from completed onboarding request
     * Implements Requirements 1.5
     */
    public BusinessProfile createBusinessProfile(OnboardingRequest request, User user) {
        BusinessProfile profile = new BusinessProfile(user, request.getBusinessType(), request.getCity());
        
        // Set business-specific fields based on type
        if (request.getBusinessType().requiresSeatingCapacity()) {
            profile.setSeatingCapacity(request.getSeatingCapacity());
        }
        
        if (request.getBusinessType().requiresPackagingCosts()) {
            profile.setPackagingCosts(request.getPackagingCosts());
        }
        
        if (request.getBusinessType().requiresPowerConsumption()) {
            profile.setPowerConsumption(request.getPowerConsumption());
            profile.setRawMaterialSourcing(request.getRawMaterialSourcing());
        }
        
        // Store all questionnaire responses as JSON
        try {
            String responsesJson = objectMapper.writeValueAsString(request.getResponses());
            profile.setQuestionnaireResponses(responsesJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize questionnaire responses", e);
        }
        
        return businessProfileRepository.save(profile);
    }
    
    /**
     * Validates if questionnaire is complete for the given business type
     */
    public boolean isQuestionnaireComplete(BusinessType businessType, Map<String, Object> responses) {
        // City is always required
        if (!responses.containsKey("city")) {
            return false;
        }
        
        // Check business-specific required fields
        switch (businessType) {
            case CAFE:
                return responses.containsKey("seating_capacity") && 
                       responses.containsKey("menu_type");
            case CLOUD_KITCHEN:
                return responses.containsKey("packaging_costs") && 
                       responses.containsKey("delivery_radius") && 
                       responses.containsKey("cuisine_type");
            case MANUFACTURING:
                return responses.containsKey("power_consumption") && 
                       responses.containsKey("raw_material_sourcing") && 
                       responses.containsKey("production_capacity");
            case RETAIL:
                return responses.containsKey("store_size") && 
                       responses.containsKey("product_category");
            case SERVICE:
                return responses.containsKey("service_type") && 
                       responses.containsKey("team_size");
            default:
                return true;
        }
    }
}