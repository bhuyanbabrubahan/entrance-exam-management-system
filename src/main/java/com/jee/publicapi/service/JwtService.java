package com.jee.publicapi.service;

import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    /* ========= LOGIN JWT ========= */
    String generateToken(UserDetails userDetails);

    String extractUsername(String token);

    Set<String> extractRoles(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    /* ========= CAPTCHA JWT ========= */
    String generateCaptchaToken(String captchaId, String captchaText);

    boolean validateCaptchaToken(String token, String userInput);
}
