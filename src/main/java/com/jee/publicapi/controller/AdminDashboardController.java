package com.jee.publicapi.controller;

import com.jee.publicapi.dto.CandidateFullDetailsDTO;
import com.jee.publicapi.dto.AdminDashboardStatsDTO;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.repository.UserRepository;
import com.jee.publicapi.service.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;

    @Autowired
    private UserRepository userRepository;

    // ================= STATUS COUNTS =================
    @GetMapping("/candidates/status-counts")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Integer>> getStatusCounts() {
        Map<String, Integer> counts = new HashMap<>();

        // Personal Status
        List<Object[]> personalCounts = userRepository.countByPersonalStatus();
        for (Object[] row : personalCounts) {
            counts.put(row[0].toString(), ((Long) row[1]).intValue());
        }

        // Education Status
        List<Object[]> educationCounts = userRepository.countByEducationStatus();
        for (Object[] row : educationCounts) {
            counts.put(row[0].toString(), ((Long) row[1]).intValue());
        }

        // Document Status
        List<Object[]> documentCounts = userRepository.countByDocumentStatus();
        for (Object[] row : documentCounts) {
            counts.put(row[0].toString(), ((Long) row[1]).intValue());
        }

        // Payment Status
        List<Object[]> paymentCounts = userRepository.countByPaymentStatus();
        for (Object[] row : paymentCounts) {
            counts.put(row[0].toString(), ((Long) row[1]).intValue());
        }

        return ResponseEntity.ok(counts);
    }

    // ================= PAGINATED CANDIDATES =================
    @GetMapping("/candidates")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getCandidates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "applicationNumber,desc") String sort,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {
        Sort sortObj = Sort.by("applicationNumber").descending();
        if (sort != null && sort.contains(",")) {
            String[] parts = sort.split(",");
            sortObj = Sort.by(parts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, parts[0]);
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        // Convert status string to FormStatus safely
        FormStatus statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = FormStatus.valueOf(status);
            } catch (IllegalArgumentException ignored) {
            }
        }

        Page<User> pageResult = userRepository.findByStatusAndSearch(statusEnum, search, pageable);

        // Map User entity to frontend-friendly format
        List<Map<String, Object>> content = new ArrayList<>();
        for (User u : pageResult.getContent()) {
            Map<String, Object> m = new HashMap<>();
            m.put("userId", u.getId());
            m.put("fullName", u.getFirstName() + (u.getMiddleName() != null ? " " + u.getMiddleName() : "") + " " + (u.getLastName() != null ? u.getLastName() : ""));
            m.put("email", u.getEmail());
            m.put("mobileNumber", u.getMobileNumber());
            m.put("personalStatus", u.getPersonalStatus() != null ? u.getPersonalStatus().name() : "NOT_STARTED");
            m.put("educationStatus", u.getEducationStatus() != null ? u.getEducationStatus().name() : "NOT_STARTED");
            m.put("documentStatus", u.getDocumentStatus() != null ? u.getDocumentStatus().name() : "NOT_STARTED");
            m.put("paymentStatus", u.getPaymentStatus() != null ? u.getPaymentStatus().name() : "PENDING");
            content.add(m);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("totalPages", pageResult.getTotalPages());
        response.put("totalElements", pageResult.getTotalElements());
        response.put("pageNumber", pageResult.getNumber());

        return ResponseEntity.ok(response);
    }

    // ================= SINGLE CANDIDATE DETAILS =================
    @GetMapping("/dashboard/candidate/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CandidateFullDetailsDTO> getCandidateDetails(@PathVariable Long id) {
        try {
            CandidateFullDetailsDTO dto = dashboardService.getCandidateFullDetails(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    // ================= DASHBOARD STATS =================
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdminDashboardStatsDTO> getDashboardStats() {
        try {
            AdminDashboardStatsDTO stats = dashboardService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}