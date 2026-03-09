package com.jee.publicapi.dto;

import org.springframework.web.multipart.MultipartFile;

import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.enums.UploadStatus;

import jakarta.persistence.Column;

public class DocumentDetailsDTO {

	// For responses
	private String photo;
	private String signature;
	private String marksheet;

	private Boolean photoUploaded;
	private Boolean signatureUploaded;
	private Boolean marksheetUploaded;

	private UploadStatus uploadStatus;
	private FormStatus documentStatus;

	private String sourceOfInfo;

	// For uploads
	private MultipartFile photoFile;
	private MultipartFile signatureFile;
	private MultipartFile marksheetFile;
	
	@Column
	private Boolean reopenAllowed = false;

	public Boolean getReopenAllowed() {
		return reopenAllowed;
	}

	public void setReopenAllowed(Boolean reopenAllowed) {
		this.reopenAllowed = reopenAllowed;
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

	public MultipartFile getPhotoFile() {
		return photoFile;
	}

	public void setPhotoFile(MultipartFile photoFile) {
		this.photoFile = photoFile;
	}

	public MultipartFile getSignatureFile() {
		return signatureFile;
	}

	public void setSignatureFile(MultipartFile signatureFile) {
		this.signatureFile = signatureFile;
	}

	public MultipartFile getMarksheetFile() {
		return marksheetFile;
	}

	public void setMarksheetFile(MultipartFile marksheetFile) {
		this.marksheetFile = marksheetFile;
	}

}
