package com.jee.publicapi.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jee.publicapi.location.entity.City;
import com.jee.publicapi.location.entity.Country;
import com.jee.publicapi.location.entity.District;
import com.jee.publicapi.location.entity.Pincode;
import com.jee.publicapi.location.entity.State;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "personal_details")
public class PersonalDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/* ===== BASIC INFORMATION (Read-only, fetched from registration) ===== */
	@Column(name = "application_no", unique = true, nullable = false)
	private String applicationNumber;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "middle_name")
	private String middleName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Column(name = "gender", nullable = false)
	private String gender;

	@Column(name = "dob", nullable = false)
	private LocalDate dob;

	@Column(name = "aadhaar_number")
	private String aadhaarCard;

	@Column(name = "mobile_number", nullable = false)
	private String mobileNumber;

	@Column(name = "email_address", nullable = false)
	private String email;

	/* ===== PERSONAL INFORMATION (Editable) ===== */
	@Column(name = "father_name", nullable = false)
	private String fatherName;

	@Column(name = "mother_name", nullable = false)
	private String motherName;

	@Column(name = "nationality", nullable = false)
	private String nationality;

	@Column(name = "category", nullable = false)
	private String category; // GEN/OBC/SC/ST/EWS

	@Column(name = "marital_status", nullable = false)
	private String maritalStatus; // Single/Married

	@Column(name = "disability", nullable = false)
	private String disability; // Yes/No

	/* ===== ADDRESS DETAILS (Editable) ===== */
	@Column(name = "correspondence_address", nullable = false, length = 500)
	private String correspondenceAddress;


	@Column(name = "same_as_correspondence")
	private Boolean sameAsCorrespondence;

	@Column(name = "permanent_address", length = 500)
	private String permanentAddress;

	/* ===== ADMIN REOPEN FLAG ===== */
	@Column
	private Boolean reopenAllowed = false;

	/* ===== AUDIT ===== */
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	/* ===== USER LINK ===== */
	@JsonBackReference
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="country_id")
	private Country country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="state_id")
	private State state;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="district_id")
	private District district;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="city_id")
	private City city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="pincode_id")
	private Pincode pincode;
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(String applicationNumber) {
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

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getAadhaarCard() {
		return aadhaarCard;
	}

	public void setAadhaarCard(String aadhaarCard) {
		this.aadhaarCard = aadhaarCard;
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

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getDisability() {
		return disability;
	}

	public void setDisability(String disability) {
		this.disability = disability;
	}

	public String getCorrespondenceAddress() {
		return correspondenceAddress;
	}

	public void setCorrespondenceAddress(String correspondenceAddress) {
		this.correspondenceAddress = correspondenceAddress;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public Boolean getSameAsCorrespondence() {
		return sameAsCorrespondence;
	}

	public void setSameAsCorrespondence(Boolean sameAsCorrespondence) {
		this.sameAsCorrespondence = sameAsCorrespondence;
	}

	public String getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Boolean getReopenAllowed() {
		return reopenAllowed;
	}

	public void setReopenAllowed(Boolean reopenAllowed) {
		this.reopenAllowed = reopenAllowed;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Pincode getPincode() {
		return pincode;
	}

	public void setPincode(Pincode pincode) {
		this.pincode = pincode;
	}

	

}