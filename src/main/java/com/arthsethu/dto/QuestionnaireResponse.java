package com.arthsethu.dto;

import com.arthsethu.model.BusinessType;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a user's response to questionnaire questions
 */
public class QuestionnaireResponse {
    @NotNull
    private String questionId;
    
    private Object answer;
    
    @NotNull
    private BusinessType businessType;
    
    private Map<String, Object> previousResponses;
    
    // Constructors
    public QuestionnaireResponse() {
        this.previousResponses = new HashMap<>();
    }
    
    public QuestionnaireResponse(String questionId, Object answer, BusinessType businessType) {
        this();
        this.questionId = questionId;
        this.answer = answer;
        this.businessType = businessType;
    }
    
    // Getters and Setters
    public String getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    
    public Object getAnswer() {
        return answer;
    }
    
    public void setAnswer(Object answer) {
        this.answer = answer;
    }
    
    public BusinessType getBusinessType() {
        return businessType;
    }
    
    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }
    
    public Map<String, Object> getPreviousResponses() {
        return previousResponses;
    }
    
    public void setPreviousResponses(Map<String, Object> previousResponses) {
        this.previousResponses = previousResponses;
    }
    
    // Utility methods
    public void addPreviousResponse(String questionId, Object answer) {
        this.previousResponses.put(questionId, answer);
    }
    
    public Object getPreviousResponse(String questionId) {
        return this.previousResponses.get(questionId);
    }
    
    public boolean hasPreviousResponse(String questionId) {
        return this.previousResponses.containsKey(questionId);
    }
}