package com.arthsethu.service;

import com.arthsethu.dto.CommodityPrices;
import com.arthsethu.dto.LocationData;
import com.arthsethu.model.BusinessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for integrating with Government of India data APIs
 * Implements Requirements 2.1, 2.2, 2.3, 2.4, 2.5
 */
@Service
public class GovernmentDataService implements GovernmentDataInterface {
    
    private static final Logger logger = LoggerFactory.getLogger(GovernmentDataService.class);
    
    private final RestTemplate restTemplate;
    
    @Value("${arthsethu.gov-api.base-url:https://api.data.gov.in}")
    private String governmentDataBaseUrl;
    
    @Value("${arthsethu.gov-api.api-key:}")
    private String apiKey;
    
    // Fallback data for when government APIs are unavailable
    private static final Map<String, LocationData> FALLBACK_DATA = new HashMap<>();
    
    static {
        // Initialize fallback data for major cities
        FALLBACK_DATA.put("Mumbai", new LocationData("Mumbai", "Maharashtra", 
            new BigDecimal("150.00"), new BigDecimal("25000.00"), 
            new CommodityPrices(new BigDecimal("60.00"), new BigDecimal("50.00"), 
                new BigDecimal("200.00"), new BigDecimal("8.50"), new BigDecimal("105.00"))));
        
        FALLBACK_DATA.put("Delhi", new LocationData("Delhi", "Delhi", 
            new BigDecimal("120.00"), new BigDecimal("22000.00"), 
            new CommodityPrices(new BigDecimal("55.00"), new BigDecimal("48.00"), 
                new BigDecimal("180.00"), new BigDecimal("7.50"), new BigDecimal("103.00"))));
        
        FALLBACK_DATA.put("Bangalore", new LocationData("Bangalore", "Karnataka", 
            new BigDecimal("100.00"), new BigDecimal("20000.00"), 
            new CommodityPrices(new BigDecimal("50.00"), new BigDecimal("45.00"), 
                new BigDecimal("170.00"), new BigDecimal("6.50"), new BigDecimal("100.00"))));
        
        FALLBACK_DATA.put("Chennai", new LocationData("Chennai", "Tamil Nadu", 
            new BigDecimal("90.00"), new BigDecimal("18000.00"), 
            new CommodityPrices(new BigDecimal("48.00"), new BigDecimal("47.00"), 
                new BigDecimal("160.00"), new BigDecimal("6.00"), new BigDecimal("98.00"))));
        
        FALLBACK_DATA.put("Pune", new LocationData("Pune", "Maharashtra", 
            new BigDecimal("80.00"), new BigDecimal("19000.00"), 
            new CommodityPrices(new BigDecimal("52.00"), new BigDecimal("49.00"), 
                new BigDecimal("175.00"), new BigDecimal("7.00"), new BigDecimal("102.00"))));
    }
    
