package com.arthsethu.service;

import com.arthsethu.dto.LocationData;
import com.arthsethu.model.BusinessType;

/**
 * Interface for government data integration
 */
public interface GovernmentDataInterface {
    LocationData fetchLocationData(String city, BusinessType businessType);
    boolean isGovernmentDataAvailable();
}