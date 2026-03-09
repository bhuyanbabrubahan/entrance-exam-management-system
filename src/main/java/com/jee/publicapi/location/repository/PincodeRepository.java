package com.jee.publicapi.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jee.publicapi.location.entity.City;
import com.jee.publicapi.location.entity.Pincode;

public interface PincodeRepository extends JpaRepository<Pincode, Long> {

    List<Pincode> findByCityId(Long cityId);

    Optional<Pincode> findByCode(String code);
 // ✅ NEW: Safe validation method
    Optional<Pincode> findByIdAndCityId(Long id, Long cityId);
    
}