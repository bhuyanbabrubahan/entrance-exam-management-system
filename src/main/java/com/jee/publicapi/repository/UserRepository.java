package com.jee.publicapi.repository;

import com.jee.publicapi.entity.User;
import com.jee.publicapi.enums.FormStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 🔹 Existing methods (keep them intact)
    Optional<User> findByApplicationNumber(Integer applicationNumber);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);

    default Optional<User> findByApplicationNumberOrEmail(String value) {
        try {
            return findByApplicationNumber(Integer.valueOf(value));
        } catch (NumberFormatException e) {
            return findByEmail(value);
        }
    }

    Page<User> findAll(Pageable pageable);

   

    // 🔹 Paginated & filtered candidates for React frontend
    @Query("SELECT u FROM User u " +
    	       "WHERE (:status IS NULL OR " +
    	       "(u.personalStatus = :status OR u.educationStatus = :status OR u.documentStatus = :status OR u.paymentStatus = :status)) " +
    	       "AND (:search IS NULL OR :search = '' OR " +
    	       "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
    	       "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
    	       "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))))")
    	Page<User> findByStatusAndSearch(@Param("status") FormStatus status,
    	                                @Param("search") String search,
    	                                Pageable pageable);

    // 🔹 Search only (status optional)
    @Query("SELECT u FROM User u " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.middleName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findBySearch(@Param("search") String search, Pageable pageable);

    

 // ======================= STATUS COUNTS =======================
    @Query("SELECT u.personalStatus AS status, COUNT(u) AS count FROM User u GROUP BY u.personalStatus")
    List<Object[]> countByPersonalStatus();

    @Query("SELECT u.educationStatus AS status, COUNT(u) AS count FROM User u GROUP BY u.educationStatus")
    List<Object[]> countByEducationStatus();

    @Query("SELECT u.documentStatus AS status, COUNT(u) AS count FROM User u GROUP BY u.documentStatus")
    List<Object[]> countByDocumentStatus();

    @Query("SELECT u.paymentStatus AS status, COUNT(u) AS count FROM User u GROUP BY u.paymentStatus")
    List<Object[]> countByPaymentStatus();
}