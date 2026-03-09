package com.jee.publicapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "correction_window")
public class CorrectionWindow {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;

	private Boolean unlockPersonal;
	private Boolean unlockEducation;
	private Boolean unlockDocuments;

	private Boolean active;

	public Boolean getUnlockPersonal() {
		return unlockPersonal;
	}

	public void setUnlockPersonal(Boolean unlockPersonal) {
		this.unlockPersonal = unlockPersonal;
	}

	public Boolean getUnlockEducation() {
		return unlockEducation;
	}

	public void setUnlockEducation(Boolean unlockEducation) {
		this.unlockEducation = unlockEducation;
	}

	public Boolean getUnlockDocuments() {
		return unlockDocuments;
	}

	public void setUnlockDocuments(Boolean unlockDocuments) {
		this.unlockDocuments = unlockDocuments;
	}

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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}