package com.jee.publicapi.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.jee.publicapi.entity.User;
import com.jee.publicapi.security.CustomUserDetailsService;
import com.jee.publicapi.service.JwtService;
import com.jee.publicapi.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "false"
)
public class UserLoginController {

    private final UserService userService;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public UserLoginController(
            UserService userService,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.userService = userService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /* =====================================================
     * LOGIN API
     * ===================================================== */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {

        System.out.println("🔐 LOGIN API HIT");

        if (request == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED",
                    "message", "Request body missing"
            ));
        }

        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null ||
                username.isBlank() || password.isBlank()) {

            return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED",
                    "message", "Username and password are required"
            ));
        }

        username = username.trim();

        /* USER LOOKUP */
        User user = userService
                .findByApplicationNumberOrEmail(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "FAILED",
                    "message", "Invalid credentials"
            ));
        }

        /* ACCOUNT CHECKS */
        if (!user.isEnabled()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", "BLOCKED",
                    "message", "Account blocked by admin"
            ));
        }

        if (userService.isAccountLocked(user)) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(Map.of(
                    "status", "LOCKED",
                    "message", "Account locked due to multiple failures"
            ));
        }

        /* PASSWORD CHECK */
        if (!userService.matchPassword(password, user.getPassword())) {

            userService.increaseFailedAttempts(user);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "FAILED",
                    "message", "Invalid credentials"
            ));
        }

        /* SUCCESS */
        userService.resetFailedAttempts(user);

        /* UPDATE LOGIN INFO */
        userService.updateLoginInfo(user.getEmail(), httpRequest);

        /* RELOAD USER */
        user = userService.findByEmail(user.getEmail());

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        String jwtToken = jwtService.generateToken(userDetails);

        /* ✅ SAFE RESPONSE MAP (allows null values) */
        Map<String, Object> response = new HashMap<>();

        response.put("status", "SUCCESS");
        response.put("message", "Login successful");
        response.put("token", jwtToken);
        response.put("role", user.getRole());
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("applicationNumber", user.getApplicationNumber());
        response.put("loginTime", user.getLastLoginTime());
        response.put("ipAddress", user.getLastLoginIp());

        return ResponseEntity.ok(response);
    }
}