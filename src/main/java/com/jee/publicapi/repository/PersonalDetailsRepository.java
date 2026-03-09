package com.jee.publicapi.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jee.publicapi.entity.PersonalDetails;
import com.jee.publicapi.entity.User;

import jakarta.transaction.Transactional;

import java.util.Optional;

public interface PersonalDetailsRepository extends JpaRepository<PersonalDetails, Long> {
	
    Optional<PersonalDetails> findByUser(User user);
    Optional<PersonalDetails> findByUserId(Long userId);
    PersonalDetails findByApplicationNumber(String applicationNumber);
    
    
    
    // ⭐ AUTO LOCK / UNLOCK ALL USERS
   
    @Modifying
    @Transactional
    @Query("""
    UPDATE PersonalDetails p
    SET p.reopenAllowed = :flag
    """)
    void updateReopenAllowed(boolean flag);
    
    @Query("""
    		   SELECT p FROM PersonalDetails p
    		   LEFT JOIN FETCH p.country
    		   LEFT JOIN FETCH p.state
    		   LEFT JOIN FETCH p.district
    		   LEFT JOIN FETCH p.city
    		   LEFT JOIN FETCH p.pincode
    		   WHERE p.user.id = :userId
    		""")
    		Optional<PersonalDetails> findByUserIdWithLocation(Long userId);
}
