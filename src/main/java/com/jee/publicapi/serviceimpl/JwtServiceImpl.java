package com.jee.publicapi.serviceimpl;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.jee.publicapi.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {

    /* =====================================================
       SECRET KEY (256-bit minimum)
       ===================================================== */
    private static final String SECRET_KEY =
            "THIS_IS_A_VERY_SECURE_SECRET_KEY_FOR_JWT_256_BITS";

    private static final long LOGIN_EXPIRATION =
            1000 * 60 * 60 * 24; // 24 hours

    private static final long CAPTCHA_EXPIRATION =
            1000 * 60 * 2; // 2 minutes

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /* =====================================================
       🔐 LOGIN JWT (USER / ADMIN)
       ===================================================== */
    @Override
    public String generateToken(UserDetails userDetails) {

        Set<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + LOGIN_EXPIRATION)
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    @Override
    public Set<String> extractRoles(String token) {

        List<String> roles =
                extractAllClaims(token).get("roles", List.class);

        return Set.copyOf(roles);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    /* =====================================================
       🧩 CAPTCHA JWT (STATELESS)
       ===================================================== */
    @Override
    public String generateCaptchaToken(String captchaId, String captchaText) {

        return Jwts.builder()
                .setClaims(Map.of(
                        "cid", captchaId,
                        "captcha", captchaText
                ))
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + CAPTCHA_EXPIRATION)
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean validateCaptchaToken(String token, String userInput) {

        try {
            Claims claims = extractAllClaims(token);
            String expectedCaptcha = claims.get("captcha", String.class);

            return expectedCaptcha != null
                    && expectedCaptcha.equalsIgnoreCase(userInput);

        } catch (Exception e) {
            return false;
        }
    }

    /* =====================================================
       COMMON UTIL
       ===================================================== */
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
