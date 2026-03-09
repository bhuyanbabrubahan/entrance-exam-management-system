package com.jee.publicapi.location.service;

import java.util.List;

import com.jee.publicapi.location.dto.LocationDTO;

public interface LocationService {

	 List<LocationDTO> getCountries();
	    List<LocationDTO> getStates(Long countryId);
	    List<LocationDTO> getDistricts(Long stateId);
	    List<LocationDTO> getCities(Long districtId);
	    List<LocationDTO> getPincodes(Long cityId);
	    // Only if needed
	    LocationDTO getByPincode(String pincode);
}