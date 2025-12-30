package com.arthsethu.repository;

import com.arthsethu.model.BusinessProfile;
import com.arthsethu.model.BusinessType;
import com.arthsethu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, Long> {
    
    /**
     * Find business profile by user
     */
    Optional<BusinessProfile> findByUser(User user);
    
    /**
     * Find business profile by user ID
     */
    Optional<BusinessProfile> findByUserId(Long userId);
    
    /**
     * Find profiles by business type
     */
    List<BusinessProfile> findByBusinessType(BusinessType businessType);
    
    /**
     * Find profiles by city
     */
    List<BusinessProfile> findByCity(String city);
    
    /**
     * Find profiles by business type and city (for market analysis)
     */
    List<BusinessProfile> findByBusinessTypeAndCity(BusinessType businessType, String city);
    
    /**
     * Count profiles by business type
     */
    long countByBusinessType(BusinessType businessType);
    
    /**
     * Count profiles by city
     */
    long countByCity(String city);
    
    /**
     * Find profiles with seating capacity (for Cafe analysis)
     */
    @Query("SELECT bp FROM BusinessProfile bp WHERE bp.businessType = :cafeType AND bp.seatingCapacity IS NOT NULL")
    List<BusinessProfile> findCafesWithSeatingCapacity(@Param("cafeType") BusinessType cafeType);
    
    /**
     * Find profiles with packaging costs (for Cloud Kitchen analysis)
     */
    @Query("SELECT bp FROM BusinessProfile bp WHERE bp.businessType = :cloudKitchenType AND bp.packagingCosts IS NOT NULL")
    List<BusinessProfile> findCloudKitchensWithPackagingCosts(@Param("cloudKitchenType") BusinessType cloudKitchenType);
    
    /**
     * Find profiles with power consumption (for Manufacturing analysis)
     */
    @Query("SELECT bp FROM BusinessProfile bp WHERE bp.businessType = :manufacturingType AND bp.powerConsumption IS NOT NULL")
    List<BusinessProfile> findManufacturingWithPowerConsumption(@Param("manufacturingType") BusinessType manufacturingType);
    
    /**
     * Get distinct cities for location-based analysis
     */
    @Query("SELECT DISTINCT bp.city FROM BusinessProfile bp ORDER BY bp.city")
    List<String> findDistinctCities();
    
    /**
     * Find profiles by business type with questionnaire responses
     */
    @Query("SELECT bp FROM BusinessProfile bp WHERE bp.businessType = :businessType AND bp.questionnaireResponses IS NOT NULL")
    List<BusinessProfile> findByBusinessTypeWithQuestionnaireResponses(@Param("businessType") BusinessType businessType);
}