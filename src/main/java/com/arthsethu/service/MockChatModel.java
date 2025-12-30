package com.arthsethu.service;

import org.springframework.stereotype.Component;

/**
 * Mock implementation of ChatModel for development purposes
 * This will be replaced with actual Spring AI integration when dependencies are available
 */
@Component
public class MockChatModel {
    
    public MockChatResponse call(MockPrompt prompt) {
        // Simulate AI response based on the prompt content
        String promptText = prompt.getContent().toLowerCase();
        String response;
        
        if (promptText.contains("performance") || promptText.contains("how is my business")) {
            response = generatePerformanceResponse();
        } else if (promptText.contains("cost") || promptText.contains("expense")) {
            response = generateCostAnalysisResponse();
        } else if (promptText.contains("margin") || promptText.contains("profit")) {
            response = generateMarginOptimizationResponse();
        } else if (promptText.contains("what if") || promptText.contains("simulation")) {
            response = generateSimulationResponse(promptText);
        } else {
            response = generateGenericResponse();
        }
        
        return new MockChatResponse(response);
    }
    
    private String generatePerformanceResponse() {
        return """
            Based on your recent business data, here's my analysis:
            
            **Performance Overview:**
            • Your health score indicates stable operations with room for improvement
            • Sales trends show consistent performance with seasonal variations
            • Expense management is within industry standards
            
            **Key Insights:**
            • Consider focusing on reducing wastage to improve margins
            • Your location-specific costs are competitive for your business type
            • Daily metrics tracking is providing valuable insights
            
            **Recommendations:**
            1. Implement waste reduction strategies
            2. Monitor peak sales periods for optimization opportunities
            3. Consider expanding successful product lines
            
            Would you like me to dive deeper into any specific aspect?
            """;
    }
    
    private String generateCostAnalysisResponse() {
        return """
            **Cost Analysis Summary:**
            
            **Major Cost Drivers:**
            • Raw materials/inventory: 40-45% of expenses
            • Labor costs: 25-30% of expenses  
            • Rent and utilities: 15-20% of expenses
            • Packaging and supplies: 5-10% of expenses
            
            **Optimization Opportunities:**
            1. **Supplier Negotiation**: Review contracts with top suppliers
            2. **Inventory Management**: Implement just-in-time ordering
            3. **Energy Efficiency**: Consider LED lighting and efficient equipment
            4. **Bulk Purchasing**: Leverage volume discounts for non-perishables
            
            **Immediate Actions:**
            • Track daily waste percentages
            • Compare supplier prices monthly
            • Monitor utility usage patterns
            
            Your cost structure is typical for your business type, but there's potential for 8-12% reduction through strategic optimization.
            """;
    }
    
    private String generateMarginOptimizationResponse() {
        return """
            **Profit Margin Optimization Strategy:**
            
            **Current Margin Analysis:**
            • Your margins are within industry range but can be improved
            • Wastage is impacting profitability more than pricing
            • Peak hours show better margin performance
            
            **Optimization Strategies:**
            
            **1. Pricing Optimization:**
            • Test 5-8% price increases on high-demand items
            • Implement dynamic pricing for peak/off-peak hours
            • Bundle complementary products
            
            **2. Cost Reduction:**
            • Focus on waste reduction (highest impact)
            • Negotiate better supplier terms
            • Optimize staff scheduling
            
            **3. Revenue Enhancement:**
            • Upselling training for staff
            • Loyalty programs to increase frequency
            • Seasonal menu/product optimization
            
            **Expected Impact:** 15-25% margin improvement over 3-6 months
            
            Would you like me to create a specific action plan for any of these strategies?
            """;
    }
    
    private String generateSimulationResponse(String scenario) {
        return String.format("""
            **What-If Analysis: %s**
            
            **Scenario Impact Assessment:**
            
            **Financial Projections:**
            • Revenue Impact: +12-18%% (estimated)
            • Cost Impact: +5-8%% (estimated)
            • Net Margin Change: +8-12%% (estimated)
            
            **Risk Factors:**
            • Customer price sensitivity
            • Competitor response
            • Implementation costs
            
            **Implementation Timeline:**
            • Phase 1 (Week 1-2): Planning and preparation
            • Phase 2 (Week 3-4): Gradual rollout
            • Phase 3 (Week 5-8): Full implementation and monitoring
            
            **Success Metrics:**
            • Daily sales volume changes
            • Customer retention rates
            • Profit margin improvements
            
            **Recommendation:** Proceed with a pilot test on 20%% of products/services first to validate assumptions before full rollout.
            
            **Impact Score: 75/100** - High potential with manageable risks
            
            Would you like me to break down the implementation steps in more detail?
            """, scenario);
    }
    
    private String generateGenericResponse() {
        return """
            I'm here to help you with strategic financial guidance for your business. I can assist with:
            
            **Financial Analysis:**
            • Performance reviews and health score interpretation
            • Cost analysis and optimization opportunities
            • Profit margin improvement strategies
            
            **Strategic Planning:**
            • What-if scenario simulations
            • Growth strategy recommendations
            • Risk assessment and mitigation
            
            **Operational Insights:**
            • Daily metrics interpretation
            • Trend analysis and forecasting
            • Benchmarking against industry standards
            
            What specific aspect of your business would you like to explore? I have access to your business profile and recent performance data to provide personalized insights.
            """;
    }
    
    // Mock classes to simulate Spring AI interfaces
    public static class MockPrompt {
        private final String content;
        
        public MockPrompt(String content) {
            this.content = content;
        }
        
        public String getContent() {
            return content;
        }
    }
    
    public static class MockChatResponse {
        private final String content;
        
        public MockChatResponse(String content) {
            this.content = content;
        }
        
        public MockResult getResult() {
            return new MockResult(content);
        }
    }
    
    public static class MockResult {
        private final MockOutput output;
        
        public MockResult(String content) {
            this.output = new MockOutput(content);
        }
        
        public MockOutput getOutput() {
            return output;
        }
    }
    
    public static class MockOutput {
        private final String content;
        
        public MockOutput(String content) {
            this.content = content;
        }
        
        public String getContent() {
            return content;
        }
    }
}