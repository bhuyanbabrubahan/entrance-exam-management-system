package com.jee.publicapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jee.publicapi.entity.EducationDetails;
import com.jee.publicapi.entity.User;

import jakarta.transaction.Transactional;

public interface EducationDetailsRepository
        extends JpaRepository<EducationDetails, Long> {

    Optional<EducationDetails> findByUser(User user);
    Optional<EducationDetails> findByUserId(Long userId);
    //EducationDetails findByUser_ApplicationNumber(Integer applicationNumber);
    
    @Modifying
    @Transactional
    @Query("""
    UPDATE EducationDetails e
    SET e.reopenAllowed = :flag
    """)
    void updateReopenAllowed(boolean flag);
    
    
}
