package com.arthsethu.controller;

import com.arthsethu.model.Subscription;
import com.arthsethu.model.SubscriptionTier;
import com.arthsethu.model.User;
import com.arthsethu.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {
    
    @Autowired
    private SubscriptionService subscriptionService;
    
    /**
     * Display subscription tiers and pricing
     */
    @GetMapping("/tiers")
    public String showSubscriptionTiers(Model model) {
        SubscriptionTier[] tiers = subscriptionService.getAvailableTiers();
        model.addAttribute("tiers", tiers);
        
        // Get current user's subscription if authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            Optional<Subscription> currentSubscription = subscriptionService.getUserSubscription(user);
            model.addAttribute("currentTier", user.getTier());
            model.addAttribute("currentSubscription", currentSubscription.orElse(null));
        }
        
        return "subscription/tiers";
    }
    
    /**
     * Initiate subscription upgrade
     */
    @PostMapping("/upgrade")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> upgradeSubscription(
            @RequestParam("tier") String tierName,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.badRequest().body(response);
            }
            
            User user = (User) authentication.getPrincipal();
            SubscriptionTier newTier = SubscriptionTier.valueOf(tierName.toUpperCase());
            
            // Check if upgrade is valid
            if (newTier == user.getTier()) {
                response.put("success", false);
                response.put("message", "Already subscribed to this tier");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (newTier.getMonthlyPrice() <= user.getTier().getMonthlyPrice()) {
                response.put("success", false);
                response.put("message", "Cannot downgrade subscription");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create upgrade subscription
            Subscription newSubscription = subscriptionService.upgradeSubscription(user, newTier);
            
            response.put("success", true);
            response.put("subscriptionId", newSubscription.getId());
            response.put("amount", newSubscription.getAmount());
            response.put("tier", newTier.getDisplayName());
            response.put("message", "Subscription upgrade initiated. Please complete payment.");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Invalid subscription tier");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to upgrade subscription: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Process payment for subscription
     */
    @PostMapping("/payment")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> processPayment(
            @RequestParam("subscriptionId") Long subscriptionId,
            @RequestParam("paymentMethod") String paymentMethod,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Process payment simulation
            boolean paymentSuccess = subscriptionService.processPayment(subscriptionId, paymentMethod);
            
            if (paymentSuccess) {
                response.put("success", true);
                response.put("message", "Payment processed successfully. Subscription activated!");
                response.put("redirectUrl", "/dashboard");
            } else {
                response.put("success", false);
                response.put("message", "Payment failed. Please try again.");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Payment processing failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Cancel subscription
     */
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelSubscription(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.badRequest().body(response);
            }
            
            User user = (User) authentication.getPrincipal();
            subscriptionService.cancelSubscription(user);
            
            response.put("success", true);
            response.put("message", "Subscription cancelled successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to cancel subscription: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get current subscription status
     */
    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSubscriptionStatus(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.badRequest().body(response);
            }
            
            User user = (User) authentication.getPrincipal();
            Optional<Subscription> subscription = subscriptionService.getUserSubscription(user);
            
            response.put("success", true);
            response.put("currentTier", user.getTier().getDisplayName());
            response.put("tierPrice", user.getTier().getMonthlyPrice());
            
            if (subscription.isPresent()) {
                Subscription sub = subscription.get();
                response.put("status", sub.getStatus().getDisplayName());
                response.put("startDate", sub.getStartDate());
                response.put("endDate", sub.getEndDate());
                response.put("autoRenew", sub.getAutoRenew());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get subscription status: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Check feature access
     */
    @GetMapping("/feature-access")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkFeatureAccess(
            @RequestParam("feature") String featureName,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                response.put("hasAccess", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.ok(response);
            }
            
            User user = (User) authentication.getPrincipal();
            boolean hasAccess = subscriptionService.canAccessFeature(user, featureName);
            
            response.put("hasAccess", hasAccess);
            response.put("currentTier", user.getTier().getDisplayName());
            
            if (!hasAccess) {
                response.put("message", "Feature not available in your current tier");
                response.put("upgradeRequired", true);
                
                // Suggest upgrade tiers
                if (user.getTier() == SubscriptionTier.AARAMBH) {
                    if (featureName.equals("ai_cfo") || featureName.equals("what_if_simulation")) {
                        response.put("suggestedTier", SubscriptionTier.SHIKHAR.getDisplayName());
                        response.put("suggestedPrice", SubscriptionTier.SHIKHAR.getMonthlyPrice());
                    } else {
                        response.put("suggestedTier", SubscriptionTier.VISTAR.getDisplayName());
                        response.put("suggestedPrice", SubscriptionTier.VISTAR.getMonthlyPrice());
                    }
                } else if (user.getTier() == SubscriptionTier.VISTAR) {
                    response.put("suggestedTier", SubscriptionTier.SHIKHAR.getDisplayName());
                    response.put("suggestedPrice", SubscriptionTier.SHIKHAR.getMonthlyPrice());
                }
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("hasAccess", false);
            response.put("message", "Failed to check feature access: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Display subscription management page
     */
    @GetMapping("/manage")
    public String manageSubscription(Model model, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return "redirect:/login";
        }
        
        User user = (User) authentication.getPrincipal();
        Optional<Subscription> subscription = subscriptionService.getUserSubscription(user);
        
        model.addAttribute("user", user);
        model.addAttribute("subscription", subscription.orElse(null));
        model.addAttribute("availableTiers", subscriptionService.getAvailableTiers());
        
        return "subscription/manage";
    }
}