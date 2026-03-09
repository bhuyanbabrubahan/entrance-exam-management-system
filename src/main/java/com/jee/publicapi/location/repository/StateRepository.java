package com.jee.publicapi.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jee.publicapi.location.entity.State;

import java.util.List;
import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Long> {
    List<State> findByCountryId(Long countryId);
    Optional<State> findByNameIgnoreCase(String name);
}