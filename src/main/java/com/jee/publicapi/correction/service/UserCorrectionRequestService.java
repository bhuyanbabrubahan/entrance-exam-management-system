package com.jee.publicapi.correction.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.jee.publicapi.correction.dto.CorrectionHistoryDTO;
import com.jee.publicapi.correction.dto.UserCorrectionResponseDTO;
import com.jee.publicapi.correction.entity.UserCorrectionRequest;
import com.jee.publicapi.correction.enums.CorrectionStatus;
import com.jee.publicapi.entity.User;

public interface UserCorrectionRequestService {

	/* ================= USER SIDE ================= */

	// Submit correction request
	UserCorrectionRequest submitCorrection(Long userId, String fieldName, String requestedValue, String reason,
			List<MultipartFile> documents);

	boolean isSectionEditable(User user, String fieldName);

	List<CorrectionHistoryDTO> getMyRequests(String email);

	// ✅ Add this method
	List<UserCorrectionRequest> getUserHistory(Long userId);
	
	// 🔥 ADD THIS
	List<UserCorrectionRequest> getFieldHistory(Long userId, String fieldName);

	/* ================= ADMIN SIDE ================= */


	// Approve
	void approveRequest(Long requestId, Long adminId, String remark);

	// Reject
	void rejectRequest(Long requestId, Long adminId, String remark);

	List<UserCorrectionRequest> findByUserIdOrderByRequestedAtDesc(Long userId);

	// Optional: check if user has pending request for a field
	boolean existsByUserIdAndFieldNameAndStatusNot(Long userId, String fieldName, CorrectionStatus status);
	

	
	
	

}