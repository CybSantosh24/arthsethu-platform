package com.arthsethu.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for tier access control exceptions
 */
@ControllerAdvice
public class TierAccessControlExceptionHandler {
    
    /**
     * Handle insufficient tier exceptions for AJAX requests
     */
    @ExceptionHandler(InsufficientTierException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleInsufficientTierException(
            InsufficientTierException ex, HttpServletRequest request) {
        
        // Check if it's an AJAX request
        String requestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(requestedWith) || 
                        request.getRequestURI().startsWith("/api/") ||
                        request.getContentType() != null && request.getContentType().contains("application/json");
        
        if (isAjax) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "INSUFFICIENT_TIER");
            response.put("message", ex.getMessage());
            response.put("currentTier", ex.getCurrentTier() != null ? ex.getCurrentTier().getDisplayName() : "Unknown");
            
            if (ex.getRequiredTier() != null) {
                response.put("requiredTier", ex.getRequiredTier().getDisplayName());
                response.put("requiredTierPrice", ex.getRequiredTier().getMonthlyPrice());
            }
            
            if (ex.getFeatureName() != null) {
                response.put("featureName", ex.getFeatureName());
            }
            
            response.put("upgradeUrl", "/subscription/tiers");
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } else {
            // For regular web requests, redirect to upgrade page
            ModelAndView modelAndView = new ModelAndView("subscription/upgrade-required");
            modelAndView.addObject("message", ex.getMessage());
            modelAndView.addObject("currentTier", ex.getCurrentTier());
            modelAndView.addObject("requiredTier", ex.getRequiredTier());
            modelAndView.addObject("featureName", ex.getFeatureName());
            
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse(ex));
        }
    }
    
    private Map<String, Object> createErrorResponse(InsufficientTierException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "INSUFFICIENT_TIER");
        response.put("message", ex.getMessage());
        response.put("currentTier", ex.getCurrentTier() != null ? ex.getCurrentTier().getDisplayName() : "Unknown");
        
        if (ex.getRequiredTier() != null) {
            response.put("requiredTier", ex.getRequiredTier().getDisplayName());
            response.put("requiredTierPrice", ex.getRequiredTier().getMonthlyPrice());
        }
        
        if (ex.getFeatureName() != null) {
            response.put("featureName", ex.getFeatureName());
        }
        
        response.put("upgradeUrl", "/subscription/tiers");
        return response;
    }
}