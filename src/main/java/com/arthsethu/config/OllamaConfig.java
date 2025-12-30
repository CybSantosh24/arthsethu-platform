package com.arthsethu.config;

import org.springframework.context.annotation.Configuration;

/**
 * Ollama Configuration for Spring AI Integration
 * 
 * Using Spring Boot auto-configuration for Ollama integration.
 * Configuration is handled through application.properties:
 * 
 * spring.ai.ollama.base-url=http://localhost:11434
 * spring.ai.ollama.chat.model=llama3.2
 * spring.ai.ollama.chat.options.temperature=0.7
 */
@Configuration
public class OllamaConfig {
    // Spring Boot auto-configuration handles the ChatModel bean creation
    // No manual configuration needed
}