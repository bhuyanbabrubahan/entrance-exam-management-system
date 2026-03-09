package com.jee.publicapi.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jee.publicapi.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // If no Bearer token → continue filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        // Safety check for malformed token
        if (jwt == null || jwt.trim().isEmpty() || !jwt.contains(".")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String username = jwtService.extractUsername(jwt);

            if (username != null) {

                // 🔥 VERY IMPORTANT CHECK
                if (SecurityContextHolder.getContext().getAuthentication() == null
                        || SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {

                    UserDetails userDetails =
                            userDetailsService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(jwt, userDetails)) {

                        // 🔥 FORCE CustomUserDetails principal
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,   // MUST be object, not String
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        // Debug logs
                        System.out.println("JWT username: " + username);
                        System.out.println("Authentication set for: " + username);
                        System.out.println("Principal class: "
                                + SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getPrincipal()
                                .getClass());

                        System.out.println("Authentication SUCCESS for: " + username);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("JWT parsing failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}