package com.arthsethu.integration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import com.arthsethu.service.GovernmentDataInterface;
import com.arthsethu.service.PdfGenerationService;

/**
 * Test configuration for integration tests
 * Provides mock beans to avoid external dependencies and disables security
 */
@TestConfiguration
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@ComponentScan(basePackages = "com.arthsethu",
               excludeFilters = {
                   @ComponentScan.Filter(type = FilterType.REGEX, 
                                       pattern = "com\\.arthsethu\\.config\\.TestConfig"),
                   @ComponentScan.Filter(type = FilterType.REGEX, 
                                       pattern = "com\\.arthsethu\\.config\\.SecurityConfig")
               })
public class IntegrationTestConfiguration {
    
    @Bean("integrationTestRestTemplate")
    @Primary
    public RestTemplate integrationTestRestTemplate() {
        return mock(RestTemplate.class);
    }
    
    @Bean("integrationTestGovernmentDataInterface")
    @Primary
    public GovernmentDataInterface integrationTestGovernmentDataInterface() {
        GovernmentDataInterface mock = mock(GovernmentDataInterface.class);
        when(mock.isGovernmentDataAvailable()).thenReturn(true);
        return mock;
    }
    
    @Bean("integrationTestPdfGenerationService")
    @Primary
    public PdfGenerationService integrationTestPdfGenerationService() {
        return mock(PdfGenerationService.class);
    }
}