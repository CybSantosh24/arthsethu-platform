package com.arthsethu.service;

import com.arthsethu.dto.AdminDashboardDTO;
import com.arthsethu.dto.UserManagementDTO;
import com.arthsethu.model.*;
import com.arthsethu.repository.SubscriptionRepository;
import com.arthsethu.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for admin dashboard functionality
 * Implements Requirements 7.1, 7.2, 7.3, 7.4, 7.5
 */
@Service
@Transactional
public class AdminService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private GovernmentDataInterface governmentDataService;
    
    @Autowired
    private AICFOService aicfoService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Get comprehensive admin dashboard data
     * Implements Requirements 7.1, 7.2, 7.3, 7.4, 7.5
     */
    public AdminDashboardDTO getAdminDashboardData() {
        logger.info("Fetching admin dashboard data");
        
        AdminDashboardDTO.UserStats userStats = getUserStats();
        AdminDashboardDTO.SystemHealth systemHealth = getSystemHealth();
        AdminDashboardDTO.RevenueStats revenueStats = getRevenueStats();
        List<AdminDashboardDTO.SystemAlert> systemAlerts = getSystemAlerts();
        
        return new AdminDashboardDTO(userStats, systemHealth, revenueStats, systemAlerts);
    }
    
    /**
     * Get user statistics for admin dashboard
     * Implements Requirement 7.1
     */
    public AdminDashboardDTO.UserStats getUserStats() {
        logger.debug("Calculating user statistics");
        
        long totalUsers = userRepository.countTotalUsers();
        long aarambhUsers = userRepository.countByTier(SubscriptionTier.AARAMBH);
        long vistarUsers = userRepository.countByTier(SubscriptionTier.VISTAR);
        long shikharUsers = userRepository.countByTier(SubscriptionTier.SHIKHAR);
        
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        long newUsersToday = userRepository.findByCreatedAtAfter(today).size();
        long newUsersThisMonth = userRepository.findByCreatedAtAfter(monthStart).size();
        
        return new AdminDashboardDTO.UserStats(totalUsers, aarambhUsers, vistarUsers, 
                                             shikharUsers, newUsersToday, newUsersThisMonth);
    }
    
    /**
     * Get system health monitoring data
     * Implements Requirements 7.3, 7.5
     */
    public AdminDashboardDTO.SystemHealth getSystemHealth() {
        logger.debug("Checking system health");
        
        // Check Government API status
        boolean govApiStatus = false;
        LocalDateTime govApiLastUpdate = LocalDateTime.now();
        try {
            govApiStatus = governmentDataService.isGovernmentDataAvailable();
        } catch (Exception e) {
            logger.warn("Error checking government API status: {}", e.getMessage());
        }
        
        // Check Ollama/AI CFO status
        boolean ollamaStatus = false;
        LocalDateTime ollamaLastCheck = LocalDateTime.now();
        try {
            ollamaStatus = aicfoService.isAIServiceAvailable();
        } catch (Exception e) {
            logger.warn("Error checking Ollama status: {}", e.getMessage());
        }
        
        // Check database status (if we can query, it's working)
        boolean databaseStatus = true;
        try {
            userRepository.countTotalUsers();
        } catch (Exception e) {
            logger.error("Database health check failed: {}", e.getMessage());
            databaseStatus = false;
        }
        
        // Get system metrics
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double systemLoad = osBean.getSystemLoadAverage();
        
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long memoryUsage = memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024); // MB
        
        return new AdminDashboardDTO.SystemHealth(govApiStatus, govApiLastUpdate,
                                                 ollamaStatus, ollamaLastCheck,
                                                 databaseStatus, systemLoad, memoryUsage);
    }
    
    /**
     * Get revenue statistics for admin dashboard
     * Implements Requirement 7.4
     */
    public AdminDashboardDTO.RevenueStats getRevenueStats() {
        logger.debug("Calculating revenue statistics");
        
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime yearStart = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        // Get total revenue
        BigDecimal totalRevenue = subscriptionRepository.getTotalRevenueByDateRange(
            SubscriptionStatus.ACTIVE, yearStart, LocalDateTime.now());
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        
        // Get monthly recurring revenue
        BigDecimal monthlyRecurringRevenue = subscriptionRepository.getMonthlyRecurringRevenue(
            SubscriptionStatus.ACTIVE, SubscriptionTier.AARAMBH);
        if (monthlyRecurringRevenue == null) monthlyRecurringRevenue = BigDecimal.ZERO;
        
        // Get today's revenue
        BigDecimal revenueToday = subscriptionRepository.getTotalRevenueByDateRange(
            SubscriptionStatus.ACTIVE, today, LocalDateTime.now());
        if (revenueToday == null) revenueToday = BigDecimal.ZERO;
        
        // Get this month's revenue
        BigDecimal revenueThisMonth = subscriptionRepository.getTotalRevenueByDateRange(
            SubscriptionStatus.ACTIVE, monthStart, LocalDateTime.now());
        if (revenueThisMonth == null) revenueThisMonth = BigDecimal.ZERO;
        
        // Get revenue by tier
        BigDecimal vistarRevenue = subscriptionRepository.getTotalRevenueByTierAndDateRange(
            SubscriptionTier.VISTAR, SubscriptionStatus.ACTIVE, yearStart, LocalDateTime.now());
        if (vistarRevenue == null) vistarRevenue = BigDecimal.ZERO;
        
        BigDecimal shikharRevenue = subscriptionRepository.getTotalRevenueByTierAndDateRange(
            SubscriptionTier.SHIKHAR, SubscriptionStatus.ACTIVE, yearStart, LocalDateTime.now());
        if (shikharRevenue == null) shikharRevenue = BigDecimal.ZERO;
        
        // Calculate conversion rate (paid users / total users)
        long totalUsers = userRepository.countTotalUsers();
        long paidUsers = subscriptionRepository.countPaidSubscriptions(SubscriptionTier.AARAMBH, SubscriptionStatus.ACTIVE);
        double conversionRate = totalUsers > 0 ? (double) paidUsers / totalUsers * 100 : 0.0;
        
        return new AdminDashboardDTO.RevenueStats(totalRevenue, monthlyRecurringRevenue,
                                                revenueToday, revenueThisMonth,
                                                vistarRevenue, shikharRevenue, conversionRate);
    }
    
    /**
     * Get system alerts for admin dashboard
     * Implements Requirement 7.5
     */
    public List<AdminDashboardDTO.SystemAlert> getSystemAlerts() {
        logger.debug("Generating system alerts");
        
        List<AdminDashboardDTO.SystemAlert> alerts = new ArrayList<>();
        
        // Check for system issues and generate alerts
        AdminDashboardDTO.SystemHealth health = getSystemHealth();
        
        if (!health.isGovernmentApiStatus()) {
            alerts.add(new AdminDashboardDTO.SystemAlert(
                "API_DOWN", 
                "Government Data API is not responding", 
                "HIGH", 
                LocalDateTime.now()
            ));
        }
        
        if (!health.isOllamaStatus()) {
            alerts.add(new AdminDashboardDTO.SystemAlert(
                "AI_DOWN", 
                "Ollama AI service is not available", 
                "HIGH", 
                LocalDateTime.now()
            ));
        }
        
        if (!health.isDatabaseStatus()) {
            alerts.add(new AdminDashboardDTO.SystemAlert(
                "DB_ERROR", 
                "Database connection issues detected", 
                "CRITICAL", 
                LocalDateTime.now()
            ));
        }
        
        if (health.getSystemLoad() > 0.8) {
            alerts.add(new AdminDashboardDTO.SystemAlert(
                "HIGH_LOAD", 
                "System load is high: " + String.format("%.2f", health.getSystemLoad()), 
                "MEDIUM", 
                LocalDateTime.now()
            ));
        }
        
        if (health.getMemoryUsage() > 1024) { // More than 1GB
            alerts.add(new AdminDashboardDTO.SystemAlert(
                "HIGH_MEMORY", 
                "Memory usage is high: " + health.getMemoryUsage() + " MB", 
                "MEDIUM", 
                LocalDateTime.now()
            ));
        }
        
        return alerts;
    }
    
    /**
     * Get all users for management
     * Implements Requirement 7.1
     */
    public List<UserManagementDTO> getAllUsers() {
        logger.debug("Fetching all users for management");
        
        List<User> users = userRepository.findAll();
        return users.stream()
                   .map(this::convertToUserManagementDTO)
                   .collect(Collectors.toList());
    }
    
    /**
     * Get users by tier
     * Implements Requirement 7.1
     */
    public List<UserManagementDTO> getUsersByTier(SubscriptionTier tier) {
        logger.debug("Fetching users by tier: {}", tier);
        
        List<User> users = userRepository.findByTier(tier);
        return users.stream()
                   .map(this::convertToUserManagementDTO)
                   .collect(Collectors.toList());
    }
    
    /**
     * Ban a user
     * Implements Requirement 7.2
     */
    public boolean banUser(Long userId, String reason) {
        logger.info("Banning user with ID: {} for reason: {}", userId, reason);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found for banning: {}", userId);
            return false;
        }
        
        User user = userOpt.get();
        
        // For this implementation, we'll add a banned flag to the user
        // In a real system, you might have a separate UserStatus entity
        // For now, we'll use a simple approach by setting a special password hash
        user.setPasswordHash("BANNED_" + user.getPasswordHash());
        
        // Cancel any active subscription
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUser(user);
        if (subscriptionOpt.isPresent()) {
            Subscription subscription = subscriptionOpt.get();
            subscription.cancel();
            subscriptionRepository.save(subscription);
        }
        
        // Downgrade to free tier
        user.setTier(SubscriptionTier.AARAMBH);
        userRepository.save(user);
        
        logger.info("User {} has been banned successfully", userId);
        return true;
    }
    
    /**
     * Unban a user
     * Implements Requirement 7.2
     */
    public boolean unbanUser(Long userId) {
        logger.info("Unbanning user with ID: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found for unbanning: {}", userId);
            return false;
        }
        
        User user = userOpt.get();
        
        // Check if user is actually banned
        if (!user.getPasswordHash().startsWith("BANNED_")) {
            logger.warn("User {} is not banned", userId);
            return false;
        }
        
        // Restore original password hash
        String originalPasswordHash = user.getPasswordHash().substring("BANNED_".length());
        user.setPasswordHash(originalPasswordHash);
        userRepository.save(user);
        
        logger.info("User {} has been unbanned successfully", userId);
        return true;
    }
    
    /**
     * Reset user password
     * Implements Requirement 7.2
     */
    public String resetUserPassword(Long userId) {
        logger.info("Resetting password for user with ID: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found for password reset: {}", userId);
            return null;
        }
        
        User user = userOpt.get();
        
        // Generate temporary password
        String tempPassword = generateTemporaryPassword();
        String hashedPassword = passwordEncoder.encode(tempPassword);
        
        user.setPasswordHash(hashedPassword);
        userRepository.save(user);
        
        logger.info("Password reset successfully for user {}", userId);
        return tempPassword;
    }
    
    /**
     * Check if user is banned
     */
    public boolean isUserBanned(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        return userOpt.get().getPasswordHash().startsWith("BANNED_");
    }
    
    /**
     * Generate temporary password
     */
    private String generateTemporaryPassword() {
        return "temp_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
    
    /**
     * Convert User entity to UserManagementDTO
     */
    private UserManagementDTO convertToUserManagementDTO(User user) {
        boolean isBanned = user.getPasswordHash().startsWith("BANNED_");
        boolean isActive = !isBanned && user.getTier() != null;
        
        String businessType = null;
        String city = null;
        if (user.getBusinessProfile() != null) {
            businessType = user.getBusinessProfile().getBusinessType() != null ? 
                          user.getBusinessProfile().getBusinessType().toString() : null;
            city = user.getBusinessProfile().getCity();
        }
        
        return new UserManagementDTO(
            user.getId(),
            user.getEmail(),
            user.getTier(),
            user.getCreatedAt(),
            null, // lastLogin - would need to track this separately
            isActive,
            isBanned,
            businessType,
            city
        );
    }
}