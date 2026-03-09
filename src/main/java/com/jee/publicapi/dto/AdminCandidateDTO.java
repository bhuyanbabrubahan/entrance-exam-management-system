package com.jee.publicapi.dto;

public class AdminCandidateDTO {

    private Long userId;
    private String applicationNo;

    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;

    private String email;
    private String mobileNumber;
    private boolean enabled;

    // ===== FORM STATUS AS STRING =====
    private String personalStatus;
    private String educationStatus;
    private String documentStatus;
    private String paymentStatus;

    // ===== ADMIN FLAGS =====
    private boolean personalReopen;
    private boolean educationReopen;
    private boolean documentReopen;

    // ===== GETTERS & SETTERS =====
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getApplicationNo() { return applicationNo; }
    public void setApplicationNo(String applicationNo) { this.applicationNo = applicationNo; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getPersonalStatus() { return personalStatus; }
    public void setPersonalStatus(String personalStatus) { this.personalStatus = personalStatus; }

    public String getEducationStatus() { return educationStatus; }
    public void setEducationStatus(String educationStatus) { this.educationStatus = educationStatus; }

    public String getDocumentStatus() { return documentStatus; }
    public void setDocumentStatus(String documentStatus) { this.documentStatus = documentStatus; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public boolean isPersonalReopen() { return personalReopen; }
    public void setPersonalReopen(boolean personalReopen) { this.personalReopen = personalReopen; }

    public boolean isEducationReopen() { return educationReopen; }
    public void setEducationReopen(boolean educationReopen) { this.educationReopen = educationReopen; }

    public boolean isDocumentReopen() { return documentReopen; }
    public void setDocumentReopen(boolean documentReopen) { this.documentReopen = documentReopen; }
}