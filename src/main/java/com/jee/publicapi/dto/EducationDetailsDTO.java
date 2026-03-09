package com.jee.publicapi.dto;
import com.jee.publicapi.enums.ExamStatus;
import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.enums.MarksType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class EducationDetailsDTO {

    private String board10;
    private String schoolName10;
    private String rollNumber10;
    private Integer passingYear10;
    private MarksType marksType10;
    private Double percentage10;

    private String board12;
    private String schoolName12;
    private String rollNumber12;
    private Integer passingYear12;
    private String stream12;
    private MarksType marksType12;
    private Double percentage12;
    private Double pcmPercentage;

    private ExamStatus examStatus;
    private Integer appearingYear;
    
    @Enumerated(EnumType.STRING)
    private FormStatus educationStatus;
    
    /* ===== ADMIN REOPEN FLAG ===== */
    private Boolean reopenAllowed = false; // <--- add this

	public String getBoard10() {
		return board10;
	}

	public void setBoard10(String board10) {
		this.board10 = board10;
	}

	public String getSchoolName10() {
		return schoolName10;
	}

	public void setSchoolName10(String schoolName10) {
		this.schoolName10 = schoolName10;
	}

	public String getRollNumber10() {
		return rollNumber10;
	}

	public void setRollNumber10(String rollNumber10) {
		this.rollNumber10 = rollNumber10;
	}

	public Integer getPassingYear10() {
		return passingYear10;
	}

	public void setPassingYear10(Integer passingYear10) {
		this.passingYear10 = passingYear10;
	}

	public MarksType getMarksType10() {
		return marksType10;
	}

	public void setMarksType10(MarksType marksType10) {
		this.marksType10 = marksType10;
	}

	public Double getPercentage10() {
		return percentage10;
	}

	public void setPercentage10(Double percentage10) {
		this.percentage10 = percentage10;
	}

	public String getBoard12() {
		return board12;
	}

	public void setBoard12(String board12) {
		this.board12 = board12;
	}

	public String getSchoolName12() {
		return schoolName12;
	}

	public void setSchoolName12(String schoolName12) {
		this.schoolName12 = schoolName12;
	}

	public String getRollNumber12() {
		return rollNumber12;
	}

	public void setRollNumber12(String rollNumber12) {
		this.rollNumber12 = rollNumber12;
	}

	public Integer getPassingYear12() {
		return passingYear12;
	}

	public void setPassingYear12(Integer passingYear12) {
		this.passingYear12 = passingYear12;
	}

	public String getStream12() {
		return stream12;
	}

	public void setStream12(String stream12) {
		this.stream12 = stream12;
	}

	public MarksType getMarksType12() {
		return marksType12;
	}

	public void setMarksType12(MarksType marksType12) {
		this.marksType12 = marksType12;
	}

	public Double getPercentage12() {
		return percentage12;
	}

	public void setPercentage12(Double percentage12) {
		this.percentage12 = percentage12;
	}

	public Double getPcmPercentage() {
		return pcmPercentage;
	}

	public void setPcmPercentage(Double pcmPercentage) {
		this.pcmPercentage = pcmPercentage;
	}

	public ExamStatus getExamStatus() {
		return examStatus;
	}

	public void setExamStatus(ExamStatus examStatus) {
		this.examStatus = examStatus;
	}

	public Integer getAppearingYear() {
		return appearingYear;
	}

	public void setAppearingYear(Integer appearingYear) {
		this.appearingYear = appearingYear;
	}

	public FormStatus getEducationStatus() {
		return educationStatus;
	}

	public void setEducationStatus(FormStatus educationStatus) {
		this.educationStatus = educationStatus;
	}

	public Boolean getReopenAllowed() {
		return reopenAllowed;
	}

	public void setReopenAllowed(Boolean reopenAllowed) {
		this.reopenAllowed = reopenAllowed;
	}
    
	
    
    
}