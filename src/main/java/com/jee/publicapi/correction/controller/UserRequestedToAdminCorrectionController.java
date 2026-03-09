package com.jee.publicapi.correction.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jee.publicapi.basicwindow.service.BasicWindowService;
import com.jee.publicapi.correction.dto.AdminCorrectionResponseDTO;
import com.jee.publicapi.correction.entity.UserCorrectionDocument;
import com.jee.publicapi.correction.entity.UserCorrectionRequest;
import com.jee.publicapi.correction.service.UserCorrectionRequestService;
import com.jee.publicapi.correction.service.UserRequestedToAdminCorrectionService;
import com.jee.publicapi.security.CustomUserDetails;

@RestController
@RequestMapping("/api/admin/user-requested-admin-correction")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UserRequestedToAdminCorrectionController {

	private final UserCorrectionRequestService correctionService;
	private final UserRequestedToAdminCorrectionService userRequestedToAdminCorrectionService;
	private final BasicWindowService basicWindowService;

	public UserRequestedToAdminCorrectionController(UserCorrectionRequestService correctionService,
			UserRequestedToAdminCorrectionService userRequestedToAdminCorrectionService,
			BasicWindowService basicWindowService) {
		this.correctionService = correctionService;
		this.userRequestedToAdminCorrectionService = userRequestedToAdminCorrectionService;
		this.basicWindowService = basicWindowService;
	}

	@PutMapping("/{id}/approve")
	public ResponseEntity<?> approve(@PathVariable Long id,
	        @RequestBody Map<String, String> body,
	        Authentication authentication) {

	    Long adminId = ((CustomUserDetails) authentication.getPrincipal()).getId();

	    userRequestedToAdminCorrectionService
	            .approveRequest(id, adminId, body.get("adminRemark"));

	    return ResponseEntity.ok("Approved Successfully");
	}

	@PutMapping("/{id}/reject")
	public ResponseEntity<?> reject(@PathVariable Long id, @RequestBody Map<String, String> body,
			Authentication authentication) {

		Long adminId = ((CustomUserDetails) authentication.getPrincipal()).getId();

		correctionService.rejectRequest(id, adminId, body.get("adminRemark"));

		return ResponseEntity.ok("Rejected Successfully");
	}

	private AdminCorrectionResponseDTO convertToDTO(UserCorrectionRequest r) {

		List<String> docs = r.getDocuments() == null ? List.of()
				: r.getDocuments().stream().map(UserCorrectionDocument::getFilePath).toList();

		AdminCorrectionResponseDTO dto = new AdminCorrectionResponseDTO();

		dto.setId(r.getId());
		dto.setUserId(r.getUser().getId());
		dto.setApplicationNumber(r.getApplicationNumber());
		dto.setUserName(r.getUser().getFirstName() + " " + r.getUser().getLastName());
		dto.setFieldName(r.getFieldName());
		dto.setOldValue(r.getOldValue());
		dto.setRequestedValue(r.getRequestedValue());
		dto.setReason(r.getReason());
		dto.setAttemptNumber(r.getAttemptNumber());
		dto.setStatus(r.getStatus().name());
		dto.setAdminRemark(r.getAdminRemark());
		dto.setRequestedAt(r.getRequestedAt());
		dto.setReviewedAt(r.getReviewedAt());
		dto.setUserUpdatedAt(r.getUserUpdatedAt());        // ✅ User update timestamp
		dto.setCorrectionCompleted(r.isCorrectionCompleted()); // ✅ Completed status
		dto.setDocumentPaths(docs);
		basicWindowService.getWindowByRequestId(r.getId()).ifPresent(window -> {
			dto.setWindowStart(window.getStartDate());
			dto.setWindowEnd(window.getEndDate());
			dto.setDeActivatedAt(window.getDeActivatedAt());          // use DB value
			dto.setDeActivatedByAdmin(window.getDeActivatedByAdmin());  // use DB value 
			
		});

		return dto;
	}

	@GetMapping("/history/{userId}/{fieldName}")
	public List<AdminCorrectionResponseDTO> getHistory(@PathVariable Long userId, @PathVariable String fieldName) {

		List<UserCorrectionRequest> history = correctionService.getFieldHistory(userId, fieldName);

		return history.stream().map(this::convertToDTO).toList();
	}

	@GetMapping("/all")
	public Page<AdminCorrectionResponseDTO> getAllRequests(@RequestParam int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ALL") String status,
			@RequestParam(defaultValue = "") String search) {

		return userRequestedToAdminCorrectionService.getAllRequests(page, size, status, search);
	}

	@GetMapping("/correction-count-stats")
	public ResponseEntity<AdminCorrectionResponseDTO> getCorrectionCountStats() {

		System.out.println("API HIT: /correction-count-stats");

		AdminCorrectionResponseDTO stats = userRequestedToAdminCorrectionService.getCorrectionCountStats();

		return ResponseEntity.ok(stats);
	}
}
