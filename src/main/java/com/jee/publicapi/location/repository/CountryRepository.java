package com.jee.publicapi.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jee.publicapi.location.entity.Country;

// Country
public interface CountryRepository extends JpaRepository<Country, Long> {}

