package com.jee.publicapi.basicwindow.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.*;

import com.jee.publicapi.basicwindow.entity.BasicWindow;
import com.jee.publicapi.basicwindow.service.BasicWindowService;

@RestController
@RequestMapping("/api/user/basic-window")
public class UserBasicWindowController {

    private final BasicWindowService basicWindowService;

    public UserBasicWindowController(BasicWindowService basicWindowService) {
        this.basicWindowService = basicWindowService;
    }

    // =====================================================
    // CHECK IF CORRECTION WINDOW IS ACTIVE
    // =====================================================
    @GetMapping("/active/{requestId}")
    public boolean isWindowActive(@PathVariable Long requestId) {

        System.out.println("===== USER CHECK WINDOW STATUS =====");
        System.out.println("Request ID : " + requestId);

        boolean active = basicWindowService.isWindowActiveForRequest(requestId);

        System.out.println("Window Active : " + active);

        return active;
    }
    
    @GetMapping("/status/{requestId}")
    public Map<String, Object> getWindowStatus(@PathVariable Long requestId) {
        boolean active = basicWindowService.isWindowActiveForRequest(requestId);
        Optional<BasicWindow> window = basicWindowService.getWindowByRequestId(requestId);

        Map<String, Object> response = new HashMap<>();
        response.put("windowActive", active);
        response.put("windowStart", window.map(BasicWindow::getStartDate).orElse(null));
        response.put("windowEnd", window.map(BasicWindow::getEndDate).orElse(null));

        return response;
    }
    
}