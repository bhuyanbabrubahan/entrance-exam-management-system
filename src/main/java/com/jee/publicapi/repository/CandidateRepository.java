package com.jee.publicapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jee.publicapi.entity.Candidate;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

	long countByStatus(String status);
    List<Candidate> findByStatus(String status);

    
}