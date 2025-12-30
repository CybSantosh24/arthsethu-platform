package com.arthsethu.repository;

import com.arthsethu.model.FeasibilityReport;
import com.arthsethu.model.BusinessType;
import com.arthsethu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeasibilityReportRepository extends JpaRepository<FeasibilityReport, Long> {
    
    /**
     * Find reports by user
     */
    List<FeasibilityReport> findByUser(User user);
    
    /**
     * Find reports by user ID
     */
    List<FeasibilityReport> findByUserId(Long userId);
    
    /**
     * Find latest report for a user
     */
    Optional<FeasibilityReport> findTopByUserOrderByGeneratedAtDesc(User user);
    
    /**
     * Find reports by business type
     */
    List<FeasibilityReport> findByBusinessType(BusinessType businessType);
    
    /**
     * Find reports by city
     */
    List<FeasibilityReport> findByCity(String city);
    
    /**
     * Find reports by business type and city
     */
    List<FeasibilityReport> findByBusinessTypeAndCity(BusinessType businessType, String city);
    
    /**
     * Find reports generated after a specific date
     */
    List<FeasibilityReport> findByGeneratedAtAfter(LocalDateTime date);
    
    /**
     * Find reports with PDF content available
     */
    @Query("SELECT fr FROM FeasibilityReport fr WHERE fr.pdfContent IS NOT NULL")
    List<FeasibilityReport> findReportsWithPdf();
    
    /**
     * Find reports without PDF content (for batch processing)
     */
    @Query("SELECT fr FROM FeasibilityReport fr WHERE fr.pdfContent IS NULL")
    List<FeasibilityReport> findReportsWithoutPdf();
    
    /**
     * Count reports by business type
     */
    long countByBusinessType(BusinessType businessType);
    
    /**
     * Count reports by city
     */
    long countByCity(String city);
    
    /**
     * Get average CAPEX by business type and city
     */
    @Query("SELECT AVG(fr.capex) FROM FeasibilityReport fr WHERE fr.businessType = :businessType AND fr.city = :city")
    Double getAverageCapexByBusinessTypeAndCity(@Param("businessType") BusinessType businessType, @Param("city") String city);
    
    /**
     * Get average OPEX by business type and city
     */
    @Query("SELECT AVG(fr.opex) FROM FeasibilityReport fr WHERE fr.businessType = :businessType AND fr.city = :city")
    Double getAverageOpexByBusinessTypeAndCity(@Param("businessType") BusinessType businessType, @Param("city") String city);
    
    /**
     * Get average break-even point by business type
     */
    @Query("SELECT AVG(fr.breakEvenPoint) FROM FeasibilityReport fr WHERE fr.businessType = :businessType")
    Double getAverageBreakEvenPointByBusinessType(@Param("businessType") BusinessType businessType);
    
    /**
     * Find reports generated within date range
     */
    List<FeasibilityReport> findByGeneratedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Check if user has any feasibility reports
     */
    boolean existsByUser(User user);
    
    /**
     * Get distinct cities from reports (for analytics)
     */
    @Query("SELECT DISTINCT fr.city FROM FeasibilityReport fr WHERE fr.city IS NOT NULL ORDER BY fr.city")
    List<String> findDistinctCities();
    
    /**
     * Find reports by user with government data snapshot
     */
    @Query("SELECT fr FROM FeasibilityReport fr WHERE fr.user = :user AND fr.governmentDataSnapshot IS NOT NULL")
    List<FeasibilityReport> findByUserWithGovernmentData(@Param("user") User user);
}