package com.arthsethu.config;

import com.arthsethu.service.GovernmentDataInterface;
import com.arthsethu.service.PdfGenerationService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

/**
 * Test configuration to provide mock beans for testing
 */
@TestConfiguration
public class TestConfig {
    
    @Bean("testRestTemplate")
    @Primary
    public RestTemplate testRestTemplate() {
        return mock(RestTemplate.class);
    }
    
    @Bean("testGovernmentDataInterface")
    @Primary
    public GovernmentDataInterface testGovernmentDataInterface() {
        return mock(GovernmentDataInterface.class);
    }
    
    @Bean("testPdfGenerationService")
    @Primary
    public PdfGenerationService testPdfGenerationService() {
        return mock(PdfGenerationService.class);
    }
}