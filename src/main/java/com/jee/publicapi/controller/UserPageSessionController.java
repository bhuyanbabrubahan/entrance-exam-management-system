package com.jee.publicapi.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.jee.publicapi.service.UserPageSessionService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/user/page-session")
public class UserPageSessionController {

    private final UserPageSessionService service;

    public UserPageSessionController(UserPageSessionService service) {
        this.service = service;
    }

    @PostMapping("/start")
    public void startSession(
            @RequestBody Map<String,String> req,
            Authentication authentication) {

        String email = authentication.getName();
        String pageName = req.get("pageName");
        service.startSession(email, pageName);
    }

    @PostMapping("/end")
    public void endSession(
            @RequestBody Map<String,String> req,
            Authentication authentication) {

        String email = authentication.getName();
        String pageName = req.get("pageName");
        service.endSession(email, pageName);
    }
}