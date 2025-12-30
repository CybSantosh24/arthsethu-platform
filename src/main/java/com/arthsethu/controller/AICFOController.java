package com.arthsethu.controller;

import com.arthsethu.dto.AICFORequest;
import com.arthsethu.dto.WhatIfAnalysis;
import com.arthsethu.model.BusinessProfile;
import com.arthsethu.model.DailyMetrics;
import com.arthsethu.model.SubscriptionTier;
import com.arthsethu.model.User;
import com.arthsethu.repository.BusinessProfileRepository;
import com.arthsethu.repository.DailyMetricsRepository;
import com.arthsethu.repository.UserRepository;
import com.arthsethu.service.AICFOService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/ai-cfo")
public class AICFOController {
    
    private final AICFOService aicfoService;
    private final UserRepository userRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final DailyMetricsRepository dailyMetricsRepository;
    
    @Autowired
    public AICFOController(AICFOService aicfoService, 
                          UserRepository userRepository,
                          BusinessProfileRepository businessProfileRepository,
                          DailyMetricsRepository dailyMetricsRepository) {
        this.aicfoService = aicfoService;
        this.userRepository = userRepository;
        this.businessProfileRepository = businessProfileRepository;
        this.dailyMetricsRepository = dailyMetricsRepository;
    }
    
    /**
     * Display AI CFO chat interface
     */
    @GetMapping
    public String showAICFOChat(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }
        
        Optional<User> userOpt = userRepository.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        User user = userOpt.get();
        
        // Check if user has Shikhar tier access
        if (user.getTier() != SubscriptionTier.SHIKHAR) {
            model.addAttribute("requiresUpgrade", true);
            model.addAttribute("currentTier", user.getTier());
            return "ai-cfo/upgrade-required";
        }
        
        // Get business profile for context
        Optional<BusinessProfile> profileOpt = businessProfileRepository.findByUser(user);
        if (profileOpt.isEmpty()) {
            model.addAttribute("error", "Please complete your business profile first");
            return "redirect:/onboarding";
        }
        
        BusinessProfile profile = profileOpt.get();
        List<DailyMetrics> recentMetrics = dailyMetricsRepository
            .findLast30DaysMetrics(user, java.time.LocalDate.now().minusDays(7))
            .stream()
            .limit(7) // Last 7 days for quick context
            .toList();
        
        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("recentMetrics", recentMetrics);
        
        return "ai-cfo/chat";
    }
    
    /**
     * Process AI CFO query via AJAX
     */
    @PostMapping("/query")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> processQuery(@Valid @RequestBody AICFORequest request,
                                                           Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication == null) {
            response.put("error", "Authentication required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        Optional<User> userOpt = userRepository.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        User user = userOpt.get();
        
        // Check tier access
        if (user.getTier() != SubscriptionTier.SHIKHAR) {
            response.put("error", "Shikhar tier required for AI CFO access");
            response.put("requiresUpgrade", true);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        // Get business profile
        Optional<BusinessProfile> profileOpt = businessProfileRepository.findByUser(user);
        if (profileOpt.isEmpty()) {
            response.put("error", "Business profile required");
            return ResponseEntity.badRequest().body(response);
        }
        
        BusinessProfile profile = profileOpt.get();
        List<DailyMetrics> history = dailyMetricsRepository
            .findLast30DaysMetrics(user, java.time.LocalDate.now().minusDays(30));
        
        try {
            String aiResponse = aicfoService.processQuery(request.getQuery(), profile, history);
            response.put("response", aiResponse);
            response.put("success", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Unable to process your query at this time");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Perform what-if simulation
     */
    @PostMapping("/simulate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> performSimulation(@RequestBody Map<String, String> request,
                                                               Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication == null) {
            response.put("error", "Authentication required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        Optional<User> userOpt = userRepository.findByEmail(authentication.getName());
        if (userOpt.isEmpty()) {
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        User user = userOpt.get();
        
        // Check tier access
        if (user.getTier() != SubscriptionTier.SHIKHAR) {
            response.put("error", "Shikhar tier required for what-if simulations");
            response.put("requiresUpgrade", true);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        // Get business profile
        Optional<BusinessProfile> profileOpt = businessProfileRepository.findByUser(user);
        if (profileOpt.isEmpty()) {
            response.put("error", "Business profile required");
            return ResponseEntity.badRequest().body(response);
        }
        
        String scenario = request.get("scenario");
        if (scenario == null || scenario.trim().isEmpty()) {
            response.put("error", "Scenario description required");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            WhatIfAnalysis analysis = aicfoService.performSimulation(scenario, profileOpt.get());
            response.put("analysis", analysis);
            response.put("success", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Unable to perform simulation at this time");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get chat history (placeholder for future implementation)
     */
    @GetMapping("/history")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChatHistory(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        // For now, return empty history - this could be expanded to store chat history
        response.put("history", List.of());
        response.put("success", true);
        
        return ResponseEntity.ok(response);
    }
}