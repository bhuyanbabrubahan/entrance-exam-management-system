package com.jee.publicapi.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /* ================= AUTH MANAGER ================= */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /* ================= SECURITY ================= */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            .authorizeHttpRequests(auth -> auth

                /* 🔥 ALLOW PREFLIGHT (THIS FIXES principal NULL) */
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                /* ---------- PUBLIC ---------- */
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/favicon.ico",
                    "/error",
                    "/captcha/**",
                    "/api/auth/**",
                    "/api/user/register/**",
                    "/api/user/otp/**",   
                    "/api/user/basic-correction-update/**", 
                    "/correction-files/**",
                    ("/files/**")
                ).permitAll()

                /* ---------- ADMIN ---------- */
                .requestMatchers("/api/admin/**")
                .hasRole("ADMIN")

                /* ---------- USER ---------- */
                .requestMatchers("/api/user/**")
                .hasRole("USER")

                /* ---------- EVERYTHING ELSE ---------- */
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    /* ================= CORS ================= */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 🔥 IMPORTANT FOR AUTH HEADER

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
