package com.jee.publicapi.correction.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jee.publicapi.basicwindow.service.BasicWindowService;
import com.jee.publicapi.correction.dto.AdminCorrectionResponseDTO;
import com.jee.publicapi.correction.dto.CorrectionDashboardDTO;
import com.jee.publicapi.correction.dto.CorrectionHistoryDTO;
import com.jee.publicapi.correction.entity.UserCorrectionDocument;
import com.jee.publicapi.correction.entity.UserCorrectionRequest;
import com.jee.publicapi.correction.service.CorrectionDashboardService;
import com.jee.publicapi.correction.service.UserCorrectionRequestService;
import com.jee.publicapi.dto.LoginInfoDto;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.repository.UserRepository;
import com.jee.publicapi.service.UserService;

@RestController
@RequestMapping("/api/user/user-correction-request")
@CrossOrigin(origins = "http://localhost:5173")
public class UserRequestCorrectionController {

    private final UserCorrectionRequestService correctionService;
    private final BasicWindowService basicWindowService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CorrectionDashboardService dashboardService;

    public UserRequestCorrectionController(
            UserCorrectionRequestService correctionService,
            BasicWindowService basicWindowService,
            UserRepository userRepository,
            UserService userService,
            CorrectionDashboardService dashboardService) {

        this.correctionService = correctionService;
        this.basicWindowService = basicWindowService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.dashboardService = dashboardService;
    }

    /* =====================================================
       GET CURRENT USER INFO
    ===================================================== */

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {

        System.out.println("🔥 USER CORRECTION API HIT");

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("status", "UNAUTHORIZED"));
        }

        String email = authentication.getName();

        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("status", "NOT_FOUND"));
        }

        LoginInfoDto loginInfo = userService.getLoginInfoByEmail(email);

        String ipAddress =
                loginInfo != null ? loginInfo.getIpAddress() : "—";

        String loginTime =
                loginInfo != null && loginInfo.getLoginTime() != null
                        ? loginInfo.getLoginTime().toString()
                        : LocalDateTime.now().toString();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", user.getFirstName());
        userMap.put("middleName", user.getMiddleName());
        userMap.put("lastName", user.getLastName());
        userMap.put("email", user.getEmail());
        userMap.put("mobileNumber", user.getMobileNumber());
        userMap.put("applicationNumber", user.getApplicationNumber());

        Map<String, Object> loginInfoMap = new HashMap<>();
        loginInfoMap.put("ipAddress", ipAddress);
        loginInfoMap.put("loginTime", loginTime);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("user", userMap);
        response.put("loginInfo", loginInfoMap);

        return ResponseEntity.ok(response);
    }

    /* =====================================================
       SUBMIT CORRECTION
    ===================================================== */

    @PostMapping(value = "/request", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitCorrection(
            Principal principal,
            @RequestParam String fieldName,
            @RequestParam String requestedValue,
            @RequestParam String reason,
            @RequestParam(required = false) List<MultipartFile> documents) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "UNAUTHORIZED"));
        }

        String email = principal.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {

            UserCorrectionRequest request =
                    correctionService.submitCorrection(
                            user.getId(),
                            fieldName,
                            requestedValue,
                            reason,
                            documents);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Correction request submitted successfully",
                    "requestId", request.getId()
            ));

        } catch (RuntimeException ex) {

            return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", ex.getMessage()
            ));
        }
    }

    /* =====================================================
       USER HISTORY
    ===================================================== */

    @GetMapping("/history")
    public ResponseEntity<?> getCorrectionHistory(Principal principal) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserCorrectionRequest> history =
                correctionService.getUserHistory(user.getId());

        List<Map<String, Object>> responseList = history.stream().map(req -> {

            Map<String, Object> map = new HashMap<>();

            map.put("id", req.getId());
            map.put("fieldName", req.getFieldName());
            map.put("oldValue", req.getOldValue());
            map.put("requestedValue", req.getRequestedValue());
            map.put("status", req.getStatus());
            map.put("requestedAt", req.getRequestedAt());
            map.put("reviewedAt", req.getReviewedAt());
            map.put("adminRemark", req.getAdminRemark());
            map.put("attemptNumber", req.getAttemptNumber());
            map.put("reason", req.getReason());
            map.put("deActivatedByAdmin", req.getDeActivatedByAdmin());
            map.put("deActivatedAt", req.getDeActivatedAt());
            map.put("windowStart", req.getWindowStart());
            map.put("windowEnd", req.getWindowEnd());
            map.put("correctionCompleted",req.isCorrectionCompleted());
            map.put("userUpdatedAt",req.getUserUpdatedAt());
            
            List<String> docs =
                    req.getDocuments() != null
                            ? req.getDocuments()
                            .stream()
                            .map(UserCorrectionDocument::getFilePath)
                            .toList()
                            : Collections.emptyList();

            map.put("documentPaths", docs);

            return map;

        }).toList();

        return ResponseEntity.ok(responseList);
    }

    /* =====================================================
       ADMIN FIELD HISTORY
    ===================================================== */

    @GetMapping("/history/{userId}/{fieldName}")
    public List<AdminCorrectionResponseDTO> getHistory(
            @PathVariable Long userId,
            @PathVariable String fieldName) {

        List<UserCorrectionRequest> history =
                correctionService.getFieldHistory(userId, fieldName);

        return history.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private AdminCorrectionResponseDTO convertToDTO(UserCorrectionRequest r) {

        List<String> docs = r.getDocuments()
                .stream()
                .map(UserCorrectionDocument::getFilePath)
                .toList();

        AdminCorrectionResponseDTO dto = new AdminCorrectionResponseDTO();

        dto.setId(r.getId());
        dto.setUserId(r.getUser().getId());
        dto.setApplicationNumber(r.getApplicationNumber());
        dto.setUserName(r.getUser().getFirstName() + " " + r.getUser().getLastName());
        dto.setFieldName(r.getFieldName());
        dto.setOldValue(r.getOldValue());
        dto.setRequestedValue(r.getRequestedValue());
        dto.setReason(r.getReason());
        dto.setAttemptNumber(r.getAttemptNumber());
        dto.setStatus(r.getStatus().name());
        dto.setAdminRemark(r.getAdminRemark());
        dto.setRequestedAt(r.getRequestedAt());
        dto.setReviewedAt(r.getReviewedAt());
        dto.setDocumentPaths(docs);

        return dto;
    }

    /* =====================================================
       DASHBOARD STATUS
    ===================================================== */

    @GetMapping("/dashboard-status")
    public ResponseEntity<?> getDashboardStatus(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CorrectionDashboardDTO dto =
                dashboardService.getDashboardStatus(user.getId());

        return ResponseEntity.ok(dto);
    }

    /* =====================================================
       USER REQUESTS WITH WINDOW
    ===================================================== */

    @GetMapping("/my-requests")
    public ResponseEntity<List<CorrectionHistoryDTO>> getMyRequests(
            Principal principal) {

        System.out.println("====== API HIT : /my-requests ======");

        String email = principal.getName();

        List<CorrectionHistoryDTO> list =
                correctionService.getMyRequests(email);

        System.out.println("Total Records Returned: " + list.size());

        return ResponseEntity.ok(list);
    }

}