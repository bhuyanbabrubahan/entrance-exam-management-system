package com.jee.publicapi.correction.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jee.publicapi.correction.enums.CorrectionStatus;
import com.jee.publicapi.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_correction_requests")
public class UserCorrectionRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/* ================= USER LINK ================= */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnoreProperties({ "correctionRequests" })
	private User user;

	private String applicationNumber;

	private String fieldName;

	@Column(length = 500)
	private String oldValue;

	@Column(length = 500)
	private String requestedValue;

	@Column(length = 1000)
	private String reason;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CorrectionStatus status;

	private String adminRemark;

	private LocalDateTime requestedAt;

	private LocalDateTime reviewedAt;

	private Long reviewedByAdminId;
	
	 // WINDOW DATA
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
    private Boolean windowActive;
    private Boolean deActivatedByAdmin;
    private LocalDateTime deActivatedAt;

    @Column(nullable = false)
    private boolean correctionCompleted = false;
    @Column(name = "user_updated_at")
    private LocalDateTime userUpdatedAt;

	private Integer attemptNumber; // 1,2,3

	/* ================= DOCUMENTS ================= */

	@OneToMany(mappedBy = "correctionRequest", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties({ "correctionRequest" })
	private List<UserCorrectionDocument> documents;

	@PrePersist
	public void prePersist() {
		requestedAt = LocalDateTime.now();
		status = CorrectionStatus.REQUESTED;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
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

	public CorrectionStatus getStatus() {
		return status;
	}

	public void setStatus(CorrectionStatus status) {
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

	public Long getReviewedByAdminId() {
		return reviewedByAdminId;
	}

	public void setReviewedByAdminId(Long reviewedByAdminId) {
		this.reviewedByAdminId = reviewedByAdminId;
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

	public Boolean getWindowActive() {
		return windowActive;
	}

	public void setWindowActive(Boolean windowActive) {
		this.windowActive = windowActive;
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

	public Integer getAttemptNumber() {
		return attemptNumber;
	}

	public void setAttemptNumber(Integer attemptNumber) {
		this.attemptNumber = attemptNumber;
	}

	public List<UserCorrectionDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<UserCorrectionDocument> documents) {
		this.documents = documents;
	}

	

}
