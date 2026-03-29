package com.jee.publicapi.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.jee.publicapi.dto.AdminActivityDto;
import com.jee.publicapi.entity.UserActivity;
import com.jee.publicapi.repository.UserActivityRepository;
import com.jee.publicapi.serviceimpl.UserActivityServiceImpl;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin/activity")
public class AdminActivityController {

    private final UserActivityRepository repo;
    private final UserActivityServiceImpl activityService;

    public AdminActivityController(UserActivityRepository repo,
    		UserActivityServiceImpl activityService) {
        this.repo = repo;
        this.activityService = activityService;
    }

    /* Latest Activities */
    @GetMapping("/latest")
    public List<AdminActivityDto> getLatestActivity() {

        return activityService.getLatestActivities();
    }

    /* Activity by user */
    @GetMapping("/user/{email}")
    public List<UserActivity> userActivity(@PathVariable String email) {
        return repo.findByEmailOrderByActivityTimeDesc(email);
    }
}