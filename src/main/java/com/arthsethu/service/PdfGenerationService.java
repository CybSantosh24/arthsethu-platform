package com.arthsethu.service;

import com.arthsethu.dto.CostAnalysis;
import com.arthsethu.model.FeasibilityReport;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Service for generating PDF feasibility reports
 * Implements Requirements 3.1, 3.2, 3.3, 3.4, 3.5
 */
@Service
public class PdfGenerationService {
    
    private static final Logger logger = LoggerFactory.getLogger(PdfGenerationService.class);
    
    // Color scheme for ArthSethu branding
    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(41, 128, 185); // Blue
    private static final DeviceRgb SECONDARY_COLOR = new DeviceRgb(52, 73, 94); // Dark blue-gray
    private static final DeviceRgb ACCENT_COLOR = new DeviceRgb(231, 76, 60); // Red
    private static final DeviceRgb SUCCESS_COLOR = new DeviceRgb(39, 174, 96); // Green
    
    /**
     * Generate comprehensive feasibility report PDF
     * Implements Requirements 3.1, 3.2, 3.3, 3.4, 3.5
     */
    public byte[] generateFeasibilityReportPdf(FeasibilityReport report, CostAnalysis costAnalysis) {
        logger.info("Generating PDF for feasibility report ID: {}", report.getId());
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            
            // Set document margins
            document.setMargins(50, 50, 50, 50);
            
            // Add content sections
            addHeader(document, report);
            addExecutiveSummary(document, report, costAnalysis);
            addCostBreakdown(document, costAnalysis);
            addBreakEvenAnalysis(document, costAnalysis);
            addRecommendations(document, report, costAnalysis);
            addFooter(document, report);
            
            document.close();
            
            logger.info("PDF generated successfully for report ID: {}", report.getId());
            return baos.toByteArray();
            
        } catch (Exception e) {
            logger.error("Failed to generate PDF for report ID: {}", report.getId(), e);
            throw new RuntimeException("PDF generation failed", e);
        }
    }
    
    /**
     * Add header section with ArthSethu branding and report title
     */
    private void addHeader(Document document, FeasibilityReport report) throws IOException {
        PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont subtitleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        
        // Company header
        Paragraph companyName = new Paragraph("ArthSethu")
            .setFont(titleFont)
            .setFontSize(24)
            .setFontColor(PRIMARY_COLOR)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5);
        document.add(companyName);
        
        Paragraph tagline = new Paragraph("Don't just dream. Calculate.")
            .setFont(subtitleFont)
            .setFontSize(12)
            .setFontColor(SECONDARY_COLOR)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
        document.add(tagline);
        
        // Report title
        Paragraph reportTitle = new Paragraph("Business Feasibility Report")
            .setFont(titleFont)
            .setFontSize(20)
            .setFontColor(SECONDARY_COLOR)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10);
        document.add(reportTitle);
        
        // Business details
        Table businessInfo = new Table(2);
        businessInfo.setWidth(UnitValue.createPercentValue(100));
        businessInfo.addCell(createInfoCell("Business Type:", report.getBusinessType().toString()));
        businessInfo.addCell(createInfoCell("Location:", report.getCity()));
        businessInfo.addCell(createInfoCell("Report Date:", 
            report.getGeneratedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))));
        businessInfo.addCell(createInfoCell("Report ID:", report.getId().toString()));
        
        document.add(businessInfo);
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Add executive summary section
     * Implements Requirements 3.1, 3.2
     */
    private void addExecutiveSummary(Document document, FeasibilityReport report, CostAnalysis costAnalysis) throws IOException {
        PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont bodyFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        
        // Section header
        Paragraph sectionHeader = new Paragraph("Executive Summary")
            .setFont(headerFont)
            .setFontSize(16)
            .setFontColor(PRIMARY_COLOR)
            .setMarginBottom(10);
        document.add(sectionHeader);
        
        // Summary content
        String summaryText = generateExecutiveSummaryText(report, costAnalysis);
        Paragraph summary = new Paragraph(summaryText)
            .setFont(bodyFont)
            .setFontSize(11)
            .setTextAlignment(TextAlignment.JUSTIFIED)
            .setMarginBottom(15);
        document.add(summary);
        
        // Key metrics table
        Table metricsTable = new Table(4);
        metricsTable.setWidth(UnitValue.createPercentValue(100));
        
        // Header row
        metricsTable.addHeaderCell(createHeaderCell("Total Investment"));
        metricsTable.addHeaderCell(createHeaderCell("Monthly Operating Cost"));
        metricsTable.addHeaderCell(createHeaderCell("Break-even Period"));
        metricsTable.addHeaderCell(createHeaderCell("ROI Potential"));
        
        // Data row
        metricsTable.addCell(createMetricCell("₹" + formatCurrency(costAnalysis.getTotalCapex())));
        metricsTable.addCell(createMetricCell("₹" + formatCurrency(costAnalysis.getMonthlyOpex())));
        metricsTable.addCell(createMetricCell(costAnalysis.getBreakEvenMonths() + " months"));
        metricsTable.addCell(createMetricCell(calculateROI(costAnalysis) + "%"));
        
        document.add(metricsTable);
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Add cost breakdown section with CAPEX vs OPEX analysis
     * Implements Requirements 3.3
     */
    private void addCostBreakdown(Document document, CostAnalysis costAnalysis) throws IOException {
        PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        
        // Section header
        Paragraph sectionHeader = new Paragraph("Cost Analysis")
            .setFont(headerFont)
            .setFontSize(16)
            .setFontColor(PRIMARY_COLOR)
            .setMarginBottom(10);
        document.add(sectionHeader);
        
        // CAPEX breakdown
        addCostBreakdownTable(document, "Capital Expenditure (CAPEX)", 
            costAnalysis.getCapexBreakdown(), costAnalysis.getTotalCapex());
        
        document.add(new Paragraph("\n"));
        
        // OPEX breakdown
        addCostBreakdownTable(document, "Operating Expenditure (OPEX) - Monthly", 
            costAnalysis.getOpexBreakdown(), costAnalysis.getMonthlyOpex());
        
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Add break-even analysis section with graphs
     * Implements Requirements 3.4
     */
    private void addBreakEvenAnalysis(Document document, CostAnalysis costAnalysis) throws IOException {
        PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont bodyFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        
        // Section header
        Paragraph sectionHeader = new Paragraph("Break-Even Analysis")
            .setFont(headerFont)
            .setFontSize(16)
            .setFontColor(PRIMARY_COLOR)
            .setMarginBottom(10);
        document.add(sectionHeader);
        
        // Break-even summary
        String breakEvenText = generateBreakEvenAnalysisText(costAnalysis);
        Paragraph breakEvenPara = new Paragraph(breakEvenText)
            .setFont(bodyFont)
            .setFontSize(11)
            .setTextAlignment(TextAlignment.JUSTIFIED)
            .setMarginBottom(15);
        document.add(breakEvenPara);
        
        // Break-even metrics table
        Table breakEvenTable = new Table(3);
        breakEvenTable.setWidth(UnitValue.createPercentValue(100));
        
        breakEvenTable.addHeaderCell(createHeaderCell("Metric"));
        breakEvenTable.addHeaderCell(createHeaderCell("Value"));
        breakEvenTable.addHeaderCell(createHeaderCell("Status"));
        
        // Monthly revenue required
        BigDecimal monthlyRevenueRequired = costAnalysis.getBreakEvenPoint();
        breakEvenTable.addCell(createCell("Monthly Revenue Required"));
        breakEvenTable.addCell(createCell("₹" + formatCurrency(monthlyRevenueRequired)));
        breakEvenTable.addCell(createStatusCell(monthlyRevenueRequired, costAnalysis.getProjectedRevenue()));
        
        // Break-even period
        breakEvenTable.addCell(createCell("Break-even Period"));
        breakEvenTable.addCell(createCell(costAnalysis.getBreakEvenMonths() + " months"));
        breakEvenTable.addCell(createViabilityCell(costAnalysis.getBreakEvenMonths()));
        
        // Profit margin
        BigDecimal profitMargin = calculateProfitMargin(costAnalysis);
        breakEvenTable.addCell(createCell("Projected Profit Margin"));
        breakEvenTable.addCell(createCell(profitMargin + "%"));
        breakEvenTable.addCell(createProfitabilityCell(profitMargin));
        
        document.add(breakEvenTable);
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Add recommendations section
     */
    private void addRecommendations(Document document, FeasibilityReport report, CostAnalysis costAnalysis) throws IOException {
        PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont bodyFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        
        // Section header
        Paragraph sectionHeader = new Paragraph("Recommendations")
            .setFont(headerFont)
            .setFontSize(16)
            .setFontColor(PRIMARY_COLOR)
            .setMarginBottom(10);
        document.add(sectionHeader);
        
        // Generate recommendations based on analysis
        List recommendations = generateRecommendations(report, costAnalysis);
        document.add(recommendations);
        
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Add footer with disclaimer and contact information
     */
    private void addFooter(Document document, FeasibilityReport report) throws IOException {
        PdfFont footerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        
        // Disclaimer
        Paragraph disclaimer = new Paragraph("Disclaimer: This feasibility report is based on current market data and government statistics. " +
            "Actual results may vary based on market conditions, execution quality, and external factors. " +
            "Please consult with financial advisors before making investment decisions.")
            .setFont(footerFont)
            .setFontSize(9)
            .setFontColor(SECONDARY_COLOR)
            .setTextAlignment(TextAlignment.JUSTIFIED)
            .setMarginTop(20)
            .setMarginBottom(10);
        document.add(disclaimer);
        
        // Contact information
        Paragraph contact = new Paragraph("Generated by ArthSethu Platform | www.arthsethu.com | support@arthsethu.com")
            .setFont(footerFont)
            .setFontSize(9)
            .setFontColor(SECONDARY_COLOR)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(contact);
    }
    
    // Helper methods for creating table cells and formatting
    
    private Cell createInfoCell(String label, String value) throws IOException {
        PdfFont labelFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont valueFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        
        Paragraph content = new Paragraph()
            .add(new Text(label).setFont(labelFont).setFontSize(10))
            .add(new Text(" " + value).setFont(valueFont).setFontSize(10));
        
        return new Cell().add(content).setBorder(Border.NO_BORDER).setPadding(5);
    }
    
    private Cell createHeaderCell(String text) throws IOException {
        PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        return new Cell().add(new Paragraph(text))
            .setFont(headerFont)
            .setFontSize(10)
            .setBackgroundColor(PRIMARY_COLOR)
            .setFontColor(ColorConstants.WHITE)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(8);
    }
    
    private Cell createMetricCell(String text) throws IOException {
        PdfFont cellFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        return new Cell().add(new Paragraph(text))
            .setFont(cellFont)
            .setFontSize(11)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(8);
    }
    
    private Cell createCell(String text) throws IOException {
        PdfFont cellFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        return new Cell().add(new Paragraph(text))
            .setFont(cellFont)
            .setFontSize(10)
            .setPadding(5);
    }
    
    private Cell createStatusCell(BigDecimal required, BigDecimal projected) throws IOException {
        PdfFont cellFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        String status;
        DeviceRgb color;
        
        if (projected.compareTo(required) >= 0) {
            status = "Achievable";
            color = SUCCESS_COLOR;
        } else {
            status = "Challenging";
            color = ACCENT_COLOR;
        }
        
        return new Cell().add(new Paragraph(status))
            .setFont(cellFont)
            .setFontSize(10)
            .setFontColor(color)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(5);
    }
    
    private Cell createViabilityCell(Integer breakEvenMonths) throws IOException {
        PdfFont cellFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        String status;
        DeviceRgb color;
        
        if (breakEvenMonths == null) {
            status = "Not Viable";
            color = ACCENT_COLOR;
        } else if (breakEvenMonths <= 12) {
            status = "Excellent";
            color = SUCCESS_COLOR;
        } else if (breakEvenMonths <= 24) {
            status = "Good";
            color = PRIMARY_COLOR;
        } else {
            status = "Risky";
            color = ACCENT_COLOR;
        }
        
        return new Cell().add(new Paragraph(status))
            .setFont(cellFont)
            .setFontSize(10)
            .setFontColor(color)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(5);
    }
    
    private Cell createProfitabilityCell(BigDecimal profitMargin) throws IOException {
        PdfFont cellFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        String status;
        DeviceRgb color;
        
        if (profitMargin.compareTo(new BigDecimal("20")) >= 0) {
            status = "High";
            color = SUCCESS_COLOR;
        } else if (profitMargin.compareTo(new BigDecimal("10")) >= 0) {
            status = "Moderate";
            color = PRIMARY_COLOR;
        } else if (profitMargin.compareTo(BigDecimal.ZERO) > 0) {
            status = "Low";
            color = ACCENT_COLOR;
        } else {
            status = "Negative";
            color = ACCENT_COLOR;
        }
        
        return new Cell().add(new Paragraph(status))
            .setFont(cellFont)
            .setFontSize(10)
            .setFontColor(color)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(5);
    }
    
    private void addCostBreakdownTable(Document document, String title, 
                                     Map<String, BigDecimal> breakdown, BigDecimal total) throws IOException {
        PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        
        Paragraph tableTitle = new Paragraph(title)
            .setFont(headerFont)
            .setFontSize(12)
            .setFontColor(SECONDARY_COLOR)
            .setMarginBottom(5);
        document.add(tableTitle);
        
        Table table = new Table(3);
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Headers
        table.addHeaderCell(createHeaderCell("Category"));
        table.addHeaderCell(createHeaderCell("Amount (₹)"));
        table.addHeaderCell(createHeaderCell("Percentage"));
        
        // Data rows
        for (Map.Entry<String, BigDecimal> entry : breakdown.entrySet()) {
            table.addCell(createCell(entry.getKey()));
            table.addCell(createCell(formatCurrency(entry.getValue())));
            
            BigDecimal percentage = entry.getValue()
                .divide(total, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
            table.addCell(createCell(percentage.setScale(1, RoundingMode.HALF_UP) + "%"));
        }
        
        // Total row
        table.addCell(createCell("Total").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
        table.addCell(createCell(formatCurrency(total)).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
        table.addCell(createCell("100.0%").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)));
        
        document.add(table);
    }
    
    // Business logic helper methods
    
    private String generateExecutiveSummaryText(FeasibilityReport report, CostAnalysis costAnalysis) {
        StringBuilder summary = new StringBuilder();
        
        summary.append("This feasibility analysis evaluates the viability of establishing a ")
               .append(report.getBusinessType().toString().toLowerCase().replace("_", " "))
               .append(" business in ").append(report.getCity()).append(". ");
        
        summary.append("The total initial investment required is ₹")
               .append(formatCurrency(costAnalysis.getTotalCapex()))
               .append(", with monthly operating costs of ₹")
               .append(formatCurrency(costAnalysis.getMonthlyOpex())).append(". ");
        
        if (costAnalysis.getBreakEvenMonths() != null) {
            summary.append("Based on projected revenues, the business is expected to break even within ")
                   .append(costAnalysis.getBreakEvenMonths()).append(" months. ");
        }
        
        BigDecimal profitMargin = calculateProfitMargin(costAnalysis);
        if (profitMargin.compareTo(BigDecimal.ZERO) > 0) {
            summary.append("The projected profit margin is ").append(profitMargin).append("%, ");
            if (profitMargin.compareTo(new BigDecimal("15")) >= 0) {
                summary.append("indicating a potentially profitable venture.");
            } else {
                summary.append("suggesting moderate profitability with careful management.");
            }
        } else {
            summary.append("The current projections indicate challenges in achieving profitability, requiring strategic adjustments.");
        }
        
        return summary.toString();
    }
    
    private String generateBreakEvenAnalysisText(CostAnalysis costAnalysis) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("The break-even analysis determines the minimum performance required for business viability. ");
        
        if (costAnalysis.getBreakEvenMonths() != null) {
            analysis.append("To recover the initial investment of ₹")
                   .append(formatCurrency(costAnalysis.getTotalCapex()))
                   .append(", the business needs to generate monthly profits that will accumulate over ")
                   .append(costAnalysis.getBreakEvenMonths()).append(" months. ");
            
            if (costAnalysis.getBreakEvenMonths() <= 12) {
                analysis.append("This represents an excellent recovery timeline, indicating strong business potential.");
            } else if (costAnalysis.getBreakEvenMonths() <= 24) {
                analysis.append("This is a reasonable recovery period for most businesses in this sector.");
            } else {
                analysis.append("This extended recovery period suggests higher risk and requires careful consideration.");
            }
        } else {
            analysis.append("Current projections indicate that the business may struggle to achieve profitability ")
                   .append("with the given cost structure and revenue assumptions. ")
                   .append("Consider optimizing costs or exploring additional revenue streams.");
        }
        
        return analysis.toString();
    }
    
    private List generateRecommendations(FeasibilityReport report, CostAnalysis costAnalysis) throws IOException {
        PdfFont bodyFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        List recommendations = new List();
        
        // Cost optimization recommendations
        if (costAnalysis.getTotalCapex().compareTo(new BigDecimal("500000")) > 0) {
            recommendations.add(new ListItem("Consider phased investment approach to reduce initial capital requirements."));
        }
        
        // Revenue enhancement recommendations
        BigDecimal profitMargin = calculateProfitMargin(costAnalysis);
        if (profitMargin.compareTo(new BigDecimal("15")) < 0) {
            recommendations.add(new ListItem("Explore additional revenue streams to improve profit margins."));
            recommendations.add(new ListItem("Review pricing strategy to ensure competitive yet profitable positioning."));
        }
        
        // Location-specific recommendations
        if (report.getBusinessType() == com.arthsethu.model.BusinessType.CAFE) {
            recommendations.add(new ListItem("Focus on high-traffic hours and consider extended operating hours during peak seasons."));
            recommendations.add(new ListItem("Implement loyalty programs to increase customer retention and average transaction value."));
        } else if (report.getBusinessType() == com.arthsethu.model.BusinessType.CLOUD_KITCHEN) {
            recommendations.add(new ListItem("Optimize delivery partnerships to reduce commission costs."));
            recommendations.add(new ListItem("Consider multiple cuisine offerings to maximize order volume."));
        }
        
        // Financial management recommendations
        recommendations.add(new ListItem("Maintain 3-6 months of operating expenses as working capital reserve."));
        recommendations.add(new ListItem("Implement robust financial tracking systems from day one."));
        recommendations.add(new ListItem("Consider professional consultation for tax optimization and compliance."));
        
        return recommendations;
    }
    
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0";
        return amount.setScale(0, RoundingMode.HALF_UP).toString();
    }
    
    private String calculateROI(CostAnalysis costAnalysis) {
        if (costAnalysis.getBreakEvenMonths() == null || costAnalysis.getBreakEvenMonths() == 0) {
            return "N/A";
        }
        
        // Simple ROI calculation: (Annual Profit / Investment) * 100
        BigDecimal monthlyProfit = costAnalysis.getProjectedRevenue().subtract(costAnalysis.getMonthlyOpex());
        BigDecimal annualProfit = monthlyProfit.multiply(new BigDecimal("12"));
        
        if (costAnalysis.getTotalCapex().compareTo(BigDecimal.ZERO) == 0) {
            return "N/A";
        }
        
        BigDecimal roi = annualProfit.divide(costAnalysis.getTotalCapex(), 4, RoundingMode.HALF_UP)
                                   .multiply(new BigDecimal("100"));
        
        return roi.setScale(1, RoundingMode.HALF_UP).toString();
    }
    
    private BigDecimal calculateProfitMargin(CostAnalysis costAnalysis) {
        if (costAnalysis.getProjectedRevenue().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal profit = costAnalysis.getProjectedRevenue().subtract(costAnalysis.getMonthlyOpex());
        return profit.divide(costAnalysis.getProjectedRevenue(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(1, RoundingMode.HALF_UP);
    }
}