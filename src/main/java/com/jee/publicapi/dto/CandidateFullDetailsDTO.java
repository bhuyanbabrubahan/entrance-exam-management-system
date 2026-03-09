package com.jee.publicapi.dto;

public class CandidateFullDetailsDTO {

    private UserBasicDTO user;

    private PersonalDetailsDTO personalDetails;
    private EducationDetailsDTO educationDetails;
    private DocumentDetailsDTO documentDetails;

    private String personalStatus;
    private String educationStatus;
    private String documentStatus;

    // ===== GETTERS & SETTERS =====

    public UserBasicDTO getUser() { return user; }
    public void setUser(UserBasicDTO user) { this.user = user; }

    public PersonalDetailsDTO getPersonalDetails() { return personalDetails; }
    public void setPersonalDetails(PersonalDetailsDTO personalDetails) { this.personalDetails = personalDetails; }

    public EducationDetailsDTO getEducationDetails() { return educationDetails; }
    public void setEducationDetails(EducationDetailsDTO educationDetails) { this.educationDetails = educationDetails; }

    public DocumentDetailsDTO getDocumentDetails() { return documentDetails; }
    public void setDocumentDetails(DocumentDetailsDTO documentDetails) { this.documentDetails = documentDetails; }

    public String getPersonalStatus() { return personalStatus; }
    public void setPersonalStatus(String personalStatus) { this.personalStatus = personalStatus; }

    public String getEducationStatus() { return educationStatus; }
    public void setEducationStatus(String educationStatus) { this.educationStatus = educationStatus; }

    public String getDocumentStatus() { return documentStatus; }
    public void setDocumentStatus(String documentStatus) { this.documentStatus = documentStatus; }
}