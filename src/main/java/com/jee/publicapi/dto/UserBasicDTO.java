package com.jee.publicapi.dto;

public class UserBasicDTO {

    private Long id;
    private String applicationNumber;
    private String email;
    private String fullName;
    private String role;

    public UserBasicDTO() {}

    public UserBasicDTO(Long id,
                        String applicationNumber,
                        String email,
                        String fullName,
                        String role) {
        this.id = id;
        this.applicationNumber = applicationNumber;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    // ===== GETTERS & SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApplicationNumber() { return applicationNumber; }
    public void setApplicationNumber(String applicationNumber) { this.applicationNumber = applicationNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}