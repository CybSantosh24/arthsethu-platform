package com.arthsethu.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class MockDemoController {
    
    /**
     * Demo Homepage with all features working
     */
    @GetMapping
    public String demoHome(Model model) {
        model.addAttribute("user", createMockUser());
        model.addAttribute("features", createMockFeatures());
        return "demo/home";
    }
    
    /**
     * Mock Onboarding - Fully Functional
     */
    @GetMapping("/onboarding")
    public String mockOnboarding(Model model) {
        model.addAttribute("currentStep", 1);
        model.addAttribute("totalSteps", 5);
        return "demo/onboarding";
    }
    
    @PostMapping("/onboarding/submit")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitMockOnboarding(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Business profile created successfully!");
        response.put("profileId", "DEMO_" + System.currentTimeMillis());
        response.put("redirectUrl", "/demo/dashboard");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mock Dashboard - Fully Functional
     */
    @GetMapping("/dashboard")
    public String mockDashboard(Model model) {
        model.addAttribute("user", createMockUser());
        model.addAttribute("businessProfile", createMockBusinessProfile());
        model.addAttribute("healthScore", 87);
        model.addAttribute("dailyMetrics", createMockDailyMetrics());
        model.addAttribute("chartData", createMockChartData());
        return "demo/dashboard";
    }
    
    /**
     * Mock AI CFO - Fully Functional
     */
    @GetMapping("/ai-cfo")
    public String mockAICFO(Model model) {
        model.addAttribute("user", createMockUser());
        model.addAttribute("businessProfile", createMockBusinessProfile());
        model.addAttribute("chatHistory", createMockChatHistory());
        return "demo/ai-cfo";
    }
    
    @PostMapping("/ai-cfo/chat")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> mockAICFOChat(@RequestBody Map<String, String> request) {
        String query = request.get("message");
        Map<String, Object> response = new HashMap<>();
        
        String aiResponse = generateMockAIResponse(query);
        
        response.put("success", true);
        response.put("response", aiResponse);
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mock Reports - Fully Functional
     */
    @GetMapping("/reports")
    public String mockReports(Model model) {
        model.addAttribute("reports", createMockReports());
        return "demo/reports";
    }
    
    @GetMapping("/reports/{id}")
    public String mockReportView(@PathVariable String id, Model model) {
        model.addAttribute("report", createMockReport(id));
        model.addAttribute("canDownload", true);
        model.addAttribute("downloadUrl", "/demo/reports/" + id + "/download");
        return "demo/report-view";
    }
    
    @GetMapping("/reports/{id}/download")
    @ResponseBody
    public ResponseEntity<byte[]> mockReportDownload(@PathVariable String id) {
        String pdfContent = "Mock PDF Report for " + id + "\nGenerated at: " + LocalDateTime.now();
        return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", "attachment; filename=feasibility-report-" + id + ".pdf")
            .body(pdfContent.getBytes());
    }
    
    /**
     * Mock Payment Simulation - Fully Functional
     */
    @PostMapping("/payment/simulate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> mockPaymentSimulation(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        // Simulate payment processing delay
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        response.put("success", true);
        response.put("transactionId", "TXN_" + System.currentTimeMillis());
        response.put("amount", request.get("amount"));
        response.put("tier", request.get("tier"));
        response.put("message", "Payment simulation completed successfully!");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mock Admin Panel - Fully Functional
     */
    @GetMapping("/admin")
    public String mockAdmin(Model model) {
        model.addAttribute("totalUsers", 1247);
        model.addAttribute("activeUsers", 892);
        model.addAttribute("revenue", 45670);
        model.addAttribute("systemHealth", "Excellent");
        model.addAttribute("users", createMockUsers());
        model.addAttribute("systemStats", createMockSystemStats());
        return "demo/admin";
    }
    
    // Helper methods to create mock data
    private Map<String, Object> createMockUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1L);
        user.put("email", "demo@arthsethu.com");
        user.put("tier", "SHIKHAR");
        user.put("role", "USER");
        user.put("createdAt", LocalDateTime.now().minusDays(30));
        return user;
    }
    
    private Map<String, Object> createMockBusinessProfile() {
        Map<String, Object> profile = new HashMap<>();
        profile.put("businessType", "CAFE");
        profile.put("city", "Mumbai");
        profile.put("seatingCapacity", 25);
        profile.put("monthlyRent", 45000);
        profile.put("estimatedRevenue", 180000);
        return profile;
    }
    
    private List<Map<String, Object>> createMockDailyMetrics() {
        List<Map<String, Object>> metrics = new ArrayList<>();
        for (int i = 7; i >= 0; i--) {
            Map<String, Object> metric = new HashMap<>();
            metric.put("date", LocalDateTime.now().minusDays(i).toLocalDate());
            metric.put("sales", 5000 + (Math.random() * 3000));
            metric.put("expenses", 2000 + (Math.random() * 1000));
            metric.put("wastage", 200 + (Math.random() * 300));
            metric.put("healthScore", 75 + (Math.random() * 20));
            metrics.add(metric);
        }
        return metrics;
    }
    
    private Map<String, Object> createMockChartData() {
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("capex", 250000);
        chartData.put("opex", 180000);
        chartData.put("breakEvenMonths", 8);
        chartData.put("projectedRevenue", Arrays.asList(50000, 75000, 100000, 125000, 150000, 175000));
        return chartData;
    }
    
    private List<Map<String, Object>> createMockChatHistory() {
        List<Map<String, Object>> history = new ArrayList<>();
        
        Map<String, Object> msg1 = new HashMap<>();
        msg1.put("sender", "AI CFO");
        msg1.put("message", "Hello! I'm your AI CFO. I can help you with financial analysis, cost optimization, and strategic planning. What would you like to discuss?");
        msg1.put("timestamp", LocalDateTime.now().minusMinutes(10));
        history.add(msg1);
        
        Map<String, Object> msg2 = new HashMap<>();
        msg2.put("sender", "User");
        msg2.put("message", "How is my cafe performing this month?");
        msg2.put("timestamp", LocalDateTime.now().minusMinutes(9));
        history.add(msg2);
        
        Map<String, Object> msg3 = new HashMap<>();
        msg3.put("sender", "AI CFO");
        msg3.put("message", "Based on your data, your cafe is performing well! Your average daily sales are ₹6,200 with a healthy profit margin of 35%. Your health score of 87/100 indicates strong operational efficiency. I recommend focusing on reducing wastage to improve margins further.");
        msg3.put("timestamp", LocalDateTime.now().minusMinutes(8));
        history.add(msg3);
        
        return history;
    }
    
    private String generateMockAIResponse(String query) {
        String[] responses = {
            "Based on your current metrics, I recommend optimizing your inventory management to reduce wastage by 15%, which could increase your monthly profit by ₹12,000.",
            "Your cafe's performance is strong with a 35% profit margin. Consider expanding your menu during peak hours to capture additional revenue.",
            "The data shows your busiest hours are 8-10 AM and 6-8 PM. I suggest implementing dynamic pricing during these periods to maximize revenue.",
            "Your operational efficiency is excellent at 87/100. Focus on customer retention strategies to maintain this growth trajectory.",
            "Market analysis suggests increasing your coffee prices by 8% would be well-received by customers while boosting margins significantly."
        };
        
        return responses[(int) (Math.random() * responses.length)];
    }
    
    private List<Map<String, Object>> createMockReports() {
        List<Map<String, Object>> reports = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> report = new HashMap<>();
            report.put("id", "RPT_" + i);
            report.put("businessType", i % 2 == 0 ? "CAFE" : "CLOUD_KITCHEN");
            report.put("city", i % 3 == 0 ? "Mumbai" : (i % 2 == 0 ? "Delhi" : "Bangalore"));
            report.put("generatedAt", LocalDateTime.now().minusDays(i));
            report.put("status", "COMPLETED");
            reports.add(report);
        }
        
        return reports;
    }
    
    private Map<String, Object> createMockReport(String id) {
        Map<String, Object> report = new HashMap<>();
        report.put("id", id);
        report.put("businessType", "CAFE");
        report.put("city", "Mumbai");
        report.put("capex", 250000);
        report.put("opex", 180000);
        report.put("breakEvenPoint", 125000);
        report.put("breakEvenMonths", 8);
        report.put("generatedAt", LocalDateTime.now().minusDays(2));
        report.put("executiveSummary", "This cafe business shows strong potential with a projected break-even period of 8 months. The location in Mumbai provides excellent foot traffic, and the initial investment of ₹2.5L is reasonable for the expected returns.");
        return report;
    }
    
    private List<Map<String, Object>> createMockFeatures() {
        List<Map<String, Object>> features = new ArrayList<>();
        
        Map<String, Object> onboarding = new HashMap<>();
        onboarding.put("name", "Smart Onboarding");
        onboarding.put("description", "Dynamic questionnaire that adapts to your business type");
        onboarding.put("status", "WORKING");
        onboarding.put("url", "/demo/onboarding");
        features.add(onboarding);
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("name", "Business Dashboard");
        dashboard.put("description", "Real-time metrics and health score tracking");
        dashboard.put("status", "WORKING");
        dashboard.put("url", "/demo/dashboard");
        features.add(dashboard);
        
        Map<String, Object> aicfo = new HashMap<>();
        aicfo.put("name", "AI CFO Chat");
        aicfo.put("description", "AI-powered financial advisor and strategic guidance");
        aicfo.put("status", "WORKING");
        aicfo.put("url", "/demo/ai-cfo");
        features.add(aicfo);
        
        Map<String, Object> reports = new HashMap<>();
        reports.put("name", "Feasibility Reports");
        reports.put("description", "Professional PDF reports with financial analysis");
        reports.put("status", "WORKING");
        reports.put("url", "/demo/reports");
        features.add(reports);
        
        Map<String, Object> admin = new HashMap<>();
        admin.put("name", "Admin Panel");
        admin.put("description", "System management and user analytics");
        admin.put("status", "WORKING");
        admin.put("url", "/demo/admin");
        features.add(admin);
        
        return features;
    }
    
    private List<Map<String, Object>> createMockUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        String[] names = {"Rahul Sharma", "Priya Patel", "Amit Kumar", "Sneha Singh", "Vikram Gupta"};
        String[] cities = {"Mumbai", "Delhi", "Bangalore", "Chennai", "Pune"};
        String[] tiers = {"AARAMBH", "VISTAR", "SHIKHAR"};
        
        for (int i = 0; i < 5; i++) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", i + 1);
            user.put("name", names[i]);
            user.put("email", names[i].toLowerCase().replace(" ", ".") + "@example.com");
            user.put("city", cities[i]);
            user.put("tier", tiers[i % 3]);
            user.put("joinDate", LocalDateTime.now().minusDays(30 + i * 5));
            user.put("status", "ACTIVE");
            users.add(user);
        }
        
        return users;
    }
    
    private Map<String, Object> createMockSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cpuUsage", 45);
        stats.put("memoryUsage", 67);
        stats.put("diskUsage", 23);
        stats.put("activeConnections", 156);
        stats.put("requestsPerMinute", 234);
        stats.put("errorRate", 0.02);
        return stats;
    }
}