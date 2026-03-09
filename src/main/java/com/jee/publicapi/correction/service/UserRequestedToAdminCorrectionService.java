package com.jee.publicapi.correction.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.jee.publicapi.correction.dto.AdminCorrectionResponseDTO;
import com.jee.publicapi.correction.entity.UserCorrectionRequest;

public interface UserRequestedToAdminCorrectionService {

	Page<AdminCorrectionResponseDTO> getAllRequests(
            int page,
            int size,
            String status,
            String search
    );

    void approveRequest(Long requestId, Long adminId, String remark);

    void rejectRequest(Long requestId, Long adminId, String remark);

    List<UserCorrectionRequest> getFieldHistory(Long userId, String fieldName);
    
    AdminCorrectionResponseDTO getCorrectionCountStats();
    
}
