package com.jee.publicapi.correction.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jee.publicapi.correction.entity.UserCorrectionRequest;
import com.jee.publicapi.correction.enums.CorrectionStatus;
import com.jee.publicapi.entity.User;

import java.util.List;

/* =========================================================
   USER CORRECTION REQUEST REPOSITORY
   Handles:
   - User correction requests
   - Admin search & filter
   - Dashboard counts
   - Field history tracking
   ========================================================= */

public interface UserCorrectionRequestRepository
        extends JpaRepository<UserCorrectionRequest, Long> {

    /* =====================================================
       USER SIDE METHODS
       ===================================================== */

    // Get all correction requests of a user
    List<UserCorrectionRequest> findByUserIdOrderByRequestedAtDesc(Long userId);

    // Check if correction already exists (not approved/rejected)
    boolean existsByUserIdAndFieldNameAndStatusNot(
            Long userId,
            String fieldName,
            CorrectionStatus status);

    // Count how many times a field was corrected
    long countByUserIdAndFieldName(Long userId, String fieldName);

    // Dashboard count
    long countByUserId(Long userId);

    // Latest correction request (for dashboard status display)
    UserCorrectionRequest findTopByUserIdOrderByRequestedAtDesc(Long userId);


    /* =====================================================
       ADMIN FILTER METHODS
       ===================================================== */

    // Filter by status
    Page<UserCorrectionRequest> findByStatus(
            CorrectionStatus status,
            Pageable pageable);


    /* =====================================================
       ADMIN SEARCH + FILTER (Single Optimized Query)
       ===================================================== */

    @Query("""
    	    SELECT r FROM UserCorrectionRequest r
    	    WHERE (:status IS NULL OR r.status = :status)
    	    AND (
    	        :search IS NULL OR
    	        LOWER(r.applicationNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
    	        LOWER(r.user.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
    	    )
    	""")
    	Page<UserCorrectionRequest> searchRequests(
    	        @Param("status") CorrectionStatus status,
    	        @Param("search") String search,
    	        Pageable pageable
    	);


    /* =====================================================
       FETCH USER WITH DOCUMENTS
       ===================================================== */

    @Query("""
           SELECT r FROM UserCorrectionRequest r
           LEFT JOIN FETCH r.documents
           WHERE r.user.id = :userId
           ORDER BY r.requestedAt DESC
           """)
    List<UserCorrectionRequest> findByUserIdWithDocuments(
            @Param("userId") Long userId);


    /* =====================================================
       FIELD HISTORY (Admin/User View)
       ===================================================== */

    @Query("""
           SELECT r FROM UserCorrectionRequest r
           WHERE r.user.id = :userId
           AND LOWER(r.fieldName) = LOWER(:fieldName)
           ORDER BY r.requestedAt DESC
           """)
    List<UserCorrectionRequest> findFieldHistory(
            @Param("userId") Long userId,
            @Param("fieldName") String fieldName);
    
    
    // Total requests
    long count();

    // Count DISTINCT users from user_correction_requests table
    @Query("SELECT COUNT(DISTINCT ucr.user.id) FROM UserCorrectionRequest ucr")
    long countDistinctUsers();
    
    
    boolean existsByUserAndFieldNameAndStatus(
            User user,
            String fieldName,
            CorrectionStatus status
    );
 
    
}