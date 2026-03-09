package com.jee.publicapi.controller;

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

/**
 * =====================================================
 * USER LOGIN CONTROLLER (JWT ONLY)
 * -----------------------------------------------------
 * ✔ Stateless
 * ✔ No Session
 * ✔ No Cookie
 * ✔ Returns JWT on success
 * =====================================================
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "false" // 🔥 JWT → NO COOKIES
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
     * LOGIN API (JWT)
     * ===================================================== */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {

        System.out.println("🔐 LOGIN API HIT");

        if (request == null) {
            System.out.println("❌ Request body missing");
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED",
                    "message", "Request body missing"
            ));
        }

        String username = request.get("username");
        String password = request.get("password");

        System.out.println("➡ Username received: " + username);

        if (username == null || password == null ||
            username.isBlank() || password.isBlank()) {

            System.out.println("❌ Username or password empty");

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
            System.out.println("❌ User not found: " + username);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "FAILED",
                    "message", "Invalid credentials"
            ));
        }

        System.out.println("✅ User found: " + user.getEmail());

        /* ACCOUNT CHECKS */
        if (!user.isEnabled()) {
            System.out.println("⛔ Account disabled");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", "BLOCKED",
                    "message", "Account blocked by admin"
            ));
        }

        if (userService.isAccountLocked(user)) {
            System.out.println("🔒 Account locked");

            return ResponseEntity.status(HttpStatus.LOCKED).body(Map.of(
                    "status", "LOCKED",
                    "message", "Account locked due to multiple failures"
            ));
        }

        /* PASSWORD CHECK */
        if (!userService.matchPassword(password, user.getPassword())) {
            System.out.println("❌ Password mismatch");

            userService.increaseFailedAttempts(user);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "FAILED",
                    "message", "Invalid credentials"
            ));
        }

        System.out.println("✅ Password matched");

        /* SUCCESS */
        userService.resetFailedAttempts(user);
        userService.updateLoginInfo(user.getEmail(), httpRequest);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        String jwtToken = jwtService.generateToken(userDetails);

        System.out.println("🎫 JWT GENERATED");

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Login successful",
                "token", jwtToken,
                "role", user.getRole(),
                "email", user.getEmail()
        ));
    }

}
