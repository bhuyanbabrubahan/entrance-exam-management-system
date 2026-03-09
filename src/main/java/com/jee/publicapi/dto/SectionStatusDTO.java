package com.jee.publicapi.dto;

public class SectionStatusDTO {
	private boolean correctionActive;

	private boolean personalEditable;
	private boolean educationEditable;
	private boolean documentEditable;

	public boolean isCorrectionActive() {
		return correctionActive;
	}

	public void setCorrectionActive(boolean correctionActive) {
		this.correctionActive = correctionActive;
	}

	public boolean isPersonalEditable() {
		return personalEditable;
	}

	public void setPersonalEditable(boolean personalEditable) {
		this.personalEditable = personalEditable;
	}

	public boolean isEducationEditable() {
		return educationEditable;
	}

	public void setEducationEditable(boolean educationEditable) {
		this.educationEditable = educationEditable;
	}

	public boolean isDocumentEditable() {
		return documentEditable;
	}

	public void setDocumentEditable(boolean documentEditable) {
		this.documentEditable = documentEditable;
	}

}