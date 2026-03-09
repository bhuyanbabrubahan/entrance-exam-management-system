package com.jee.publicapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jee.publicapi.entity.DocumentDetails;
import com.jee.publicapi.entity.User;

import jakarta.transaction.Transactional;

public interface DocumentDetailsRepository
        extends JpaRepository<DocumentDetails, Long> {

    Optional<DocumentDetails> findByUser(User user);
    Optional<DocumentDetails> findByUserId(Long userId);
    
    @Modifying
    @Transactional
    @Query("""
        UPDATE DocumentDetails d
        SET d.reopenAllowed = :flag
    """)
    void updateReopenAllowed(boolean flag);
}
