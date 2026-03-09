package com.jee.publicapi.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jee.publicapi.dto.AdminCandidateDTO;
import com.jee.publicapi.dto.LoginInfoDto;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.entity.UserLoginLog;
import com.jee.publicapi.enums.FormStatus;

import jakarta.servlet.http.HttpServletRequest;

/**
 * User service abstraction
 * Handles authentication-related logic
 */
public interface UserService {

    /**
     * Find user by application number OR email (login support)
     */
	Optional<User> findById(Long id);  // ✅ add this
    Optional<User> findByApplicationNumberOrEmail(String value);

    /**
     * Save user (password encoding handled internally)
     */
    User save(User user);

    /**
     * Duplicate checks
     */
    boolean existsByEmail(String email);
    // ✅ ADD THIS
    User findByEmail(String email);
    boolean existsByMobile(String mobile);
    
    void updateLoginInfo(String email, HttpServletRequest request);

    LoginInfoDto getLoginInfoByEmail(String email);

    /**
     * Admin controlled account status
     */
    void blockUser(Long userId);
    void allowUser(Long userId);

    /**
     * Password verification using BCrypt
     */
    boolean matchPassword(String rawPassword, String encodedPassword);
    
    void increaseFailedAttempts(User user);

    void resetFailedAttempts(User user);

    boolean isAccountLocked(User user);

    void unlockAccount(User user);

    void unlockAccountByAdmin(Long userId);
    
 // 🔹 Admin candidate pagination
    Page<AdminCandidateDTO> getCandidates(Pageable pageable, FormStatus status, String search);
    

    // 🔹 Admin dashboard status counts
    Map<String, Long> getStatusCounts();

    // 🔹 JWT user fetch
    User getUserFromToken(String authHeader);
    
	

}
