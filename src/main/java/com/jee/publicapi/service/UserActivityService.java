package com.jee.publicapi.service;

import java.util.List;

import com.jee.publicapi.dto.AdminActivityDto;

public interface UserActivityService {

    void logActivity(
            String email,
            String method,
            String endpoint,
            String ip,
            String browser,
            String os,
            String device
    );
    
    /* FETCH ADMIN ACTIVITY */
    List<AdminActivityDto> getLatestActivities();

}