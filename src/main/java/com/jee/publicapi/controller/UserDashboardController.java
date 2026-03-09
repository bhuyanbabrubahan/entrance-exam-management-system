package com.jee.publicapi.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.jee.publicapi.dto.LoginInfoDto;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.service.UserService;

@RestController
@RequestMapping("/api/user/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class UserDashboardController {

    private final UserService userService;

    public UserDashboardController(UserService userService) {
        this.userService = userService;
    }

    /* =====================================================
       GET CURRENT LOGGED-IN USER (JWT)
       ===================================================== */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {

        System.out.println("🔥 DASHBOARD API HIT");

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(
                    Map.of("status", "UNAUTHORIZED", "message", "User not authenticated")
            );
        }

        String email = authentication.getName();
        System.out.println("✅ AUTHENTICATED USER = " + email);

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body(
                    Map.of("status", "NOT_FOUND", "message", "User not found")
            );
        }

        LoginInfoDto loginInfo = userService.getLoginInfoByEmail(email);

        String ipAddress =
                loginInfo != null ? loginInfo.getIpAddress() : "—";

        String loginTime =
                loginInfo != null && loginInfo.getLoginTime() != null
                        ? loginInfo.getLoginTime().toString()
                        : LocalDateTime.now().toString();

        /* ✅ NULL-SAFE RESPONSE (NO Map.of) */
        Map<String, Object> userMap = new java.util.HashMap<>();
        userMap.put("firstName", user.getFirstName());
        userMap.put("middleName", user.getMiddleName());
        userMap.put("lastName", user.getLastName());
        userMap.put("email", user.getEmail());
        userMap.put("mobileNumber", user.getMobileNumber());
        userMap.put("applicationNumber", user.getApplicationNumber());
        userMap.put("personalStatus", user.getPersonalStatus());
        userMap.put("educationStatus", user.getEducationStatus());
        userMap.put("documentStatus", user.getDocumentStatus());
        userMap.put("paymentStatus", user.getPaymentStatus());

        Map<String, Object> loginInfoMap = new java.util.HashMap<>();
        loginInfoMap.put("ipAddress", ipAddress);
        loginInfoMap.put("loginTime", loginTime);

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("status", "SUCCESS");
        response.put("user", userMap);
        response.put("loginInfo", loginInfoMap);

        return ResponseEntity.ok(response);
    }

}
