package com.arthsethu.dto;

import com.arthsethu.model.SubscriptionTier;
import java.time.LocalDateTime;

/**
 * DTO for user management operations
 * Implements Requirements 7.1, 7.2
 */
public class UserManagementDTO {
    
    private Long userId;
    private String email;
    private SubscriptionTier tier;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean isActive;
    private boolean isBanned;
    private String businessType;
    private String city;
    
    public UserManagementDTO() {}
    
    public UserManagementDTO(Long userId, String email, SubscriptionTier tier, 
                           LocalDateTime createdAt, LocalDateTime lastLogin,
                           boolean isActive, boolean isBanned, String businessType, String city) {
        this.userId = userId;
        this.email = email;
        this.tier = tier;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
        this.isBanned = isBanned;
        this.businessType = businessType;
        this.city = city;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public SubscriptionTier getTier() {
        return tier;
    }
    
    public void setTier(SubscriptionTier tier) {
        this.tier = tier;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public boolean isBanned() {
        return isBanned;
    }
    
    public void setBanned(boolean banned) {
        isBanned = banned;
    }
    
    public String getBusinessType() {
        return businessType;
    }
    
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
}