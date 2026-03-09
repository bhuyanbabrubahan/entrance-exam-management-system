package com.jee.publicapi.serviceimpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jee.publicapi.dto.AdminCandidateDTO;
import com.jee.publicapi.dto.LoginInfoDto;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.repository.UserLoginLogRepository;
import com.jee.publicapi.repository.UserRepository;
import com.jee.publicapi.service.JwtService;
import com.jee.publicapi.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl implements UserService {


    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION_HOURS = 24;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /* ==================== EXISTING METHODS ==================== */
    @Override
    public Optional<User> findByApplicationNumberOrEmail(String value) {
        return userRepository.findByApplicationNumberOrEmail(value);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User save(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public boolean matchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public boolean isAccountLocked(User user) {
        if (user.isAccountNonLocked()) return false;

        if (user.getLockTime() == null) return true;

        LocalDateTime unlockTime = user.getLockTime().plusHours(LOCK_TIME_DURATION_HOURS);
        if (LocalDateTime.now().isAfter(unlockTime)) {
            unlockAccount(user);
            return false;
        }

        return true;
    }

    @Override
    public void increaseFailedAttempts(User user) {
        int attempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountNonLocked(false);
            user.setLockTime(LocalDateTime.now());
        }

        userRepository.save(user);
    }

    @Override
    public void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
    }

    @Override
    public void unlockAccount(User user) {
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
    }

    @Override
    public void updateLoginInfo(String email, HttpServletRequest request) {
        User user = findByEmail(email);
        if (user == null) return;

        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(getClientIp(request));
        user.setFailedAttempts(0);
        userRepository.save(user);
    }

    @Override
    public LoginInfoDto getLoginInfoByEmail(String email) {
        User user = findByEmail(email);
        if (user == null) return null;

        return new LoginInfoDto(user.getLastLoginTime(), user.getLastLoginIp());
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isBlank()) ? xff.split(",")[0] : request.getRemoteAddr();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByMobile(String mobile) {
        return userRepository.existsByMobileNumber(mobile);
    }

    @Override
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void allowUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void unlockAccountByAdmin(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        unlockAccount(user);
    }

    /* ==================== NEW METHOD: Get User from JWT ==================== */
    @Override
    public User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        // Remove "Bearer " prefix
        String token = authHeader.substring(7);

        // Extract email/username from JWT
        String email = jwtService.extractUsername(token);

        // Fetch user from DB
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate token using only JWT and user info (no UserDetailsService needed)
        if (!jwtService.isTokenValid(token,
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        user.isEnabled(),
                        true,
                        true,
                        true,
                        user.getAuthorities()
                ))) {
            throw new RuntimeException("Invalid or expired JWT");
        }

        return user;
    }

    /* ==================== PAGINATED & FILTERED CANDIDATES ==================== */
    @Override
    public Page<AdminCandidateDTO> getCandidates(Pageable pageable, FormStatus status, String search) {

        String searchFilter = (search != null && !search.isBlank())
                ? search.toLowerCase()
                : null;

        Page<User> users = userRepository.findByStatusAndSearch(status, searchFilter, pageable);

     // 🔹 DEBUG LOGGING
        System.out.println("Status Filter: " + status);
        System.out.println("Search Filter: " + searchFilter);
        System.out.println("Users fetched: " + users.getContent());
        return users.map(this::convertToAdminDTO);
    }

    // Example conversion method
    private AdminCandidateDTO convertToAdminDTO(User u) {
        AdminCandidateDTO dto = new AdminCandidateDTO();
        dto.setUserId(u.getId());
        dto.setApplicationNo(u.getApplicationNumber() != null ? u.getApplicationNumber().toString() : null);
        dto.setFullName(u.getFirstName() + " " + (u.getMiddleName() != null ? u.getMiddleName() + " " : "") + u.getLastName());
        dto.setEmail(u.getEmail());
        dto.setMobileNumber(u.getMobileNumber());
        dto.setEnabled(u.isEnabled());
        dto.setPersonalStatus(u.getPersonalStatus() != null ? u.getPersonalStatus().name() : null);
        dto.setEducationStatus(u.getEducationStatus() != null ? u.getEducationStatus().name() : null);
        dto.setDocumentStatus(u.getDocumentStatus() != null ? u.getDocumentStatus().name() : null);
        dto.setPaymentStatus(u.getPaymentStatus() != null ? u.getPaymentStatus().name() : null);
        return dto;
    }

    /* ==================== STATUS COUNTS ==================== */
    @Override
    public Map<String, Long> getStatusCounts() {

        Map<String, Long> counts = Arrays.stream(FormStatus.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        s -> 0L
                ));

        List<User> users = userRepository.findAll();

        users.forEach(u -> {
            Stream.of(
                    u.getPersonalStatus(),
                    u.getEducationStatus(),
                    u.getDocumentStatus(),
                    u.getPaymentStatus()
            )
            .filter(Objects::nonNull)
            .forEach(status ->
                    counts.put(status.name(), counts.get(status.name()) + 1)
            );
        });

        return counts;
    }

	@Override
	public Optional<User> findById(Long id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

    
	
	
}
