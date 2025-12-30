package com.arthsethu.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arthsethu.dto.OnboardingRequest;
import com.arthsethu.dto.QuestionnaireResponse;
import com.arthsethu.dto.QuestionnaireStep;
import com.arthsethu.model.BusinessProfile;
import com.arthsethu.model.BusinessType;
import com.arthsethu.model.User;
import com.arthsethu.service.OnboardingService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/onboarding")
public class OnboardingController {
    
    @Autowired
    private OnboardingService onboardingService;
    
    /**
     * Display onboarding page
     */
    @GetMapping
    public String showOnboardingPage() {
        return "onboarding";
    }
    
    /**
     * Get the first question in the questionnaire
     */
    @GetMapping("/start")
    @ResponseBody
    public ResponseEntity<QuestionnaireStep> startQuestionnaire() {
        try {
            QuestionnaireStep firstStep = onboardingService.getNextQuestion((BusinessType) null, new HashMap<>());
            return ResponseEntity.ok(firstStep);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get the next question based on business type and previous responses
     */
    @PostMapping("/next-question")
    @ResponseBody
    public ResponseEntity<?> getNextQuestion(@RequestBody Map<String, Object> request) {
        try {
            String questionId = (String) request.get("questionId");
            Object answer = request.get("answer");
            String businessType = (String) request.get("businessType");
            Map<String, Object> previousResponses = (Map<String, Object>) request.get("previousResponses");
            
            if (previousResponses == null) {
                previousResponses = new HashMap<>();
            }
            
            // Add current answer to previous responses
            if (questionId != null && answer != null) {
                previousResponses.put(questionId, answer);
            }
            
            // Determine business type
            BusinessType type = null;
            if (businessType != null && !businessType.isEmpty()) {
                try {
                    type = BusinessType.valueOf(businessType.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // If business type is invalid, treat as null
                }
            }
            
            // Get next question
            QuestionnaireStep nextStep = onboardingService.getNextQuestion(type, previousResponses);
            
            // Check if questionnaire is complete
            if (type != null && onboardingService.isQuestionnaireComplete(type, previousResponses)) {
                Map<String, Object> response = new HashMap<>();
                response.put("completed", true);
                response.put("message", "Questionnaire completed successfully");
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.ok(nextStep);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An error occurred while processing your request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Submit completed questionnaire and create business profile
     */
    @PostMapping("/complete")
    @ResponseBody
    public ResponseEntity<?> completeOnboarding(@RequestBody Map<String, Object> request,
                                              Authentication authentication) {
        try {
            String businessTypeStr = (String) request.get("businessType");
            Map<String, Object> responses = (Map<String, Object>) request.get("responses");
            
            if (businessTypeStr == null || responses == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Business type and responses are required");
                return ResponseEntity.badRequest().body(error);
            }
            
            BusinessType businessType;
            try {
                businessType = BusinessType.valueOf(businessTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid business type: " + businessTypeStr);
                return ResponseEntity.badRequest().body(error);
            }
            
            // Check if user is authenticated (optional for onboarding, but required for completion)
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User must be authenticated to complete onboarding");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            // Validate questionnaire completeness
            if (!onboardingService.isQuestionnaireComplete(businessType, responses)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Questionnaire is not complete for the selected business type");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Create OnboardingRequest from the data
            OnboardingRequest onboardingRequest = new OnboardingRequest();
            onboardingRequest.setBusinessType(businessType);
            onboardingRequest.setCity((String) responses.get("city"));
            onboardingRequest.setResponses(responses);
            
            // Set business-specific fields
            if (responses.containsKey("seating_capacity")) {
                onboardingRequest.setSeatingCapacity(Integer.valueOf(responses.get("seating_capacity").toString()));
            }
            if (responses.containsKey("packaging_costs")) {
                onboardingRequest.setPackagingCosts(Double.valueOf(responses.get("packaging_costs").toString()));
            }
            if (responses.containsKey("power_consumption")) {
                onboardingRequest.setPowerConsumption(Double.valueOf(responses.get("power_consumption").toString()));
            }
            if (responses.containsKey("raw_material_sourcing")) {
                onboardingRequest.setRawMaterialSourcing((String) responses.get("raw_material_sourcing"));
            }
            
            // Get current user (this would typically come from authentication)
            // For now, we'll create a mock user - this will be properly implemented with security
            User user = new User();
            user.setEmail(authentication.getName());
            
            // Create business profile
            BusinessProfile profile = onboardingService.createBusinessProfile(onboardingRequest, user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Business profile created successfully");
            response.put("profileId", profile.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An error occurred while creating your business profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Validate a specific questionnaire response
     */
    @PostMapping("/validate")
    @ResponseBody
    public ResponseEntity<?> validateResponse(@Valid @RequestBody QuestionnaireResponse response,
                                            BindingResult bindingResult) {
        // Validation error handling
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }
        
        try {
            Map<String, Object> validationResult = new HashMap<>();
            
            // Basic validation based on question type and business type
            boolean isValid = validateQuestionResponse(response);
            
            validationResult.put("valid", isValid);
            if (!isValid) {
                validationResult.put("message", "Invalid response for the given question");
            }
            
            return ResponseEntity.ok(validationResult);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An error occurred while validating your response");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Get questionnaire progress for a specific business type
     */
    @GetMapping("/progress")
    @ResponseBody
    public ResponseEntity<?> getProgress(@RequestParam String businessType,
                                       @RequestParam Map<String, Object> responses) {
        try {
            BusinessType type = BusinessType.valueOf(businessType.toUpperCase());
            boolean isComplete = onboardingService.isQuestionnaireComplete(type, responses);
            
            Map<String, Object> progress = new HashMap<>();
            progress.put("businessType", type);
            progress.put("complete", isComplete);
            progress.put("totalResponses", responses.size());
            
            return ResponseEntity.ok(progress);
            
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid business type: " + businessType);
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An error occurred while checking progress");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Private helper method to validate individual question responses
     */
    private boolean validateQuestionResponse(QuestionnaireResponse response) {
        if (response.getAnswer() == null) {
            return false;
        }
        
        String questionId = response.getQuestionId();
        Object answer = response.getAnswer();
        
        // Validate based on question type
        switch (questionId) {
            case "business_type":
                try {
                    BusinessType.valueOf(answer.toString().toUpperCase());
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            case "city":
                return answer instanceof String && !((String) answer).trim().isEmpty();
            case "seating_capacity":
            case "delivery_radius":
            case "production_capacity":
            case "store_size":
            case "team_size":
                try {
                    int value = Integer.parseInt(answer.toString());
                    return value > 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            case "packaging_costs":
            case "power_consumption":
                try {
                    double value = Double.parseDouble(answer.toString());
                    return value >= 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            default:
                return answer instanceof String && !((String) answer).trim().isEmpty();
        }
    }
}