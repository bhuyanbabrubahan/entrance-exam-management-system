package com.jee.publicapi.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jee.publicapi.dto.EducationDetailsDTO;
import com.jee.publicapi.entity.EducationDetails;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.repository.UserRepository;
import com.jee.publicapi.service.CorrectionWindowService;
import com.jee.publicapi.service.EducationDetailsService;

@RestController
@RequestMapping("/api/user/education-details")
public class EducationDetailsController {

    private final UserRepository userRepository;
    private final EducationDetailsService educationService;
    private final CorrectionWindowService correctionService;

    public EducationDetailsController(
            UserRepository userRepository,
            EducationDetailsService educationService,
            CorrectionWindowService correctionService) {

        this.userRepository = userRepository;
        this.educationService = educationService;
        this.correctionService = correctionService;
    }

    /* =====================================================
       GET CURRENT EDUCATION DETAILS
       ===================================================== */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrent() {

        System.out.println("\n========== [EDU][GET CURRENT] ==========");

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            System.out.println("[EDU][GET][ERROR] Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = auth.getName();
        System.out.println("[EDU][GET] User = " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("[EDU][GET][ERROR] User not found");
                    return new RuntimeException("User not found");
                });

        EducationDetails edu = educationService.getOrCreate(user);

        System.out.println("[EDU][GET] Education exists = " + (edu != null));
        System.out.println("[EDU][GET] User educationStatus = " + user.getEducationStatus());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("education", edu);
        response.put("educationStatus", user.getEducationStatus().name());

        return ResponseEntity.ok(response);
    }

    /* =====================================================
       SAVE DRAFT
       ===================================================== */
    @PostMapping("/save")
    public ResponseEntity<?> saveDraft(@RequestBody EducationDetailsDTO dto) {

        System.out.println("\n========== [EDU][SAVE DRAFT] ==========");

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();
        System.out.println("[EDU][SAVE] User = " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("[EDU][SAVE][ERROR] User not found");
                    return new RuntimeException("User not found");
                });

        EducationDetails edu = educationService.saveDraft(user, dto);

        userRepository.save(user);

        System.out.println("[EDU][SAVE] Saved successfully");
        System.out.println("[EDU][SAVE] Status now = " + user.getEducationStatus());

        return ResponseEntity.ok(
                Map.of(
                        "status", "SUCCESS",
                        "educationStatus", user.getEducationStatus().name()
                )
        );
    }

    /* =====================================================
       SUBMIT & LOCK
       ===================================================== */
    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody EducationDetailsDTO dto) {

        System.out.println("\n========== [EDU][SUBMIT] ==========");

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            System.out.println("[EDU][SUBMIT][ERROR] Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
        }

        String email = auth.getName();
        System.out.println("[EDU][SUBMIT] User = " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("[EDU][SUBMIT][ERROR] User not found");
                    return new RuntimeException("User not found");
                });

        /* 🔒 Correction Window Check */
        if (user.getEducationStatus() == FormStatus.COMPLETED
                && !correctionService.isSectionEditable(user, "EDUCATION")) {

            System.out.println("[EDU][SUBMIT] Correction window CLOSED");

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "status", "LOCKED",
                            "message", "Correction window closed"
                    ));
        }

        EducationDetails edu = educationService.submit(user, dto);

        userRepository.save(user);

        System.out.println("[EDU][SUBMIT] Submitted successfully");
        System.out.println("[EDU][SUBMIT] Status now = " + user.getEducationStatus());

        return ResponseEntity.ok(
                Map.of(
                        "status", "SUCCESS",
                        "educationStatus", user.getEducationStatus().name()
                )
        );
    }
}
