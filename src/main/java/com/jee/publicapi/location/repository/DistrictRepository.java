package com.jee.publicapi.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jee.publicapi.location.entity.District;
import com.jee.publicapi.location.entity.State;

public interface DistrictRepository extends JpaRepository<District, Long> {
	 // ✅ Find district by name (case-insensitive)
    Optional<District> findByNameIgnoreCase(String name);

    // Optional: find all districts by state
    List<District> findByStateId(Long stateId);
    Optional<District>findByNameIgnoreCaseAndState(String name, State state);
    
    
}
