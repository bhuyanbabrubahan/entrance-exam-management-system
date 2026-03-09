package com.jee.publicapi.correction.serviceImpl;


import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.jee.publicapi.correction.dto.CorrectionDashboardDTO;
import com.jee.publicapi.correction.entity.UserCorrectionRequest;
import com.jee.publicapi.correction.repository.UserCorrectionRequestRepository;
import com.jee.publicapi.correction.service.CorrectionDashboardService;

@Service
public class CorrectionDashboardServiceImpl
        implements CorrectionDashboardService {

    private final UserCorrectionRequestRepository requestRepo;

    public CorrectionDashboardServiceImpl(
            UserCorrectionRequestRepository requestRepo) {
        this.requestRepo = requestRepo;
    }

    @Override
    public CorrectionDashboardDTO getDashboardStatus(Long userId) {

        long total = requestRepo.countByUserId(userId);

        UserCorrectionRequest latest =
                requestRepo.findTopByUserIdOrderByRequestedAtDesc(userId);

        CorrectionDashboardDTO dto = new CorrectionDashboardDTO();
        dto.setTotalRequests(total);

        if (latest != null) {
            dto.setStatus(latest.getStatus().name()); // ✅ ENUM → STRING

            LocalDateTime last =
                    latest.getReviewedAt() != null
                            ? latest.getReviewedAt()
                            : latest.getRequestedAt();

            dto.setLastUpdated(last != null ? last.toString() : null);
        } else {
            dto.setStatus("NO_REQUEST"); // better than "No Request"
            dto.setLastUpdated(null);
        }

        return dto;
    }
}