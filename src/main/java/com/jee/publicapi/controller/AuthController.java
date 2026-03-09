package com.jee.publicapi.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jee.publicapi.dto.LoginRequest;
import com.jee.publicapi.security.CustomUserDetailsService;
import com.jee.publicapi.service.JwtService;
import com.jee.publicapi.service.UserService;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(
            UserService userService,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.userService = userService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        return userService
            .findByApplicationNumberOrEmail(request.getUsername())
            .filter(user ->
                userService.matchPassword(
                    request.getPassword(),
                    user.getPassword()
                )
            )
            .map(user -> {

                var userDetails =
                        userDetailsService
                            .loadUserByUsername(user.getEmail());

                String token =
                        jwtService.generateToken(userDetails);

                return ResponseEntity.ok(
                    Map.of(
                        "token", token,
                        "role", user.getRole(),
                        "email", user.getEmail()
                    )
                );
            })
            .orElseGet(() ->
                ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"))
            );
    }
}
