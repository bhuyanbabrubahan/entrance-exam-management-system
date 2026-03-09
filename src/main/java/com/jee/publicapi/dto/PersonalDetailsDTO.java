package com.jee.publicapi.dto;

import com.jee.publicapi.enums.FormStatus;

public class PersonalDetailsDTO {
	/* ================= BASIC INFO ================= */
    private String applicationNo;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String dob; // yyyy-MM-dd
    private String aadharCard;
    private String mobileNumber;
    private String emailAddress;

    /* ================= PERSONAL INFO ================= */
    private String fatherName;
    private String motherName;
    private String nationality;
    private String category;
    private String maritalStatus;
    private String disability;
    private FormStatus personalStatus;

    /* ================= ADDRESS DETAILS ================= */

    private String correspondenceAddress;

    /* ---------- LOCATION IDS (USED FOR SAVE + DROPDOWN) ---------- */
    private Long countryId;
    private Long stateId;
    private Long districtId;
    private Long cityId;
    private Long pincodeId;

    /* ---------- LOCATION NAMES (USED FOR DISPLAY ONLY) ---------- */
    private String countryName;
    private String stateName;
    private String districtName;
    private String cityName;
    private String pincode;

    private Boolean sameAsCorrespondence;
    private String permanentAddress;

    /* ================= ADMIN ================= */
    private Boolean reopenAllowed = false;


    /* ================= GETTERS & SETTERS ================= */

    public String getApplicationNo() { return applicationNo; }
    public void setApplicationNo(String applicationNo) { this.applicationNo = applicationNo; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getAadharCard() { return aadharCard; }
    public void setAadharCard(String aadharCard) { this.aadharCard = aadharCard; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public String getDisability() { return disability; }
    public void setDisability(String disability) { this.disability = disability; }

    public FormStatus getPersonalStatus() { return personalStatus; }
    public void setPersonalStatus(FormStatus personalStatus) { this.personalStatus = personalStatus; }

    public String getCorrespondenceAddress() { return correspondenceAddress; }
    public void setCorrespondenceAddress(String correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }

    public Long getCountryId() { return countryId; }
    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public Long getStateId() { return stateId; }
    public void setStateId(Long stateId) { this.stateId = stateId; }

    public Long getDistrictId() { return districtId; }
    public void setDistrictId(Long districtId) { this.districtId = districtId; }

    public Long getCityId() { return cityId; }
    public void setCityId(Long cityId) { this.cityId = cityId; }

    public Long getPincodeId() { return pincodeId; }
    public void setPincodeId(Long pincodeId) { this.pincodeId = pincodeId; }

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }

    public String getStateName() { return stateName; }
    public void setStateName(String stateName) { this.stateName = stateName; }

    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public Boolean getSameAsCorrespondence() { return sameAsCorrespondence; }
    public void setSameAsCorrespondence(Boolean sameAsCorrespondence) {
        this.sameAsCorrespondence = sameAsCorrespondence;
    }

    public String getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public Boolean getReopenAllowed() { return reopenAllowed; }
    public void setReopenAllowed(Boolean reopenAllowed) {
        this.reopenAllowed = reopenAllowed;
    }
    
    
}