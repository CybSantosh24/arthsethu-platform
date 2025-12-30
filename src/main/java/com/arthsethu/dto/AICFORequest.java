package com.arthsethu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AICFORequest {
    @NotBlank(message = "Query cannot be empty")
    @Size(max = 1000, message = "Query cannot exceed 1000 characters")
    private String query;
    
    private String context; // Optional context for what-if scenarios
    
    // Constructors
    public AICFORequest() {}
    
    public AICFORequest(String query) {
        this.query = query;
    }
    
    public AICFORequest(String query, String context) {
        this.query = query;
        this.context = context;
    }
    
    // Getters and Setters
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public String getContext() {
        return context;
    }
    
    public void setContext(String context) {
        this.context = context;
    }
}