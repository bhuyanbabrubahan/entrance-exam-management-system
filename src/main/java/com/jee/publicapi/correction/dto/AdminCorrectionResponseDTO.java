package com.jee.publicapi.correction.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AdminCorrectionResponseDTO {

	private Long id;

    private Long userId;

    private String applicationNumber;

    private String userName;

    private String fieldName;

    private String oldValue;

    private String requestedValue;

    private String reason;

    private String status;

    private String adminRemark;

    private LocalDateTime requestedAt;

    private LocalDateTime reviewedAt;

    private Integer updateCount;

    private Integer attemptNumber; // 1,2,3
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime windowStart;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime windowEnd;
    
    private List<String> documentPaths;
    
    private long totalUsers;
    private long totalRequests;
    
    private boolean windowActive;
    private Boolean deActivatedByAdmin = false;
    private LocalDateTime deActivatedAt;
    
    private boolean correctionCompleted;
    private LocalDateTime userUpdatedAt;

	public boolean isCorrectionCompleted() {
		return correctionCompleted;
	}

	public void setCorrectionCompleted(boolean correctionCompleted) {
		this.correctionCompleted = correctionCompleted;
	}

	public LocalDateTime getUserUpdatedAt() {
		return userUpdatedAt;
	}

	public void setUserUpdatedAt(LocalDateTime userUpdatedAt) {
		this.userUpdatedAt = userUpdatedAt;
	}

	public Boolean getDeActivatedByAdmin() {
		return deActivatedByAdmin;
	}

	public void setDeActivatedByAdmin(Boolean deActivatedByAdmin) {
		this.deActivatedByAdmin = deActivatedByAdmin;
	}

	public LocalDateTime getDeActivatedAt() {
		return deActivatedAt;
	}

	public void setDeActivatedAt(LocalDateTime deActivatedAt) {
		this.deActivatedAt = deActivatedAt;
	}

	public boolean isWindowActive() {
		return windowActive;
	}

	public void setWindowActive(boolean windowActive) {
		this.windowActive = windowActive;
	}

	public AdminCorrectionResponseDTO() {
        // default constructor
    }

	public long getTotalUsers() {
		return totalUsers;
	}

	public void setTotalUsers(long totalUsers) {
		this.totalUsers = totalUsers;
	}

	public long getTotalRequests() {
		return totalRequests;
	}

	public void setTotalRequests(long totalRequests) {
		this.totalRequests = totalRequests;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getRequestedValue() {
		return requestedValue;
	}

	public void setRequestedValue(String requestedValue) {
		this.requestedValue = requestedValue;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAdminRemark() {
		return adminRemark;
	}

	public void setAdminRemark(String adminRemark) {
		this.adminRemark = adminRemark;
	}

	public LocalDateTime getRequestedAt() {
		return requestedAt;
	}

	public void setRequestedAt(LocalDateTime requestedAt) {
		this.requestedAt = requestedAt;
	}

	public LocalDateTime getReviewedAt() {
		return reviewedAt;
	}

	public void setReviewedAt(LocalDateTime reviewedAt) {
		this.reviewedAt = reviewedAt;
	}

	public Integer getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(Integer updateCount) {
		this.updateCount = updateCount;
	}

	public Integer getAttemptNumber() {
		return attemptNumber;
	}

	public void setAttemptNumber(Integer attemptNumber) {
		this.attemptNumber = attemptNumber;
	}

	public LocalDateTime getWindowStart() {
		return windowStart;
	}

	public void setWindowStart(LocalDateTime windowStart) {
		this.windowStart = windowStart;
	}

	public LocalDateTime getWindowEnd() {
		return windowEnd;
	}

	public void setWindowEnd(LocalDateTime windowEnd) {
		this.windowEnd = windowEnd;
	}

	public List<String> getDocumentPaths() {
		return documentPaths;
	}

	public void setDocumentPaths(List<String> documentPaths) {
		this.documentPaths = documentPaths;
	}

	
    
}