package com.jee.publicapi.basicwindow.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jee.publicapi.basicwindow.service.BasicWindowService;

@RestController
@RequestMapping("/api/admin/basic-window")
public class AdminBasicWindowController {

    private final BasicWindowService basicWindowService;

    public AdminBasicWindowController(BasicWindowService basicWindowService) {
        this.basicWindowService = basicWindowService;
    }

    // =====================================================
    // ACTIVATE WINDOW
    // =====================================================
    @PutMapping("/open/{requestId}")
    public ResponseEntity<?> activateWindow(@PathVariable Long requestId) {
        try {
            // Activate the window using existing service
            basicWindowService.activeWindowForRequest(requestId);

            // ✅ reset deactivation fields in DB
            basicWindowService.getWindowByRequestId(requestId).ifPresent(window -> {
                window.setDeActivatedByAdmin(false);
                window.setDeActivatedAt(null);
                basicWindowService.save(window);   // IMPORTANT
            });

            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Correction window activated"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "ERROR",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "ERROR",
                "message", "Unexpected error: " + e.getMessage()
            ));
        }
    }

    // =====================================================
    // DEACTIVATE WINDOW
    // =====================================================
    @PutMapping("/deactivate/{requestId}")
    public ResponseEntity<?> deactivateWindow(@PathVariable Long requestId) {
        try {
            basicWindowService.deactivateWindow(requestId);
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Window deactivated by admin"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "ERROR",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "ERROR",
                "message", "Unexpected error: " + e.getMessage()
            ));
        }
    }
}