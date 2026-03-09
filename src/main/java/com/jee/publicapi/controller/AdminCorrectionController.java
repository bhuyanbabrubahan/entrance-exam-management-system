package com.jee.publicapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.jee.publicapi.dto.AdminCorrectionDTO;
import com.jee.publicapi.entity.CorrectionWindow;
import com.jee.publicapi.service.CorrectionWindowService;

@RestController
@RequestMapping("/api/admin/correction")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminCorrectionController {

    @Autowired
    private CorrectionWindowService correctionWindowService;

    @PostMapping("/activate")
    public ResponseEntity<String> activate(
            @RequestBody AdminCorrectionDTO dto) {

        CorrectionWindow window = new CorrectionWindow();

        window.setStartDateTime(dto.getStartDateTime());
        window.setEndDateTime(dto.getEndDateTime());
        window.setUnlockPersonal(dto.isUnlockPersonal());
        window.setUnlockEducation(dto.isUnlockEducation());
        window.setUnlockDocuments(dto.isUnlockDocuments());

        correctionWindowService.activateCorrectionWindow(window);

        return ResponseEntity.ok("Activated");
    }

    @PutMapping("/deactivate")
    public ResponseEntity<String> deactivate() {
        correctionWindowService.deactivateAllWindows();
        return ResponseEntity.ok("Deactivated");
    }

    @GetMapping("/active")
    public ResponseEntity<CorrectionWindow> active() {
        return ResponseEntity.ok(
                correctionWindowService.getActiveWindow());
    }
}