# AI Models Setup Guide for ArthSethu

## 🎯 Current Status: ENABLED

Your application is now configured to use **real AI models** instead of the mock implementation!

## 🚀 Quick Start with Ollama (Recommended)

### Step 1: Install Ollama

1. **Download Ollama**
   - Visit: https://ollama.ai
   - Download for your operating system
   - Install following the instructions

2. **Verify Installation**
   ```bash
   ollama --version
   ```

### Step 2: Download AI Models

Choose one of these models based on your system capabilities:

**For Development/Testing (Lighter models):**
```bash
ollama pull phi3:mini          # ~2.3GB - Fast, good for testing
ollama pull llama3.2:1b        # ~1.3GB - Very fast, basic responses
```

**For Production (Better quality):**
```bash
ollama pull llama3.2:3b        # ~2GB - Good balance of speed/quality
ollama pull llama3.2:8b        # ~4.7GB - High quality responses
ollama pull llama3.2:70b       # ~40GB - Best quality (requires 64GB+ RAM)
```

**Recommended for your setup:**
```bash
ollama pull llama3.2:3b
```

### Step 3: Start Ollama Service

```bash
ollama serve
```

This will start Ollama on `http://localhost:11434` (default port).

### Step 4: Update Your Model Configuration

Update `src/main/resources/application.properties`:

```properties
# Change this line to match your downloaded model
spring.ai.ollama.chat.model=llama3.2:3b
```

### Step 5: Start Your Application

```bash
./mvnw spring-boot:run
```

## 🧪 Testing the AI Integration

1. **Start your application**
2. **Login as admin** (admin@arthsethu.com / admin123)
3. **Go to Admin Dashboard** → System Status
4. **Check AI Service Status** - Should show "Online"
5. **Test AI CFO Chat** - Go to AI CFO section and ask a question

## 🔧 Alternative AI Models

### Option 2: OpenAI Integration

If you prefer OpenAI (requires API key):

1. **Add OpenAI dependency to pom.xml:**
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

2. **Update application.properties:**
```properties
spring.ai.openai.api-key=your-openai-api-key-here
spring.ai.openai.chat.model=gpt-3.5-turbo
```

3. **Create OpenAI Configuration:**
```java
@Configuration
public class OpenAIConfig {
    @Bean
    public OpenAiChatModel openAiChatModel() {
        return new OpenAiChatModel(new OpenAiApi(apiKey));
    }
}
```

### Option 3: Azure OpenAI

For enterprise Azure OpenAI:

1. **Add Azure OpenAI dependency:**
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-azure-openai</artifactId>
</dependency>
```

2. **Configure Azure OpenAI:**
```properties
spring.ai.azure.openai.api-key=your-azure-key
spring.ai.azure.openai.endpoint=https://your-resource.openai.azure.com/
spring.ai.azure.openai.chat.model=gpt-35-turbo
```

## 🛠️ Troubleshooting

### Common Issues:

1. **"Connection refused" error**
   - Make sure Ollama is running: `ollama serve`
   - Check if port 11434 is available
   - Verify firewall settings

2. **"Model not found" error**
   - List available models: `ollama list`
   - Pull the model: `ollama pull llama3.2:3b`
   - Update application.properties with correct model name

3. **Slow responses**
   - Use a smaller model (phi3:mini)
   - Increase system RAM
   - Close other applications

4. **Out of memory errors**
   - Use smaller models
   - Increase JVM heap size: `-Xmx4g`
   - Monitor system resources

### Performance Tips:

1. **Model Selection:**
   - Development: `phi3:mini` or `llama3.2:1b`
   - Production: `llama3.2:3b` or `llama3.2:8b`

2. **System Requirements:**
   - Minimum: 8GB RAM for small models
   - Recommended: 16GB+ RAM for better models
   - SSD storage for faster model loading

3. **Configuration Tuning:**
   ```properties
   # Adjust temperature for creativity vs consistency
   spring.ai.ollama.chat.options.temperature=0.7
   
   # Increase timeout for complex queries
   arthsethu.ollama.timeout=60000
   ```

## 📊 Monitoring AI Performance

Your application includes built-in monitoring:

1. **Admin Dashboard** → System Status → AI Service
2. **Health checks** run automatically
3. **Error logging** in application logs
4. **Response time tracking** in admin metrics

## 🔄 Switching Between Models

You can easily switch between different AI providers:

1. **Change dependencies** in pom.xml
2. **Update configuration** in application.properties
3. **Restart application**

The `ChatModel` interface ensures compatibility across all providers.

## 🎯 What's Changed in Your Application

✅ **Real AI Integration**: No more mock responses
✅ **Spring AI Framework**: Industry-standard AI integration
✅ **Flexible Model Support**: Easy to switch between providers
✅ **Error Handling**: Graceful fallbacks and error messages
✅ **Performance Monitoring**: Built-in health checks
✅ **Production Ready**: Proper configuration and logging

Your AI CFO will now provide real, intelligent responses based on your business data!

## 🚀 Next Steps

1. **Install and test Ollama** with a small model
2. **Verify AI integration** works in your application
3. **Experiment with different models** to find the best fit
4. **Consider cloud AI providers** for production scaling
5. **Monitor performance** and adjust configuration as needed

Happy AI-powered financial analysis! 🤖💰