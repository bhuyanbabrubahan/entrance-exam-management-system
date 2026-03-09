package com.jee.publicapi.basicwindow.dto;

import java.time.LocalDateTime;

public class BasicCorrectionDetailsDTO {

    private Long requestId;

    // which field user is allowed to edit
    private String fieldName;

    // current user data
    private String firstName;
    private String middleName;
    private String lastName;

    private String gender;

    private Integer day;
    private Integer month;
    private Integer year;

    private String aadharCard;
    
 // ==================== Window fields ====================
    private boolean windowActive;
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
    private Boolean deActivatedByAdmin;
    private LocalDateTime deActivatedAt;
    
    private boolean correctionCompleted;
    private LocalDateTime userUpdatedAt;

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getAadharCard() {
		return aadharCard;
	}

	public void setAadharCard(String aadharCard) {
		this.aadharCard = aadharCard;
	}

	public boolean isWindowActive() {
		return windowActive;
	}

	public void setWindowActive(boolean windowActive) {
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
