package com.arthsethu.controller;

import com.arthsethu.dto.HealthScoreDTO;
import com.arthsethu.model.DailyMetrics;
import com.arthsethu.model.SubscriptionTier;
import com.arthsethu.model.User;
import com.arthsethu.repository.UserRepository;
import com.arthsethu.security.RequiresTier;
import com.arthsethu.security.RequiresFeature;
import com.arthsethu.service.DailyMetricsService;
import com.arthsethu.service.HealthScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    private final DailyMetricsService dailyMetricsService;
    private final HealthScoreService healthScoreService;
    private final UserRepository userRepository;
    
    @Autowired
    public DashboardController(DailyMetricsService dailyMetricsService, 
                             HealthScoreService healthScoreService,
                             UserRepository userRepository) {
        this.dailyMetricsService = dailyMetricsService;
        this.healthScoreService = healthScoreService;
        this.userRepository = userRepository;
    }
    
    /**
     * Basic dashboard - available to all authenticated users
     */
    @GetMapping
    public String dashboard(Model model, Authentication authentication) {
        User user = getCurrentUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("hasLoggedToday", dailyMetricsService.hasLoggedToday(user));
        
        // Add latest metrics if available
        Optional<DailyMetrics> latestMetrics = dailyMetricsService.getLatestMetrics(user);
        latestMetrics.ifPresent(metrics -> model.addAttribute("latestMetrics", metrics));
        
        return "dashboard/index";
    }
    
    /**
     * Vistar tier dashboard - requires Vistar or higher subscription
     */
    @GetMapping("/vistar")
    @RequiresTier(SubscriptionTier.VISTAR)
    public String vistarDashboard(Model model, Authentication authentication) {
        User user = getCurrentUser(authentication);
        
        // Get health score analysis
        HealthScoreDTO healthScore = healthScoreService.getHealthScoreAnalysis(user);
        
        // Get 30-day trend data
        List<DailyMetrics> trendData = dailyMetricsService.get30DayTrend(user);
        
        model.addAttribute("user", user);
        model.addAttribute("dashboardType", "vistar");
        model.addAttribute("healthScore", healthScore);
        model.addAttribute("trendData", trendData);
        model.addAttribute("hasLoggedToday", dailyMetricsService.hasLoggedToday(user));
        
        return "dashboard/vistar";
    }
    
    /**
     * Health score feature - requires specific feature access
     */
    @GetMapping("/health-score")
    @RequiresFeature("health_score")
    public String healthScore(Model model, Authentication authentication) {
        User user = getCurrentUser(authentication);
        HealthScoreDTO healthScore = healthScoreService.getHealthScoreAnalysis(user);
        
        model.addAttribute("user", user);
        model.addAttribute("healthScore", healthScore);
        
        return "dashboard/health-score";
    }
    
    /**
     * Daily metrics input - requires Vistar tier
     */
    @GetMapping("/metrics")
    @RequiresTier(SubscriptionTier.VISTAR)
    public String dailyMetrics(Model model, Authentication authentication) {
        User user = getCurrentUser(authentication);
        
        // Check if already logged today
        boolean hasLoggedToday = dailyMetricsService.hasLoggedToday(user);
        
        // Get today's metrics if they exist
        Optional<DailyMetrics> todayMetrics = Optional.empty();
        if (hasLoggedToday) {
            todayMetrics = dailyMetricsService.getLatestMetrics(user)
                .filter(metrics -> metrics.getDate().equals(LocalDate.now()));
        }
        
        model.addAttribute("user", user);
        model.addAttribute("hasLoggedToday", hasLoggedToday);
        todayMetrics.ifPresent(metrics -> model.addAttribute("todayMetrics", metrics));
        
        return "dashboard/metrics";
    }
    
    /**
     * Submit daily metrics - POST endpoint
     */
    @PostMapping("/metrics")
    @RequiresTier(SubscriptionTier.VISTAR)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitDailyMetrics(
            @RequestParam BigDecimal sales,
            @RequestParam BigDecimal expenses,
            @RequestParam BigDecimal wastage,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            User user = getCurrentUser(authentication);
            
            // Validate input
            if (sales.compareTo(BigDecimal.ZERO) < 0 || 
                expenses.compareTo(BigDecimal.ZERO) < 0 || 
                wastage.compareTo(BigDecimal.ZERO) < 0) {
                response.put("success", false);
                response.put("message", "All values must be non-negative");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Save metrics
            DailyMetrics metrics = dailyMetricsService.saveDailyMetrics(user, LocalDate.now(), sales, expenses, wastage);
            
            response.put("success", true);
            response.put("message", "Daily metrics saved successfully");
            response.put("healthScore", metrics.getHealthScore());
            response.put("margin", metrics.getMargin());
            response.put("wastagePercentage", metrics.getWastagePercentage());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error saving metrics: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get health score data for Chart.js - API endpoint
     */
    @GetMapping("/api/health-score")
    @RequiresTier(SubscriptionTier.VISTAR)
    @ResponseBody
    public ResponseEntity<HealthScoreDTO> getHealthScoreData(Authentication authentication) {
        User user = getCurrentUser(authentication);
        HealthScoreDTO healthScore = healthScoreService.getHealthScoreAnalysis(user);
        return ResponseEntity.ok(healthScore);
    }
    
    /**
     * Get trend data for charts - API endpoint
     */
    @GetMapping("/api/trend-data")
    @RequiresTier(SubscriptionTier.VISTAR)
    @ResponseBody
    public ResponseEntity<List<DailyMetrics>> getTrendData(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<DailyMetrics> trendData = dailyMetricsService.get30DayTrend(user);
        return ResponseEntity.ok(trendData);
    }
    
    /**
     * Advanced analytics - requires Shikhar tier
     */
    @GetMapping("/analytics")
    @RequiresTier(value = SubscriptionTier.SHIKHAR, message = "Advanced analytics requires Shikhar tier subscription")
    public String advancedAnalytics(Model model, Authentication authentication) {
        User user = getCurrentUser(authentication);
        model.addAttribute("user", user);
        return "dashboard/analytics";
    }
    
    /**
     * API endpoint for checking dashboard access
     */
    @GetMapping("/access-check")
    @ResponseBody
    public Map<String, Object> checkDashboardAccess(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication != null && authentication.getPrincipal() != null) {
            User user = getCurrentUser(authentication);
            response.put("hasBasicAccess", true);
            response.put("user", authentication.getName());
            response.put("tier", user.getTier().toString());
            response.put("hasVistarAccess", user.getTier().ordinal() >= SubscriptionTier.VISTAR.ordinal());
            response.put("hasShikharAccess", user.getTier().ordinal() >= SubscriptionTier.SHIKHAR.ordinal());
        } else {
            response.put("hasBasicAccess", false);
        }
        
        return response;
    }
    
    // Helper method to get current user from authentication
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}