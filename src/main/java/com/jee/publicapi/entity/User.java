package com.jee.publicapi.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.enums.UserStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class User implements UserDetails {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private Integer applicationNumber;

	private String firstName;
	private String middleName;
	private String lastName;

	private String gender;
	private Integer day;
	private Integer month;
	private Integer year;

	private String mobileNumber;
	private String email;

	@Column(name = "aadhaar_number", nullable = false, unique = true)
	private String aadharCard;

	private String password;

	private boolean verified; // OTP verified

	private String role; // ROLE_USER / ROLE_ADMIN

	@Column(columnDefinition = "TEXT")
	private String barcodeImage;

	private LocalDateTime createdAt;
	private LocalDateTime lastLoginTime;
	private String lastLoginIp;

	private boolean enabled = true; // block/allow
	private boolean accountNonLocked = true; // system lock

	private int failedAttempts = 0;
	private LocalDateTime lockTime;

	@Enumerated(EnumType.STRING)
	private FormStatus personalStatus = FormStatus.NOT_STARTED;

	@Enumerated(EnumType.STRING)
	private FormStatus educationStatus = FormStatus.NOT_STARTED;

	@Enumerated(EnumType.STRING)
	private FormStatus documentStatus = FormStatus.NOT_STARTED;

	@Enumerated(EnumType.STRING)
	private FormStatus paymentStatus = FormStatus.NOT_STARTED;
	
	

	/* ================= FINAL SUBMISSION ================= */

	@Column(name = "final_submitted")
	private Boolean finalSubmitted = false;

	@Column(name = "final_submitted_at")
	private LocalDateTime finalSubmittedAt;

	private Boolean paymentCompleted;
	private LocalDateTime otpSentTime;

	@JsonManagedReference
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private PersonalDetails personalDetails;

	@JsonManagedReference
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private EducationDetails educationDetails;

	@JsonManagedReference
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private DocumentDetails documentDetails;
	/*
	 * ===================================================== 🔐 SPRING SECURITY
	 * REQUIRED METHODS (ADDED ONLY)
	 * =====================================================
	 */

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String actualRole = role;
		if (!role.startsWith("ROLE_")) {
			actualRole = "ROLE_" + role; // ensures Spring Security works
		}
		System.out.println("User authorities: " + actualRole); // debug print
		return List.of(new SimpleGrantedAuthority(actualRole));
	}

	public Boolean getFinalSubmitted() {
		return finalSubmitted;
	}

	public void setFinalSubmitted(Boolean finalSubmitted) {
		this.finalSubmitted = finalSubmitted;
	}

	public LocalDateTime getFinalSubmittedAt() {
		return finalSubmittedAt;
	}

	public void setFinalSubmittedAt(LocalDateTime finalSubmittedAt) {
		this.finalSubmittedAt = finalSubmittedAt;
	}

	public Boolean getPaymentCompleted() {
		return paymentCompleted;
	}

	public void setPaymentCompleted(Boolean paymentCompleted) {
		this.paymentCompleted = paymentCompleted;
	}

	public LocalDateTime getOtpSentTime() {
		return otpSentTime;
	}

	public void setOtpSentTime(LocalDateTime otpSentTime) {
		this.otpSentTime = otpSentTime;
	}

	@Override
	public String getUsername() {
		return email; // login via email
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked && lockTime == null;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(Integer applicationNumber) {
		this.applicationNumber = applicationNumber;
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

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}


	public String getBarcodeImage() {
		return barcodeImage;
	}

	public void setBarcodeImage(String barcodeImage) {
		this.barcodeImage = barcodeImage;
	}

	public FormStatus getPersonalStatus() {
		return personalStatus;
	}

	public void setPersonalStatus(FormStatus personalStatus) {
		this.personalStatus = personalStatus;
	}

	public FormStatus getEducationStatus() {
		return educationStatus;
	}

	public void setEducationStatus(FormStatus educationStatus) {
		this.educationStatus = educationStatus;
	}

	public FormStatus getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(FormStatus documentStatus) {
		this.documentStatus = documentStatus;
	}

	public FormStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(FormStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(LocalDateTime lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public int getFailedAttempts() {
		return failedAttempts;
	}

	public void setFailedAttempts(int failedAttempts) {
		this.failedAttempts = failedAttempts;
	}

	public LocalDateTime getLockTime() {
		return lockTime;
	}

	public void setLockTime(LocalDateTime lockTime) {
		this.lockTime = lockTime;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public PersonalDetails getPersonalDetails() {
		return personalDetails;
	}

	public void setPersonalDetails(PersonalDetails personalDetails) {
		this.personalDetails = personalDetails;
	}

	public EducationDetails getEducationDetails() {
		return educationDetails;
	}

	public void setEducationDetails(EducationDetails educationDetails) {
		this.educationDetails = educationDetails;
	}

	public DocumentDetails getDocumentDetails() {
		return documentDetails;
	}

	public void setDocumentDetails(DocumentDetails documentDetails) {
		this.documentDetails = documentDetails;
	}

}
