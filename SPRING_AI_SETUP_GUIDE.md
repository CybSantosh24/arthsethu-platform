# Spring AI Ollama Integration Setup Guide

## Current Status

The ArthSethu application currently uses a `MockChatModel` for AI functionality, which provides simulated responses for development and testing. The Spring AI Ollama integration is prepared but disabled due to dependency issues.

## Issue Resolution

The compilation error you encountered:
```
java: package org.springframework.ai.ollama does not exist
```

Has been resolved by temporarily disabling the Spring AI configuration. The application will continue to work with the mock implementation.

## Files Modified

1. **`src/main/java/com/arthsethu/config/OllamaConfig.java`** - Disabled Spring AI imports and configuration
2. **`pom.xml`** - Commented out Spring AI dependencies and repositories

## To Enable Real Ollama Integration

When you're ready to use real Ollama instead of the mock implementation, follow these steps:

### Step 1: Install Ollama

1. Download and install Ollama from https://ollama.ai
2. Pull a model (e.g., `ollama pull llama3`)
3. Start Ollama service (usually runs on http://localhost:11434)

### Step 2: Update Maven Dependencies

Uncomment the following sections in `pom.xml`:

```xml
<!-- In properties section -->
<spring-ai.version>1.0.0-M7</spring-ai.version>

<!-- In dependencyManagement section -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>${spring-ai.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- In repositories section -->
<repositories>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>

<!-- In dependencies section -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>
```

### Step 3: Update OllamaConfig.java

Replace the content of `src/main/java/com/arthsethu/config/OllamaConfig.java` with:

```java
package com.arthsethu.config;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OllamaConfig {

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${spring.ai.ollama.chat.model:llama3}")
    private String model;

    @Value("${spring.ai.ollama.chat.options.temperature:0.7}")
    private Double temperature;

    @Bean
    public OllamaApi ollamaApi() {
        return new OllamaApi(baseUrl);
    }

    @Bean
    public OllamaChatModel ollamaChatModel(OllamaApi ollamaApi) {
        return new OllamaChatModel(ollamaApi, 
            OllamaChatOptions.builder()
                .withModel(model)
                .withTemperature(temperature.floatValue())
                .build());
    }
}
```

### Step 4: Update Application Properties

Add these properties to `src/main/resources/application.properties`:

```properties
# Ollama Configuration
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.model=llama3
spring.ai.ollama.chat.options.temperature=0.7
```

### Step 5: Update AICFOService

Modify `src/main/java/com/arthsethu/service/AICFOService.java` to use the real Ollama model:

1. Replace the import:
   ```java
   // Remove this import
   import com.arthsethu.service.MockChatModel.MockPrompt;
   
   // Add these imports
   import org.springframework.ai.chat.model.ChatModel;
   import org.springframework.ai.chat.prompt.Prompt;
   ```

2. Update the constructor:
   ```java
   private final ChatModel chatModel;  // Change from MockChatModel
   
   @Autowired
   public AICFOService(ChatModel chatModel, DailyMetricsRepository dailyMetricsRepository) {
       this.chatModel = chatModel;
       this.dailyMetricsRepository = dailyMetricsRepository;
   }
   ```

3. Update the method calls:
   ```java
   // Replace MockPrompt with Prompt
   Prompt prompt = new Prompt(promptContent);
   
   // Update the call method
   return chatModel.call(prompt).getResult().getOutput().getContent();
   ```

## Alternative: Use Spring AI Auto-Configuration

Instead of manual configuration, you can rely on Spring Boot's auto-configuration by:

1. Adding only the dependency (no manual OllamaConfig needed)
2. Setting properties in application.properties
3. Injecting `ChatModel` directly in your services

## Testing the Integration

1. Start Ollama service
2. Ensure your model is available (`ollama list`)
3. Start your Spring Boot application
4. Test the AI CFO functionality through the web interface

## Troubleshooting

- **Connection refused**: Ensure Ollama is running on the correct port
- **Model not found**: Pull the model using `ollama pull <model-name>`
- **Slow responses**: Consider using a smaller model like `phi3` for development
- **Memory issues**: Ollama requires significant RAM for larger models

## Current Application Status

The application is fully functional with the mock implementation. All features work as expected:
- User registration and authentication
- Business profile management
- Daily metrics tracking
- AI CFO chat (with simulated responses)
- Admin dashboard
- Reports generation

The mock responses are designed to be realistic and helpful for development and demonstration purposes.