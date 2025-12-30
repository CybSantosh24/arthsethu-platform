package com.arthsethu.controller;

import com.arthsethu.dto.AdminDashboardDTO;
import com.arthsethu.dto.UserManagementDTO;
import com.arthsethu.model.SubscriptionTier;
import com.arthsethu.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for admin dashboard functionality
 * Implements Requirements 7.1, 7.2, 7.3, 7.4, 7.5
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private AdminService adminService;
    
    /**
     * Redirect admin root to dashboard
     */
    @GetMapping
    public String adminRoot() {
        return "redirect:/admin/dashboard";
    }
    
    /**
     * Display admin dashboard
     * Implements Requirements 7.1, 7.2, 7.3, 7.4, 7.5
     */
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        logger.info("Loading admin dashboard");
        
        try {
            AdminDashboardDTO dashboardData = adminService.getAdminDashboardData();
            model.addAttribute("dashboardData", dashboardData);
            model.addAttribute("pageTitle", "Admin Dashboard");
            
            return "admin/dashboard";
        } catch (Exception e) {
            logger.error("Error loading admin dashboard: {}", e.getMessage());
            model.addAttribute("error", "Unable to load dashboard data");
            return "admin/error";
        }
    }
    
    /**
     * Display user management page
     * Implements Requirements 7.1, 7.2
     */
    @GetMapping("/users")
    public String userManagement(Model model, 
                                @RequestParam(value = "tier", required = false) String tier) {
        logger.info("Loading user management page with tier filter: {}", tier);
        
        try {
            List<UserManagementDTO> users;
            
            if (tier != null && !tier.isEmpty()) {
                SubscriptionTier subscriptionTier = SubscriptionTier.valueOf(tier.toUpperCase());
                users = adminService.getUsersByTier(subscriptionTier);
                model.addAttribute("selectedTier", tier);
            } else {
                users = adminService.getAllUsers();
            }
            
            model.addAttribute("users", users);
            model.addAttribute("tiers", SubscriptionTier.values());
            model.addAttribute("pageTitle", "User Management");
            
            return "admin/users";
        } catch (Exception e) {
            logger.error("Error loading user management page: {}", e.getMessage());
            model.addAttribute("error", "Unable to load user data");
            return "admin/error";
        }
    }
    
    /**
     * Display system monitoring page
     * Implements Requirements 7.3, 7.5
     */
    @GetMapping("/system")
    public String systemMonitoring(Model model) {
        logger.info("Loading system monitoring page");
        
        try {
            AdminDashboardDTO.SystemHealth systemHealth = adminService.getSystemHealth();
            List<AdminDashboardDTO.SystemAlert> systemAlerts = adminService.getSystemAlerts();
            
            model.addAttribute("systemHealth", systemHealth);
            model.addAttribute("systemAlerts", systemAlerts);
            model.addAttribute("pageTitle", "System Monitoring");
            
            return "admin/system";
        } catch (Exception e) {
            logger.error("Error loading system monitoring page: {}", e.getMessage());
            model.addAttribute("error", "Unable to load system data");
            return "admin/error";
        }
    }
    
    /**
     * Display revenue analytics page
     * Implements Requirement 7.4
     */
    @GetMapping("/revenue")
    public String revenueAnalytics(Model model) {
        logger.info("Loading revenue analytics page");
        
        try {
            AdminDashboardDTO.RevenueStats revenueStats = adminService.getRevenueStats();
            AdminDashboardDTO.UserStats userStats = adminService.getUserStats();
            
            model.addAttribute("revenueStats", revenueStats);
            model.addAttribute("userStats", userStats);
            model.addAttribute("pageTitle", "Revenue Analytics");
            
            return "admin/revenue";
        } catch (Exception e) {
            logger.error("Error loading revenue analytics page: {}", e.getMessage());
            model.addAttribute("error", "Unable to load revenue data");
            return "admin/error";
        }
    }
    
    /**
     * Ban a user
     * Implements Requirement 7.2
     */
    @PostMapping("/users/{userId}/ban")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> banUser(@PathVariable Long userId, 
                                                      @RequestParam String reason) {
        logger.info("Admin request to ban user: {} for reason: {}", userId, reason);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = adminService.banUser(userId, reason);
            
            if (success) {
                response.put("success", true);
                response.put("message", "User banned successfully");
                logger.info("User {} banned successfully", userId);
            } else {
                response.put("success", false);
                response.put("message", "Failed to ban user - user not found");
                logger.warn("Failed to ban user {}: user not found", userId);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error banning user {}: {}", userId, e.getMessage());
            response.put("success", false);
            response.put("message", "Error banning user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Unban a user
     * Implements Requirement 7.2
     */
    @PostMapping("/users/{userId}/unban")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unbanUser(@PathVariable Long userId) {
        logger.info("Admin request to unban user: {}", userId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = adminService.unbanUser(userId);
            
            if (success) {
                response.put("success", true);
                response.put("message", "User unbanned successfully");
                logger.info("User {} unbanned successfully", userId);
            } else {
                response.put("success", false);
                response.put("message", "Failed to unban user - user not found or not banned");
                logger.warn("Failed to unban user {}: user not found or not banned", userId);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error unbanning user {}: {}", userId, e.getMessage());
            response.put("success", false);
            response.put("message", "Error unbanning user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Reset user password
     * Implements Requirement 7.2
     */
    @PostMapping("/users/{userId}/reset-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resetUserPassword(@PathVariable Long userId) {
        logger.info("Admin request to reset password for user: {}", userId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String tempPassword = adminService.resetUserPassword(userId);
            
            if (tempPassword != null) {
                response.put("success", true);
                response.put("message", "Password reset successfully");
                response.put("tempPassword", tempPassword);
                logger.info("Password reset successfully for user {}", userId);
            } else {
                response.put("success", false);
                response.put("message", "Failed to reset password - user not found");
                logger.warn("Failed to reset password for user {}: user not found", userId);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error resetting password for user {}: {}", userId, e.getMessage());
            response.put("success", false);
            response.put("message", "Error resetting password: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get dashboard data as JSON (for AJAX updates)
     * Implements Requirements 7.1, 7.2, 7.3, 7.4, 7.5
     */
    @GetMapping("/api/dashboard-data")
    @ResponseBody
    public ResponseEntity<AdminDashboardDTO> getDashboardData() {
        logger.debug("API request for dashboard data");
        
        try {
            AdminDashboardDTO dashboardData = adminService.getAdminDashboardData();
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            logger.error("Error fetching dashboard data via API: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get user statistics as JSON
     * Implements Requirement 7.1
     */
    @GetMapping("/api/user-stats")
    @ResponseBody
    public ResponseEntity<AdminDashboardDTO.UserStats> getUserStats() {
        logger.debug("API request for user statistics");
        
        try {
            AdminDashboardDTO.UserStats userStats = adminService.getUserStats();
            return ResponseEntity.ok(userStats);
        } catch (Exception e) {
            logger.error("Error fetching user stats via API: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get system health as JSON
     * Implements Requirements 7.3, 7.5
     */
    @GetMapping("/api/system-health")
    @ResponseBody
    public ResponseEntity<AdminDashboardDTO.SystemHealth> getSystemHealth() {
        logger.debug("API request for system health");
        
        try {
            AdminDashboardDTO.SystemHealth systemHealth = adminService.getSystemHealth();
            return ResponseEntity.ok(systemHealth);
        } catch (Exception e) {
            logger.error("Error fetching system health via API: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get revenue statistics as JSON
     * Implements Requirement 7.4
     */
    @GetMapping("/api/revenue-stats")
    @ResponseBody
    public ResponseEntity<AdminDashboardDTO.RevenueStats> getRevenueStats() {
        logger.debug("API request for revenue statistics");
        
        try {
            AdminDashboardDTO.RevenueStats revenueStats = adminService.getRevenueStats();
            return ResponseEntity.ok(revenueStats);
        } catch (Exception e) {
            logger.error("Error fetching revenue stats via API: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}