    public GovernmentDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Fetch location-specific data from government APIs with caching
     * Implements Requirements 2.1, 2.2, 2.3, 2.4, 2.5
     */
    @Cacheable(value = "locationData", key = "#city + '_' + #businessType")
    @Retryable(retryFor = {RestClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public LocationData fetchLocationData(String city, BusinessType businessType) {
        logger.info("Fetching government data for city: {} and business type: {}", city, businessType);
        
        try {
            // Try to fetch real government data
            LocationData realData = fetchRealGovernmentData(city, businessType);
            if (realData != null) {
                logger.info("Successfully fetched real government data for {}", city);
                return realData;
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch real government data for city: {}, falling back to cached data. Error: {}", 
                       city, e.getMessage());
        }
        
        // Fallback to cached/default data
        LocationData fallbackData = FALLBACK_DATA.get(city);
        if (fallbackData != null) {
            logger.info("Using fallback data for city: {}", city);
            return fallbackData;
        }
        
        // If no fallback data exists, create default data
        logger.warn("No fallback data available for city: {}, using default values", city);
        return createDefaultLocationData(city, businessType);
    }
    
    /**
     * Fetch commercial rent data from Ministry of Consumer Affairs
     * Implements Requirement 2.3
     */
    @Retryable(retryFor = {RestClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public BigDecimal fetchCommercialRentData(String city) {
        logger.debug("Fetching commercial rent data for city: {}", city);
        
        try {
            String url = governmentDataBaseUrl + "/resource/commercial-rent/" + city;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = response.getBody();
                Object rentValue = data.get("average_rent_per_sqft");
                if (rentValue != null) {
                    return new BigDecimal(rentValue.toString());
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching commercial rent data for {}: {}", city, e.getMessage());
        }
        
        return null; // Will trigger fallback in calling method
    }
    
    /**
     * Fetch wage data from Labor Ministry
     * Implements Requirement 2.4
     */
    @Retryable(retryFor = {RestClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public BigDecimal fetchWageData(String city) {
        logger.debug("Fetching wage data for city: {}", city);
        
        try {
            String url = governmentDataBaseUrl + "/resource/minimum-wages/" + city;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = response.getBody();
                Object wageValue = data.get("average_monthly_wage");
                if (wageValue != null) {
                    return new BigDecimal(wageValue.toString());
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching wage data for {}: {}", city, e.getMessage());
        }
        
        return null; // Will trigger fallback in calling method
    }
    
    /**
     * Fetch commodity prices from government data
     * Implements Requirement 2.5
     */
    @Retryable(retryFor = {RestClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public CommodityPrices fetchCommodityPrices(String city, BusinessType businessType) {
        logger.debug("Fetching commodity prices for city: {} and business type: {}", city, businessType);
        
        try {
            String url = governmentDataBaseUrl + "/resource/commodity-prices/" + city;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = response.getBody();
                return mapToCommodityPrices(data, businessType);
            }
        } catch (Exception e) {
            logger.error("Error fetching commodity prices for {}: {}", city, e.getMessage());
        }
        
        return null; // Will trigger fallback in calling method
    }
    
    /**
     * Attempt to fetch real government data by combining all API calls
     */
    private LocationData fetchRealGovernmentData(String city, BusinessType businessType) {
        try {
            BigDecimal rent = fetchCommercialRentData(city);
            BigDecimal wage = fetchWageData(city);
            CommodityPrices commodityPrices = fetchCommodityPrices(city, businessType);
            
            if (rent != null && wage != null && commodityPrices != null) {
                return new LocationData(city, getStateForCity(city), rent, wage, commodityPrices);
            }
        } catch (Exception e) {
            logger.error("Error fetching complete government data for {}: {}", city, e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Map government API response to CommodityPrices object
     */
    private CommodityPrices mapToCommodityPrices(Map<String, Object> data, BusinessType businessType) {
        CommodityPrices prices = new CommodityPrices();
        
        // Extract relevant commodity prices based on business type
        if (data.get("milk_price") != null) {
            prices.setMilkPricePerLiter(new BigDecimal(data.get("milk_price").toString()));
        }
        if (data.get("steel_price") != null) {
            prices.setSteelPricePerKg(new BigDecimal(data.get("steel_price").toString()));
        }
        if (data.get("fabric_price") != null) {
            prices.setFabricPricePerMeter(new BigDecimal(data.get("fabric_price").toString()));
        }
        if (data.get("electricity_price") != null) {
            prices.setElectricityPricePerUnit(new BigDecimal(data.get("electricity_price").toString()));
        }
        if (data.get("fuel_price") != null) {
            prices.setFuelPricePerLiter(new BigDecimal(data.get("fuel_price").toString()));
        }
        
        return prices;
    }
    
    /**
     * Create default location data when no government data is available
     */
    private LocationData createDefaultLocationData(String city, BusinessType businessType) {
        // Create conservative default values
        BigDecimal defaultRent = new BigDecimal("75.00");
        BigDecimal defaultWage = new BigDecimal("15000.00");
        CommodityPrices defaultPrices = new CommodityPrices(
            new BigDecimal("45.00"), // milk
            new BigDecimal("40.00"), // steel
            new BigDecimal("150.00"), // fabric
            new BigDecimal("5.50"), // electricity
            new BigDecimal("95.00") // fuel
        );
        
        return new LocationData(city, "Unknown", defaultRent, defaultWage, defaultPrices);
    }
    
    /**
     * Simple mapping of cities to states (can be enhanced with a proper database)
     */
    private String getStateForCity(String city) {
        Map<String, String> cityStateMap = Map.of(
            "Mumbai", "Maharashtra",
            "Delhi", "Delhi",
            "Bangalore", "Karnataka",
            "Chennai", "Tamil Nadu",
            "Pune", "Maharashtra",
            "Hyderabad", "Telangana",
            "Kolkata", "West Bengal",
            "Ahmedabad", "Gujarat"
        );
        
        return cityStateMap.getOrDefault(city, "Unknown");
    }
    
    /**
     * Check if government data APIs are available
     */
    @Override
    public boolean isGovernmentDataAvailable() {
        try {
            String healthUrl = governmentDataBaseUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.warn("Government data APIs are not available: {}", e.getMessage());
            return false;
        }
    }
}