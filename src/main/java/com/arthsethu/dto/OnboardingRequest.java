package com.arthsethu.dto;

import com.arthsethu.model.BusinessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Request DTO for onboarding process containing all questionnaire responses
 */
public class OnboardingRequest {
    @NotNull
    private BusinessType businessType;
    
    @NotBlank
    private String city;
    
    private Map<String, Object> responses;
    
    // Business-specific fields
    private Integer seatingCapacity;
    private Double packagingCosts;
    private Double powerConsumption;
    private String rawMaterialSourcing;
    
    // Constructors
    public OnboardingRequest() {
        this.responses = new HashMap<>();
    }
    
    public OnboardingRequest(BusinessType businessType, String city) {
        this();
        this.businessType = businessType;
        this.city = city;
    }
    
    // Getters and Setters
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
    
    public Map<String, Object> getResponses() {
        return responses;
    }
    
    public void setResponses(Map<String, Object> responses) {
        this.responses = responses;
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
    
    // Utility methods
    public void addResponse(String questionId, Object answer) {
        this.responses.put(questionId, answer);
    }
    
    public Object getResponse(String questionId) {
        return this.responses.get(questionId);
    }
}