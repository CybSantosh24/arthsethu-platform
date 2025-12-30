package com.arthsethu.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "business_profiles")
public class BusinessProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", nullable = false)
    private BusinessType businessType;
    
    @Column(nullable = false)
    private String city;
    
    @Column(name = "seating_capacity")
    private Integer seatingCapacity;
    
    @Column(name = "packaging_costs")
    private Double packagingCosts;
    
    @Column(name = "power_consumption")
    private Double powerConsumption;
    
    @Column(name = "raw_material_sourcing")
    private String rawMaterialSourcing;
    
    @Column(name = "questionnaire_responses", columnDefinition = "TEXT")
    private String questionnaireResponses; // JSON format
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public BusinessProfile() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public BusinessProfile(User user, BusinessType businessType, String city) {
        this();
        this.user = user;
        this.businessType = businessType;
        this.city = city;
    }
    
    // Lifecycle callbacks
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
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
    
    public BusinessType getBusinessType() {
        return businessType;
    }
    
    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }
    
    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }
    
    public Double getPackagingCosts() {
        return packagingCosts;
    }
    
    public void setPackagingCosts(Double packagingCosts) {
        this.packagingCosts = packagingCosts;
    }
    
    public Double getPowerConsumption() {
        return powerConsumption;
    }
    
    public void setPowerConsumption(Double powerConsumption) {
        this.powerConsumption = powerConsumption;
    }
    
    public String getRawMaterialSourcing() {
        return rawMaterialSourcing;
    }
    
    public void setRawMaterialSourcing(String rawMaterialSourcing) {
        this.rawMaterialSourcing = rawMaterialSourcing;
    }
    
    public String getQuestionnaireResponses() {
        return questionnaireResponses;
    }
    
    public void setQuestionnaireResponses(String questionnaireResponses) {
        this.questionnaireResponses = questionnaireResponses;
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