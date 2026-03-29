package com.jee.publicapi.serviceimpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jee.publicapi.dto.AdminActivityDto;
import com.jee.publicapi.entity.UserActivity;
import com.jee.publicapi.entity.UserPageSession;
import com.jee.publicapi.repository.UserActivityLogRepository;
import com.jee.publicapi.repository.UserPageSessionRepository;
import com.jee.publicapi.service.UserActivityService;

@Service
public class UserActivityServiceImpl implements UserActivityService {

    private final UserActivityLogRepository activityRepository;
    private final UserPageSessionRepository sessionRepository;

    public UserActivityServiceImpl(
            UserActivityLogRepository activityRepository,
            UserPageSessionRepository sessionRepository) {
        this.activityRepository = activityRepository;
        this.sessionRepository = sessionRepository;
    }

    /* ================= SAVE ACTIVITY ================= */
    @Override
    public void logActivity(
            String email,
            String method,
            String endpoint,
            String ip,
            String browser,
            String os,
            String device) {

        UserActivity log = new UserActivity();
        log.setEmail(email);
        log.setMethod(method);
        log.setEndpoint(endpoint);
        log.setIpAddress(ip);
        log.setBrowser(browser);
        log.setOs(os);
        log.setDevice(device);
        log.setActivityTime(LocalDateTime.now());

        // Set pageName based on endpoint mapping if needed
        log.setPageName(mapEndpointToPageName(endpoint));

        activityRepository.save(log);
    }

    /* ================= ADMIN ACTIVITY LIST ================= */
    public List<AdminActivityDto> getLatestActivities() {
        List<UserActivity> activities =
                activityRepository.findTop50ByOrderByActivityTimeDesc();

        return activities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /* ================= ENTITY → DTO ================= */
    private AdminActivityDto convertToDto(UserActivity activity) {
        AdminActivityDto dto = new AdminActivityDto();

        dto.setId(activity.getId());
        dto.setEmail(activity.getEmail());
        dto.setEndpoint(activity.getEndpoint());
        dto.setMethod(activity.getMethod());
        dto.setIpAddress(activity.getIpAddress());
        dto.setBrowser(activity.getBrowser());
        dto.setDevice(activity.getDevice());
        dto.setActivityTime(activity.getActivityTime());

        // Use the real pageName from activity or session
        String pageName = activity.getPageName();
        dto.setPageName(pageName);

        // Look up session for this user and this page
        if (pageName != null) {
            Optional<UserPageSession> sessionOpt =
                    sessionRepository.findTopByEmailAndPageNameOrderByStartTimeDesc(
                            activity.getEmail(),
                            pageName
                    );

            if (sessionOpt.isPresent()) {
                UserPageSession session = sessionOpt.get();

                // Duration: use stored value or calculate live
                if (session.getDurationSeconds() != null) {
                    dto.setDurationSeconds(session.getDurationSeconds());
                } else {
                    dto.setDurationSeconds(
                            Duration.between(session.getStartTime(), LocalDateTime.now()).getSeconds()
                    );
                }
            } else {
                dto.setDurationSeconds(activity.getDurationSeconds()); // fallback
            }
        } else {
            dto.setDurationSeconds(activity.getDurationSeconds());
        }

        return dto;
    }

    /* ================= HELPER ================= */
    private String mapEndpointToPageName(String endpoint) {
        if (endpoint == null) return null;

        return switch (endpoint) {
        case "/api/user/correction-status" -> "Correction Status Page";
        case "/api/user/user-correction-request/status" -> "User Correction Request";
        case "/api/user/dashboard/current" -> "Dashboard Page";
        case "/api/user/personal-details/current" -> "Personal Details Page";
        case "/api/user/education-details/current" -> "Education Details page";
        case "/api/user/document-details/current" -> "Document Details Page";
        case "/api/payment/details" -> "Payment Page";
        case "/api/user/user-correction-request/dashboard-status" -> "Correction Dashboard Page";
        //case "/api/admin/activity/latest" -> "User Activity Monitor";
        default -> "Unknown Page";
        };
    }
}