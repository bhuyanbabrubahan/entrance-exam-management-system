package com.jee.publicapi.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.jee.publicapi.service.UserActivityService;
import com.jee.publicapi.util.DeviceDetector;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ActivityInterceptor implements HandlerInterceptor {

    private final UserActivityService activityService;

    public ActivityInterceptor(UserActivityService activityService) {
        this.activityService = activityService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        try {

            Authentication auth =
                    SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || !auth.isAuthenticated()
                    || auth.getPrincipal().equals("anonymousUser")) {

                return true;
            }

            String email = auth.getName();

            String endpoint = request.getRequestURI();
            String method = request.getMethod();

            String ip = getClientIp(request);

            String userAgent = request.getHeader("User-Agent");

            String browser = DeviceDetector.getBrowser(userAgent);
            String os = DeviceDetector.getOS(userAgent);
            String device = DeviceDetector.getDevice(userAgent);

            activityService.logActivity(
                    email,
                    method,
                    endpoint,
                    ip,
                    browser,
                    os,
                    device
            );

            System.out.println("Activity saved: " + email + " -> " + endpoint);

        } catch (Exception ex) {
            System.out.println("Activity log error: " + ex.getMessage());
        }

        return true;
    }

    private String getClientIp(HttpServletRequest request) {

        String xff = request.getHeader("X-Forwarded-For");

        String ip = (xff != null && !xff.isBlank())
                ? xff.split(",")[0]
                : request.getRemoteAddr();

        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }
}