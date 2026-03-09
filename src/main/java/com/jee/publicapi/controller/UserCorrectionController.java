package com.jee.publicapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.jee.publicapi.dto.SectionStatusDTO;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.service.CorrectionWindowService;
import com.jee.publicapi.service.UserService;

@RestController
@RequestMapping("/api/user")
//@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class UserCorrectionController {

    @Autowired
    private CorrectionWindowService correctionWindowService;

    @Autowired
    private UserService userService;

    /* ================= USER SECTION STATUS ================= */
    @GetMapping("/correction-status")
    public ResponseEntity<SectionStatusDTO> getUserCorrectionStatus(
            @RequestHeader("Authorization") String authHeader) {

        User user = userService.getUserFromToken(authHeader);

        return ResponseEntity.ok(
                correctionWindowService.getCurrentSectionStatus(user));
    }
}