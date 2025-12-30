package com.arthsethu.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for admin dashboard data
 * Implements Requirements 7.1, 7.2, 7.3, 7.4, 7.5
 */
public class AdminDashboardDTO {
    
    private UserStats userStats;
    private SystemHealth systemHealth;
    private RevenueStats revenueStats;
    private List<SystemAlert> systemAlerts;
    
    public AdminDashboardDTO() {}
    
    public AdminDashboardDTO(UserStats userStats, SystemHealth systemHealth, 
                           RevenueStats revenueStats, List<SystemAlert> systemAlerts) {
        this.userStats = userStats;
        this.systemHealth = systemHealth;
        this.revenueStats = revenueStats;
        this.systemAlerts = systemAlerts;
    }
    
    // Getters and Setters
    public UserStats getUserStats() {
        return userStats;
    }
    
    public void setUserStats(UserStats userStats) {
        this.userStats = userStats;
    }
    
    public SystemHealth getSystemHealth() {
        return systemHealth;
    }
    
    public void setSystemHealth(SystemHealth systemHealth) {
        this.systemHealth = systemHealth;
    }
    
    public RevenueStats getRevenueStats() {
        return revenueStats;
    }
    
    public void setRevenueStats(RevenueStats revenueStats) {
        this.revenueStats = revenueStats;
    }
    
    public List<SystemAlert> getSystemAlerts() {
        return systemAlerts;
    }
    
    public void setSystemAlerts(List<SystemAlert> systemAlerts) {
        this.systemAlerts = systemAlerts;
    }
    
    /**
     * User statistics for admin dashboard
     */
    public static class UserStats {
        private long totalUsers;
        private long aarambhUsers;
        private long vistarUsers;
        private long shikharUsers;
        private long newUsersToday;
        private long newUsersThisMonth;
        
        public UserStats() {}
        
        public UserStats(long totalUsers, long aarambhUsers, long vistarUsers, 
                        long shikharUsers, long newUsersToday, long newUsersThisMonth) {
            this.totalUsers = totalUsers;
            this.aarambhUsers = aarambhUsers;
            this.vistarUsers = vistarUsers;
            this.shikharUsers = shikharUsers;
            this.newUsersToday = newUsersToday;
            this.newUsersThisMonth = newUsersThisMonth;
        }
        
        // Getters and Setters
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
        
        public long getAarambhUsers() { return aarambhUsers; }
        public void setAarambhUsers(long aarambhUsers) { this.aarambhUsers = aarambhUsers; }
        
        public long getVistarUsers() { return vistarUsers; }
        public void setVistarUsers(long vistarUsers) { this.vistarUsers = vistarUsers; }
        
        public long getShikharUsers() { return shikharUsers; }
        public void setShikharUsers(long shikharUsers) { this.shikharUsers = shikharUsers; }
        
        public long getNewUsersToday() { return newUsersToday; }
        public void setNewUsersToday(long newUsersToday) { this.newUsersToday = newUsersToday; }
        
        public long getNewUsersThisMonth() { return newUsersThisMonth; }
        public void setNewUsersThisMonth(long newUsersThisMonth) { this.newUsersThisMonth = newUsersThisMonth; }
        
        public long getPaidUsers() { return vistarUsers + shikharUsers; }
    }
    
    /**
     * System health monitoring data
     */
    public static class SystemHealth {
        private boolean governmentApiStatus;
        private LocalDateTime governmentApiLastUpdate;
        private boolean ollamaStatus;
        private LocalDateTime ollamaLastCheck;
        private boolean databaseStatus;
        private double systemLoad;
        private long memoryUsage;
        
        public SystemHealth() {}
        
        public SystemHealth(boolean governmentApiStatus, LocalDateTime governmentApiLastUpdate,
                          boolean ollamaStatus, LocalDateTime ollamaLastCheck,
                          boolean databaseStatus, double systemLoad, long memoryUsage) {
            this.governmentApiStatus = governmentApiStatus;
            this.governmentApiLastUpdate = governmentApiLastUpdate;
            this.ollamaStatus = ollamaStatus;
            this.ollamaLastCheck = ollamaLastCheck;
            this.databaseStatus = databaseStatus;
            this.systemLoad = systemLoad;
            this.memoryUsage = memoryUsage;
        }
        
