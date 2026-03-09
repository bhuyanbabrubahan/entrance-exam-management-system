package com.jee.publicapi.dto;

import java.time.LocalDateTime;

public class AdminCorrectionDTO {

    private Long id;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private boolean unlockPersonal;
    private boolean unlockEducation;
    private boolean unlockDocuments;

    private boolean active;

    private String message;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}

	public boolean isUnlockPersonal() {
		return unlockPersonal;
	}

	public void setUnlockPersonal(boolean unlockPersonal) {
		this.unlockPersonal = unlockPersonal;
	}

	public boolean isUnlockEducation() {
		return unlockEducation;
	}

	public void setUnlockEducation(boolean unlockEducation) {
		this.unlockEducation = unlockEducation;
	}

	public boolean isUnlockDocuments() {
		return unlockDocuments;
	}

	public void setUnlockDocuments(boolean unlockDocuments) {
		this.unlockDocuments = unlockDocuments;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    
}