package com.arthsethu.integration;

import com.arthsethu.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Basic integration test to verify component wiring without full context loading
 * This test focuses on verifying that the main components can be instantiated
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableAutoConfiguration(exclude = {
    SecurityAutoConfiguration.class,
    UserDetailsServiceAutoConfiguration.class,
    SecurityFilterAutoConfiguration.class
})
@ActiveProfiles("test")
public class BasicComponentWiringTest {
    
    // Mock external dependencies to avoid configuration issues
    @MockBean
    private GovernmentDataInterface governmentDataInterface;
    
    @MockBean
    private PdfGenerationService pdfGenerationService;
    
    /**
     * Test that the application context can load successfully
     * This is the most basic test - if this passes, the core wiring works
     */
    @Test
    void testApplicationContextLoads() {
        // If this test runs without exceptions, it means:
        // 1. Spring can create the application context
        // 2. All beans can be instantiated
        // 3. Dependencies can be resolved
        assertTrue(true, "Application context loaded successfully");
    }
}