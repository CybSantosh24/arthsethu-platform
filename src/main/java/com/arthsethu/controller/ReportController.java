package com.arthsethu.controller;

import com.arthsethu.model.FeasibilityReport;
import com.arthsethu.model.User;
import com.arthsethu.repository.FeasibilityReportRepository;
import com.arthsethu.service.FeasibilityEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controller for handling feasibility report downloads
 * Implements Requirements 3.5
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    
    private final FeasibilityReportRepository feasibilityReportRepository;
    private final FeasibilityEngineService feasibilityEngineService;
    
    public ReportController(FeasibilityReportRepository feasibilityReportRepository,
                          FeasibilityEngineService feasibilityEngineService) {
        this.feasibilityReportRepository = feasibilityReportRepository;
        this.feasibilityEngineService = feasibilityEngineService;
    }
    
    /**
     * Download feasibility report as PDF
     * Implements Requirements 3.5 - one-click PDF download functionality
     */
    @GetMapping("/download/{reportId}")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long reportId) {
        logger.info("Request to download report with ID: {}", reportId);
        
        try {
            // Find the report
            Optional<FeasibilityReport> reportOpt = feasibilityReportRepository.findById(reportId);
            if (reportOpt.isEmpty()) {
                logger.warn("Report not found with ID: {}", reportId);
                return ResponseEntity.notFound().build();
            }
            
            FeasibilityReport report = reportOpt.get();
            
            // Verify user has access to this report (basic security check)
            if (!hasAccessToReport(report)) {
                logger.warn("User does not have access to report ID: {}", reportId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Generate or retrieve PDF content
            byte[] pdfContent;
            if (report.isPdfGenerated()) {
                logger.info("Using existing PDF content for report ID: {}", reportId);
                pdfContent = report.getPdfContent();
            } else {
                logger.info("Generating new PDF content for report ID: {}", reportId);
                pdfContent = feasibilityEngineService.generatePDFReport(report);
                
                // Save the generated PDF content to the database
                report.setPdfContent(pdfContent);
                feasibilityReportRepository.save(report);
            }
            
            // Create filename with business type, city, and date
            String filename = generateFilename(report);
            
            // Set proper headers for PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfContent.length);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            
            logger.info("Successfully serving PDF download for report ID: {} with filename: {}", reportId, filename);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
                    
        } catch (Exception e) {
            logger.error("Error downloading report ID: {}", reportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get report metadata without downloading the PDF
     */
    @GetMapping("/{reportId}/info")
    public ResponseEntity<ReportInfoResponse> getReportInfo(@PathVariable Long reportId) {
        logger.info("Request for report info with ID: {}", reportId);
        
        try {
            Optional<FeasibilityReport> reportOpt = feasibilityReportRepository.findById(reportId);
            if (reportOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            FeasibilityReport report = reportOpt.get();
            
            // Verify user has access to this report
            if (!hasAccessToReport(report)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            ReportInfoResponse response = new ReportInfoResponse(
                report.getId(),
                report.getBusinessType().toString(),
                report.getCity(),
                report.getGeneratedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")),
                report.isPdfGenerated(),
                generateFilename(report)
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting report info for ID: {}", reportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Regenerate PDF for a report (force refresh)
     */
    @PostMapping("/{reportId}/regenerate")
    public ResponseEntity<String> regenerateReport(@PathVariable Long reportId) {
        logger.info("Request to regenerate PDF for report ID: {}", reportId);
        
        try {
            Optional<FeasibilityReport> reportOpt = feasibilityReportRepository.findById(reportId);
            if (reportOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            FeasibilityReport report = reportOpt.get();
            
            // Verify user has access to this report
            if (!hasAccessToReport(report)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Generate new PDF content
            byte[] pdfContent = feasibilityEngineService.generatePDFReport(report);
            
            // Update the report with new PDF content
            report.setPdfContent(pdfContent);
            feasibilityReportRepository.save(report);
            
            logger.info("Successfully regenerated PDF for report ID: {}", reportId);
            return ResponseEntity.ok("PDF regenerated successfully");
            
        } catch (Exception e) {
            logger.error("Error regenerating PDF for report ID: {}", reportId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to regenerate PDF");
        }
    }
    
    /**
     * Check if current user has access to the report
     * Basic security check - in a real application, this would be more sophisticated
     */
    private boolean hasAccessToReport(FeasibilityReport report) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // If no authentication, deny access
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        // For now, allow access if user is authenticated
        // In a real application, you would check if the authenticated user owns the report
        // or has appropriate permissions
        return true;
    }
    
    /**
     * Generate appropriate filename for the PDF download
     */
    private String generateFilename(FeasibilityReport report) {
        String businessType = report.getBusinessType().toString().toLowerCase().replace("_", "-");
        String city = report.getCity().toLowerCase().replace(" ", "-");
        String date = report.getGeneratedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        return String.format("arthsethu-feasibility-%s-%s-%s.pdf", businessType, city, date);
    }
    
    /**
     * Response DTO for report information
     */
    public static class ReportInfoResponse {
        private final Long id;
        private final String businessType;
        private final String city;
        private final String generatedAt;
        private final boolean pdfGenerated;
        private final String filename;
        
        public ReportInfoResponse(Long id, String businessType, String city, 
                                String generatedAt, boolean pdfGenerated, String filename) {
            this.id = id;
            this.businessType = businessType;
            this.city = city;
            this.generatedAt = generatedAt;
            this.pdfGenerated = pdfGenerated;
            this.filename = filename;
        }
        
        // Getters
        public Long getId() { return id; }
        public String getBusinessType() { return businessType; }
        public String getCity() { return city; }
        public String getGeneratedAt() { return generatedAt; }
        public boolean isPdfGenerated() { return pdfGenerated; }
        public String getFilename() { return filename; }
    }
}