package com.jee.publicapi.dto;

public class RegistrationRequest {

    private String firstName;
    private String middleName;
    private String lastName;

    private String gender;
    private Integer day;
    private Integer month;
    private Integer year;

    private String mobileNumber;
    private String email;
    private String aadharCard;
    private String password;

    private String captcha;
    private String captchaId;

    /* ================= Getters & Setters ================= */

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

    public String getMobileNumber() {
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getAadharCard() {
		return aadharCard;
	}
	public void setAadharCard(String aadharCard) {
		this.aadharCard = aadharCard;
	}
	public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }
    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptchaId() {
        return captchaId;
    }
    public void setCaptchaId(String captchaId) {
        this.captchaId = captchaId;
    }
}