        // Getters and Setters
        public boolean isGovernmentApiStatus() { return governmentApiStatus; }
        public void setGovernmentApiStatus(boolean governmentApiStatus) { this.governmentApiStatus = governmentApiStatus; }
        
        public LocalDateTime getGovernmentApiLastUpdate() { return governmentApiLastUpdate; }
        public void setGovernmentApiLastUpdate(LocalDateTime governmentApiLastUpdate) { this.governmentApiLastUpdate = governmentApiLastUpdate; }
        
        public boolean isOllamaStatus() { return ollamaStatus; }
        public void setOllamaStatus(boolean ollamaStatus) { this.ollamaStatus = ollamaStatus; }
        
        public LocalDateTime getOllamaLastCheck() { return ollamaLastCheck; }
        public void setOllamaLastCheck(LocalDateTime ollamaLastCheck) { this.ollamaLastCheck = ollamaLastCheck; }
        
        public boolean isDatabaseStatus() { return databaseStatus; }
        public void setDatabaseStatus(boolean databaseStatus) { this.databaseStatus = databaseStatus; }
        
        public double getSystemLoad() { return systemLoad; }
        public void setSystemLoad(double systemLoad) { this.systemLoad = systemLoad; }
        
        public long getMemoryUsage() { return memoryUsage; }
        public void setMemoryUsage(long memoryUsage) { this.memoryUsage = memoryUsage; }
    }
    
    /**
     * Revenue statistics for admin dashboard
     */
    public static class RevenueStats {
        private BigDecimal totalRevenue;
        private BigDecimal monthlyRecurringRevenue;
        private BigDecimal revenueToday;
        private BigDecimal revenueThisMonth;
        private BigDecimal vistarRevenue;
        private BigDecimal shikharRevenue;
        private double conversionRate;
        
        public RevenueStats() {}
        
        public RevenueStats(BigDecimal totalRevenue, BigDecimal monthlyRecurringRevenue,
                          BigDecimal revenueToday, BigDecimal revenueThisMonth,
                          BigDecimal vistarRevenue, BigDecimal shikharRevenue, double conversionRate) {
            this.totalRevenue = totalRevenue;
            this.monthlyRecurringRevenue = monthlyRecurringRevenue;
            this.revenueToday = revenueToday;
            this.revenueThisMonth = revenueThisMonth;
            this.vistarRevenue = vistarRevenue;
            this.shikharRevenue = shikharRevenue;
            this.conversionRate = conversionRate;
        }
        
        // Getters and Setters
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
        
        public BigDecimal getMonthlyRecurringRevenue() { return monthlyRecurringRevenue; }
        public void setMonthlyRecurringRevenue(BigDecimal monthlyRecurringRevenue) { this.monthlyRecurringRevenue = monthlyRecurringRevenue; }
        
        public BigDecimal getRevenueToday() { return revenueToday; }
        public void setRevenueToday(BigDecimal revenueToday) { this.revenueToday = revenueToday; }
        
        public BigDecimal getRevenueThisMonth() { return revenueThisMonth; }
        public void setRevenueThisMonth(BigDecimal revenueThisMonth) { this.revenueThisMonth = revenueThisMonth; }
        
        public BigDecimal getVistarRevenue() { return vistarRevenue; }
        public void setVistarRevenue(BigDecimal vistarRevenue) { this.vistarRevenue = vistarRevenue; }
        
        public BigDecimal getShikharRevenue() { return shikharRevenue; }
        public void setShikharRevenue(BigDecimal shikharRevenue) { this.shikharRevenue = shikharRevenue; }
        
        public double getConversionRate() { return conversionRate; }
        public void setConversionRate(double conversionRate) { this.conversionRate = conversionRate; }
    }
    
    /**
     * System alert for admin dashboard
     */
    public static class SystemAlert {
        private String type;
        private String message;
        private String severity;
        private LocalDateTime timestamp;
        
        public SystemAlert() {}
        
        public SystemAlert(String type, String message, String severity, LocalDateTime timestamp) {
            this.type = type;
            this.message = message;
            this.severity = severity;
            this.timestamp = timestamp;
        }
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}