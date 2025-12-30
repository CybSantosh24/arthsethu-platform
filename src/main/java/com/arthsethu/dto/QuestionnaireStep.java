package com.arthsethu.dto;

import com.arthsethu.model.BusinessType;
import java.util.List;
import java.util.Map;

/**
 * Represents a single step in the dynamic questionnaire flow
 */
public class QuestionnaireStep {
    private String questionId;
    private String questionText;
    private String questionType; // TEXT, NUMBER, SELECT, BOOLEAN
    private List<String> options; // For SELECT type questions
    private boolean required;
    private String validationMessage;
    private boolean isComplete;
    private String nextStepId;
    
    // Constructors
    public QuestionnaireStep() {}
    
    public QuestionnaireStep(String questionId, String questionText, String questionType, boolean required) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.required = required;
        this.isComplete = false;
    }
    
    // Static factory methods for common question types
    public static QuestionnaireStep textQuestion(String id, String text, boolean required) {
        return new QuestionnaireStep(id, text, "TEXT", required);
    }
    
    public static QuestionnaireStep numberQuestion(String id, String text, boolean required) {
        return new QuestionnaireStep(id, text, "NUMBER", required);
    }
    
    public static QuestionnaireStep selectQuestion(String id, String text, List<String> options, boolean required) {
        QuestionnaireStep step = new QuestionnaireStep(id, text, "SELECT", required);
        step.setOptions(options);
        return step;
    }
    
    public static QuestionnaireStep booleanQuestion(String id, String text, boolean required) {
        return new QuestionnaireStep(id, text, "BOOLEAN", required);
    }
    
    // Getters and Setters
    public String getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public String getQuestionType() {
        return questionType;
    }
    
    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public void setOptions(List<String> options) {
        this.options = options;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public String getValidationMessage() {
        return validationMessage;
    }
    
    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }
    
    public boolean isComplete() {
        return isComplete;
    }
    
    public void setComplete(boolean complete) {
        isComplete = complete;
    }
    
    public String getNextStepId() {
        return nextStepId;
    }
    
    public void setNextStepId(String nextStepId) {
        this.nextStepId = nextStepId;
    }
}