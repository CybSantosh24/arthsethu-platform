package com.arthsethu.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions",
       indexes = {
           @Index(name = "idx_subscriptions_user", columnList = "user_id"),
           @Index(name = "idx_subscriptions_status", columnList = "status")
       })
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionTier tier;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "payment_reference")
    private String paymentReference;
    
    @Column(name = "auto_renew")
    private Boolean autoRenew = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public Subscription() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = SubscriptionStatus.PENDING;
    }
    
    public Subscription(User user, SubscriptionTier tier) {
        this();
        this.user = user;
        this.tier = tier;
        this.amount = BigDecimal.valueOf(tier.getMonthlyPrice());
        this.startDate = LocalDateTime.now();
        
        // Free tier is immediately active
        if (tier == SubscriptionTier.AARAMBH) {
            this.status = SubscriptionStatus.ACTIVE;
        }
    }
    
    // Lifecycle callbacks
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Business logic methods
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE && 
               (endDate == null || endDate.isAfter(LocalDateTime.now()));
    }
    
    public boolean isExpired() {
        return endDate != null && endDate.isBefore(LocalDateTime.now());
    }
    
    public boolean canUpgradeTo(SubscriptionTier newTier) {
        if (newTier == null || newTier == this.tier) {
            return false;
        }
        
        // Can always upgrade to higher tier
        return newTier.getMonthlyPrice() > this.tier.getMonthlyPrice();
    }
    
    public void activate() {
        this.status = SubscriptionStatus.ACTIVE;
        this.startDate = LocalDateTime.now();
        
        // Set end date for paid tiers (30 days from now)
        if (tier != SubscriptionTier.AARAMBH) {
            this.endDate = startDate.plusDays(30);
        }
    }
    
    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
        this.autoRenew = false;
    }
    
    public void suspend() {
        this.status = SubscriptionStatus.SUSPENDED;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public SubscriptionTier getTier() {
        return tier;
    }
    
    public void setTier(SubscriptionTier tier) {
        this.tier = tier;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public SubscriptionStatus getStatus() {
        return status;
    }
    
    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getPaymentReference() {
        return paymentReference;
    }
    
    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }
    
    public Boolean getAutoRenew() {
        return autoRenew;
    }
    
    public void setAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}