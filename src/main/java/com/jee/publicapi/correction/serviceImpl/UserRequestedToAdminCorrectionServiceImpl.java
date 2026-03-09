package com.jee.publicapi.correction.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.jee.publicapi.basicwindow.entity.BasicWindow;
import com.jee.publicapi.basicwindow.service.BasicWindowService;
import com.jee.publicapi.correction.dto.AdminCorrectionResponseDTO;
import com.jee.publicapi.correction.entity.UserCorrectionRequest;
import com.jee.publicapi.correction.enums.CorrectionStatus;
import com.jee.publicapi.correction.repository.UserCorrectionRequestRepository;
import com.jee.publicapi.correction.service.UserRequestedToAdminCorrectionService;

import jakarta.transaction.Transactional;

/* =========================================================
   ADMIN CORRECTION SERVICE IMPLEMENTATION
   Handles:
   - Admin listing (search + filter + pagination)
   - Approve request
   - Reject request
   - Field history
   ========================================================= */

@Service
@Transactional
public class UserRequestedToAdminCorrectionServiceImpl implements UserRequestedToAdminCorrectionService {

	private final UserCorrectionRequestRepository requestRepo;
	private final BasicWindowService basicWindowService;

	public UserRequestedToAdminCorrectionServiceImpl(UserCorrectionRequestRepository requestRepo,
			BasicWindowService basicWindowService) {
		this.requestRepo = requestRepo;
		this.basicWindowService = basicWindowService;
	}

	/*
	 * ===================================================== ADMIN LIST (Single
	 * Optimized Query) =====================================================
	 */

	@Override
	public Page<AdminCorrectionResponseDTO> getAllRequests(int page, int size, String status, String search) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("requestedAt").descending());

		CorrectionStatus enumStatus = null;
		
		

		if (status != null && !status.equalsIgnoreCase("ALL")) {
			enumStatus = CorrectionStatus.valueOf(status.toUpperCase());
		}

		String finalSearch = (search == null || search.isBlank()) ? null : search.trim();

		Page<UserCorrectionRequest> requestPage = requestRepo.searchRequests(enumStatus, finalSearch, pageable);

		// 🔥 HERE IS THE IMPORTANT PART
		return requestPage.map(req -> {

			AdminCorrectionResponseDTO dto = new AdminCorrectionResponseDTO();

			dto.setId(req.getId());
			dto.setUserId(req.getUser().getId());
			dto.setUserName(req.getUser().getFirstName()); // 🔥 add this
			dto.setApplicationNumber(req.getApplicationNumber());

			dto.setFieldName(req.getFieldName());
			dto.setOldValue(req.getOldValue()); // 🔥 add
			dto.setRequestedValue(req.getRequestedValue()); // 🔥 add
			dto.setReason(req.getReason()); // 🔥 add

			dto.setStatus(req.getStatus().name());
			dto.setRequestedAt(req.getRequestedAt());
			dto.setReviewedAt(req.getReviewedAt());
			dto.setAdminRemark(req.getAdminRemark());
			dto.setAttemptNumber(req.getAttemptNumber());
			dto.setUserUpdatedAt(req.getUserUpdatedAt());       // ✅ important: include userUpdatedAt
	        dto.setCorrectionCompleted(req.isCorrectionCompleted());

			// 🔥 DOCUMENT PATHS
			if (req.getDocuments() != null) {
				dto.setDocumentPaths(req.getDocuments().stream().map(doc -> doc.getFilePath()).toList());
			}

			// ✅ ADD THIS BLOCK BELOW
	        Optional<BasicWindow> windowOpt = basicWindowService.getWindowByRequestId(req.getId());
	        if (windowOpt.isPresent()) {
	            BasicWindow window = windowOpt.get();
	            dto.setWindowStart(window.getStartDate());
	            dto.setWindowEnd(window.getEndDate());
	            dto.setWindowActive(basicWindowService.isWindowActiveForRequest(req.getId()));
	            // Important: Use DB values, do not override with NOW()
	            dto.setDeActivatedAt(window.getDeActivatedAt());
	            // If null, default to false
	            dto.setDeActivatedByAdmin(window.getDeActivatedByAdmin() != null ? window.getDeActivatedByAdmin() : false);

	        } else {
	            dto.setWindowActive(false);
	            dto.setDeActivatedByAdmin(false); // explicitly set false to avoid null
	        }

			return dto;
		});
	}

	/*
	 * ===================================================== APPROVE REQUEST
	 * =====================================================
	 */

	@Override
	public void approveRequest(Long requestId, Long adminId, String remark) {

		UserCorrectionRequest request = requestRepo.findById(requestId)
				.orElseThrow(() -> new RuntimeException("Request not found"));

		if (request.getStatus() != CorrectionStatus.REQUESTED) {
			throw new RuntimeException("Request already processed");
		}

		LocalDateTime now = LocalDateTime.now();

		request.setStatus(CorrectionStatus.APPROVED);
		request.setAdminRemark(remark);
		request.setReviewedAt(now);
		request.setReviewedByAdminId(adminId);

		requestRepo.save(request);

	}
	/*
	 * ===================================================== REJECT REQUEST
	 * =====================================================
	 */

	@Override
	public void rejectRequest(Long requestId, Long adminId, String remark) {

		UserCorrectionRequest request = requestRepo.findById(requestId)
				.orElseThrow(() -> new RuntimeException("Request not found"));

		if (request.getStatus() != CorrectionStatus.REQUESTED) {
			throw new RuntimeException("Request already processed");
		}

		request.setStatus(CorrectionStatus.REJECTED);
		request.setAdminRemark(remark);
		request.setReviewedAt(LocalDateTime.now());
		request.setReviewedByAdminId(adminId);

		requestRepo.save(request);
	}

	/*
	 * ===================================================== FIELD HISTORY
	 * =====================================================
	 */

	@Override
	public List<UserCorrectionRequest> getFieldHistory(Long userId, String fieldName) {

		return requestRepo.findFieldHistory(userId, fieldName);
	}

	/* =====================Correction count of user================ */
	public AdminCorrectionResponseDTO getCorrectionCountStats() {

		long totalRequests = requestRepo.count();
		long totalUsers = requestRepo.countDistinctUsers();

		System.out.println("===== DASHBOARD STATS DEBUG =====");
		System.out.println("Total Requests: " + totalRequests);
		System.out.println("Total Distinct Users: " + totalUsers);
		System.out.println("==================================");

		AdminCorrectionResponseDTO dto = new AdminCorrectionResponseDTO();
		dto.setTotalUsers(totalUsers);
		dto.setTotalRequests(totalRequests);

		return dto;
	}

}