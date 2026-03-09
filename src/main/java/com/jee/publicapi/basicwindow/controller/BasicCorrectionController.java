package com.jee.publicapi.basicwindow.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.jee.publicapi.basicwindow.dto.BasicCorrectionDetailsDTO;
import com.jee.publicapi.basicwindow.dto.BasicCorrectionUpdateDTO;
import com.jee.publicapi.basicwindow.service.BasicCorrectionService;

@RestController
@RequestMapping("/api/user/basic-correction-update")
@CrossOrigin(origins = "http://localhost:5173")
public class BasicCorrectionController {

    private final BasicCorrectionService basicCorrectionService;

    public BasicCorrectionController(BasicCorrectionService basicCorrectionService) {
        this.basicCorrectionService = basicCorrectionService;
    }

    @GetMapping("/details/{requestId}")
    public ResponseEntity<BasicCorrectionDetailsDTO> getDetails(@PathVariable Long requestId) {
        return ResponseEntity.ok(basicCorrectionService.getCorrectionDetails(requestId));
    }

    @PutMapping("/update/{requestId}")
    public ResponseEntity<?> updateDetails(@PathVariable Long requestId,
                                           @RequestBody BasicCorrectionUpdateDTO dto) {
        try {
            basicCorrectionService.updateBasicDetails(requestId, dto);
            return ResponseEntity.ok(Map.of("message", "Basic details updated successfully"));
        } catch (ResponseStatusException ex) {
            return ResponseEntity
                    .status(ex.getStatusCode()) // ✅ use getStatusCode() instead of getStatus()
                    .body(Map.of("message", ex.getReason()));
        }
    }
}