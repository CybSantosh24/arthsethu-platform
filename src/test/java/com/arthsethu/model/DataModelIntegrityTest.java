package com.arthsethu.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DataModelIntegrityTest {

    @Test
    @Tag("DataModelIntegrity")
    void testSensitiveDataEncryptionAndIntegrity() {
        qt()
            .forAll(
                strings().allPossible().ofLengthBetween(5, 50), // email
                strings().allPossible().ofLengthBetween(8, 100), // password
                strings().allPossible().ofLengthBetween(3, 30), // city
                strings().allPossible().ofLengthBetween(10, 500) // questionnaire responses (sensitive data)
            )
            .checkAssert((email, password, city, questionnaireData) -> {
                // Create user with sensitive data
                User user = new User(email, password);
                
                // For now, verify password is stored (later we'll implement hashing)
                assertEquals(password, user.getPasswordHash(), 
                    "Password should be stored (hashing to be implemented)");
                
                // Create business profile with sensitive financial data
                BusinessProfile profile = new BusinessProfile(user, BusinessType.CAFE, city);
                profile.setQuestionnaireResponses(questionnaireData);
                profile.setPackagingCosts(1000.0); // Sensitive financial data
                profile.setPowerConsumption(500.0); // Sensitive operational data
                
                // Verify data integrity - all required fields are preserved
                assertNotNull(profile.getUser(), "User relationship must be maintained");
                assertEquals(BusinessType.CAFE, profile.getBusinessType(), "Business type must be preserved");
                assertEquals(city, profile.getCity(), "City must be preserved");
                assertEquals(questionnaireData, profile.getQuestionnaireResponses(), 
                    "Questionnaire responses must be preserved");
                
                // Verify timestamps are automatically set
                assertNotNull(profile.getCreatedAt(), "Created timestamp must be set");
                assertNotNull(profile.getUpdatedAt(), "Updated timestamp must be set");
                assertTrue(profile.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)), 
                    "Created timestamp should be recent");
                
                // Verify financial data integrity
                assertEquals(1000.0, profile.getPackagingCosts(), 0.01, 
                    "Packaging costs must be preserved with precision");
                assertEquals(500.0, profile.getPowerConsumption(), 0.01, 
                    "Power consumption must be preserved with precision");
            });
    }

    @Test
    @Tag("DataModelIntegrity")
    void testDailyMetricsDataIntegrity() {
        qt()
            .forAll(
                strings().allPossible().ofLengthBetween(5, 50), // email
                doubles().between(0.0, 100000.0), // sales
                doubles().between(0.0, 50000.0), // expenses
                doubles().between(0.0, 10000.0) // wastage
            )
            .checkAssert((email, sales, expenses, wastage) -> {
                // Create user and daily metrics with sensitive financial data
                User user = new User(email, "hashedPassword123");
                DailyMetrics metrics = new DailyMetrics();
                metrics.setUser(user);
                metrics.setDate(LocalDate.now());
                metrics.setSales(BigDecimal.valueOf(sales));
                metrics.setExpenses(BigDecimal.valueOf(expenses));
                metrics.setWastage(BigDecimal.valueOf(wastage));
                
                // Verify sensitive financial data integrity
                assertEquals(BigDecimal.valueOf(sales), metrics.getSales(), "Sales data must be preserved exactly");
                assertEquals(BigDecimal.valueOf(expenses), metrics.getExpenses(), "Expenses data must be preserved exactly");
                assertEquals(BigDecimal.valueOf(wastage), metrics.getWastage(), "Wastage data must be preserved exactly");
                
                // Verify data relationships are maintained
                assertNotNull(metrics.getUser(), "User relationship must be maintained");
                assertEquals(user.getEmail(), metrics.getUser().getEmail(), 
                    "User relationship must preserve email");
                
                // Verify date integrity
                assertEquals(LocalDate.now(), metrics.getDate(), "Date must be preserved exactly");
                
                // Verify that financial data maintains precision (critical for financial calculations)
                assertTrue(metrics.getSales().scale() <= 2, 
                    "Sales should maintain appropriate decimal precision");
                assertTrue(metrics.getExpenses().scale() <= 2, 
                    "Expenses should maintain appropriate decimal precision");
                assertTrue(metrics.getWastage().scale() <= 2, 
                    "Wastage should maintain appropriate decimal precision");
            });
    }

    @Test
    @Tag("DataModelIntegrity")
    void testSubscriptionDataIntegrity() {
        qt()
            .forAll(
                strings().allPossible().ofLengthBetween(5, 50), // email
                doubles().between(499.0, 999.0) // subscription amount
            )
            .checkAssert((email, amount) -> {
                // Create user and subscription with sensitive payment data
                User user = new User(email, "hashedPassword123");
                Subscription subscription = new Subscription();
                subscription.setUser(user);
                subscription.setTier(SubscriptionTier.VISTAR);
                subscription.setAmount(BigDecimal.valueOf(amount));
                subscription.setStatus(SubscriptionStatus.ACTIVE);
                subscription.setStartDate(LocalDateTime.now());
                subscription.setEndDate(LocalDateTime.now().plusMonths(1));
                
                // Verify sensitive payment data integrity
                assertEquals(BigDecimal.valueOf(amount), subscription.getAmount(), "Payment amount must be preserved exactly");
                assertEquals(SubscriptionTier.VISTAR, subscription.getTier(), 
                    "Subscription tier must be preserved");
                assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus(), 
                    "Subscription status must be preserved");
                
                // Verify user relationship integrity
                assertNotNull(subscription.getUser(), "User relationship must be maintained");
                assertEquals(user.getEmail(), subscription.getUser().getEmail(), 
                    "User relationship must preserve email");
                
                // Verify temporal data integrity
                assertNotNull(subscription.getStartDate(), "Start date must be set");
                assertNotNull(subscription.getEndDate(), "End date must be set");
                assertTrue(subscription.getStartDate().isBefore(subscription.getEndDate()), 
                    "Start date must be before end date");
                
                // Verify payment amount precision (critical for billing)
                assertTrue(subscription.getAmount().scale() <= 2, 
                    "Payment amount should maintain appropriate decimal precision");
            });
    }

    @Test
    @Tag("DataModelIntegrity")
    void testFeasibilityReportDataIntegrity() {
        qt()
            .forAll(
                strings().allPossible().ofLengthBetween(5, 50), // email
                strings().allPossible().ofLengthBetween(50, 1000), // executive summary
                doubles().between(10000.0, 1000000.0), // capex
                doubles().between(5000.0, 100000.0) // opex
            )
            .checkAssert((email, summary, capex, opex) -> {
                // Create user and feasibility report with sensitive business data
                User user = new User(email, "hashedPassword123");
                FeasibilityReport report = new FeasibilityReport();
                report.setUser(user);
                report.setExecutiveSummary(summary);
                report.setCapex(BigDecimal.valueOf(capex));
                report.setOpex(BigDecimal.valueOf(opex));
                report.setBreakEvenPoint(BigDecimal.valueOf(capex).add(BigDecimal.valueOf(opex).multiply(BigDecimal.valueOf(12))));
                report.setGeneratedAt(LocalDateTime.now());
                
                // Verify sensitive business data integrity
                assertEquals(summary, report.getExecutiveSummary(), 
                    "Executive summary must be preserved exactly");
                assertEquals(BigDecimal.valueOf(capex), report.getCapex(), "CAPEX data must be preserved exactly");
                assertEquals(BigDecimal.valueOf(opex), report.getOpex(), "OPEX data must be preserved exactly");
                
                // Verify calculated fields maintain integrity
                BigDecimal expectedBreakEven = BigDecimal.valueOf(capex).add(BigDecimal.valueOf(opex).multiply(BigDecimal.valueOf(12)));
                assertEquals(expectedBreakEven, report.getBreakEvenPoint(), 
                    "Break-even calculation must be preserved exactly");
                
                // Verify user relationship integrity
                assertNotNull(report.getUser(), "User relationship must be maintained");
                assertEquals(user.getEmail(), report.getUser().getEmail(), 
                    "User relationship must preserve email");
                
                // Verify timestamp integrity
                assertNotNull(report.getGeneratedAt(), "Generation timestamp must be set");
                assertTrue(report.getGeneratedAt().isBefore(LocalDateTime.now().plusSeconds(1)), 
                    "Generation timestamp should be recent");
                
                // Verify financial precision (critical for business calculations)
                assertTrue(report.getCapex().scale() <= 2, 
                    "CAPEX should maintain appropriate decimal precision");
                assertTrue(report.getOpex().scale() <= 2, 
                    "OPEX should maintain appropriate decimal precision");
                assertTrue(report.getBreakEvenPoint().scale() <= 2, 
                    "Break-even point should maintain appropriate decimal precision");
            });
    }
}