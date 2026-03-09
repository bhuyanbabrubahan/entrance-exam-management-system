package com.jee.publicapi.location.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jee.publicapi.location.dto.LocationDTO;
import com.jee.publicapi.location.service.LocationService;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // ===== COUNTRIES =====
    @GetMapping("/countries")
    public ResponseEntity<List<LocationDTO>> getCountries() {
        try {
            return ResponseEntity.ok(locationService.getCountries());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ===== STATES =====
    @GetMapping("/states/{countryId}")
    public ResponseEntity<List<LocationDTO>> getStates(@PathVariable Long countryId) {
        try {
            return ResponseEntity.ok(locationService.getStates(countryId));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ===== DISTRICTS =====
    @GetMapping("/districts/{stateId}")
    public ResponseEntity<List<LocationDTO>> getDistricts(@PathVariable Long stateId) {
        try {
            return ResponseEntity.ok(locationService.getDistricts(stateId));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ===== CITIES =====
    @GetMapping("/cities/{districtId}")
    public ResponseEntity<List<LocationDTO>> getCities(@PathVariable Long districtId) {
        try {
            return ResponseEntity.ok(locationService.getCities(districtId));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ===== PINCODES =====
    @GetMapping("/pincodes/{cityId}")
    public ResponseEntity<List<LocationDTO>> getPincodes(@PathVariable Long cityId) {
        try {
            return ResponseEntity.ok(locationService.getPincodes(cityId));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ===== SEARCH BY PINCODE =====
    @GetMapping("/search/{pincode}")
    public ResponseEntity<LocationDTO> searchByPincode(@PathVariable String pincode) {
        LocationDTO dto = locationService.getByPincode(pincode);
        if (dto != null) return ResponseEntity.ok(dto);
        else return ResponseEntity.notFound().build();
    }
}