package com.jee.publicapi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.enums.UploadStatus;

@Entity
@Table(name = "document_details")
public class DocumentDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonBackReference
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(columnDefinition = "TEXT")
	private String photo;

	@Column(columnDefinition = "TEXT")
	private String signature;

	@Column(columnDefinition = "TEXT")
	private String marksheet;

	private Boolean photoUploaded = false;
	private Boolean signatureUploaded = false;
	private Boolean marksheetUploaded = false;

	@Enumerated(EnumType.STRING)
	private UploadStatus uploadStatus = UploadStatus.PENDING;

	@Enumerated(EnumType.STRING)
	private FormStatus documentStatus = FormStatus.NOT_STARTED;

	@Column(columnDefinition = "JSON")
	private String sourceOfInfo;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column
	private Boolean reopenAllowed = false;
	
	@PrePersist
	protected void onCreate() {
	    this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
	    this.updatedAt = LocalDateTime.now();
	}

	public Boolean getReopenAllowed() {
		return reopenAllowed;
	}

	public void setReopenAllowed(Boolean reopenAllowed) {
		this.reopenAllowed = reopenAllowed;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
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

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getMarksheet() {
		return marksheet;
	}

	public void setMarksheet(String marksheet) {
		this.marksheet = marksheet;
	}

	public Boolean getPhotoUploaded() {
		return photoUploaded;
	}

	public void setPhotoUploaded(Boolean photoUploaded) {
		this.photoUploaded = photoUploaded;
	}

	public Boolean getSignatureUploaded() {
		return signatureUploaded;
	}

	public void setSignatureUploaded(Boolean signatureUploaded) {
		this.signatureUploaded = signatureUploaded;
	}

	public Boolean getMarksheetUploaded() {
		return marksheetUploaded;
	}

	public void setMarksheetUploaded(Boolean marksheetUploaded) {
		this.marksheetUploaded = marksheetUploaded;
	}

	public UploadStatus getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(UploadStatus uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public FormStatus getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(FormStatus documentStatus) {
		this.documentStatus = documentStatus;
	}

	public String getSourceOfInfo() {
		return sourceOfInfo;
	}

	public void setSourceOfInfo(String sourceOfInfo) {
		this.sourceOfInfo = sourceOfInfo;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
