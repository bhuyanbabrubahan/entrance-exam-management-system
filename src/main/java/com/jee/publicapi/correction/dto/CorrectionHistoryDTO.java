package com.jee.publicapi.correction.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CorrectionHistoryDTO {

	private Long id;
    private String fieldName;
    private String oldValue;
    private String requestedValue;
    private String reason;
    private String status;
    private String adminRemark;

    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;

    private Integer attemptNumber;

    private List<String> documentPaths;

    // WINDOW DATA
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
    private Boolean windowActive;
    private Boolean deActivatedByAdmin;
    private LocalDateTime deActivatedAt;
    
    private boolean correctionCompleted;
    private LocalDateTime userUpdatedAt;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getAdminRemark() {
		return adminRemark;
	}
	public void setAdminRemark(String adminRemark) {
		this.adminRemark = adminRemark;
	}
	public Boolean getWindowActive() {
		return windowActive;
	}
	public void setWindowActive(Boolean windowActive) {
		this.windowActive = windowActive;
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
	public Integer getAttemptNumber() {
		return attemptNumber;
	}
	public void setAttemptNumber(Integer attemptNumber) {
		this.attemptNumber = attemptNumber;
	}
	public List<String> getDocumentPaths() {
		return documentPaths;
	}
	public void setDocumentPaths(List<String> documentPaths) {
		this.documentPaths = documentPaths;
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
    
    
}
