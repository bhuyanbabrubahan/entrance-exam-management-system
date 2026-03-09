package com.jee.publicapi.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jee.publicapi.location.entity.City;
import com.jee.publicapi.location.entity.District;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

	List<City> findByDistrictId(Long districtId);
	Optional<City>
    findByNameIgnoreCaseAndDistrict(
        String name,
        District district
    );

    
}