package com.jee.publicapi.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jee.publicapi.dto.PersonalDetailsDTO;
import com.jee.publicapi.entity.PersonalDetails;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.service.CorrectionWindowService;
import com.jee.publicapi.service.PersonalDetailsService;
import com.jee.publicapi.service.UserService;

@RestController
@RequestMapping("/api/user/personal-details")
public class PersonalDetailsController {

	@Autowired
	private PersonalDetailsService personalDetailsService;

	@Autowired
	private UserService userService;

	@Autowired
	private CorrectionWindowService correctionService;

	/*
	 * ========================================================= GET CURRENT USER
	 * PERSONAL DETAILS =========================================================
	 */
	@GetMapping("/current")
	public ResponseEntity<PersonalDetailsDTO> getCurrentUserDetails(
			@RequestHeader(value = "Authorization", required = false) String authHeader) {

		User user = userService.getUserFromToken(authHeader);
		PersonalDetails details = personalDetailsService.getPersonalDetails(user);

		PersonalDetailsDTO res = new PersonalDetailsDTO();

		// ===== USER BASIC INFO =====
		res.setFirstName(user.getFirstName());
		res.setMiddleName(user.getMiddleName());
		res.setLastName(user.getLastName());
		res.setGender(user.getGender());
		// ✅ ADD THIS LINE (MISSING)
		res.setPersonalStatus(user.getPersonalStatus() != null ? user.getPersonalStatus() : FormStatus.NOT_STARTED);

		if (user.getDay() != null && user.getMonth() != null && user.getYear() != null) {
			res.setDob(String.format("%04d-%02d-%02d", user.getYear(), user.getMonth(), user.getDay()));
		}

		res.setAadharCard(user.getAadharCard());
		res.setMobileNumber(user.getMobileNumber());
		res.setEmailAddress(user.getEmail());

		res.setApplicationNo(user.getApplicationNumber() != null ? user.getApplicationNumber().toString() : null);

		// ===== PERSONAL DETAILS =====
		if (details != null) {

		    res.setFatherName(details.getFatherName());
		    res.setMotherName(details.getMotherName());
		    res.setNationality(details.getNationality());
		    res.setCategory(details.getCategory());
		    res.setMaritalStatus(details.getMaritalStatus());
		    res.setDisability(details.getDisability());
		    res.setCorrespondenceAddress(details.getCorrespondenceAddress());

		    // ✅ COUNTRY
		    if(details.getCountry()!=null){
		        res.setCountryId(details.getCountry().getId());
		        res.setCountryName(details.getCountry().getName());
		    }

		    // ✅ STATE
		    if(details.getState()!=null){
		        res.setStateId(details.getState().getId());
		        res.setStateName(details.getState().getName());
		    }

		    // ✅ DISTRICT
		    if(details.getDistrict()!=null){
		        res.setDistrictId(details.getDistrict().getId());
		        res.setDistrictName(details.getDistrict().getName());
		    }

		    // ✅ CITY
		    if(details.getCity()!=null){
		        res.setCityId(details.getCity().getId());
		        res.setCityName(details.getCity().getName());
		    }

		    // ✅ PINCODE
		    if(details.getPincode()!=null){
		        res.setPincodeId(details.getPincode().getId());   // ✅ REQUIRED
		        res.setPincode(details.getPincode().getCode());   // display
		    }

		    res.setSameAsCorrespondence(details.getSameAsCorrespondence());
		    res.setPermanentAddress(details.getPermanentAddress());
		}

		return ResponseEntity.ok(res);
	}

	/*
	 * ========================================================= SAVE DRAFT
	 * (IN_PROGRESS) =========================================================
	 */
	@PostMapping("/save")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> saveDraft(Authentication authentication, @RequestBody PersonalDetailsDTO dto) {

		String email = authentication.getName();
		User user = userService.findByEmail(email);

		if (user == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		/*
		 * ===================================== ✅ CHECK 1 : NORMAL LOCK
		 * =====================================
		 */
		if (user.getPersonalStatus() == FormStatus.COMPLETED
				&& !correctionService.isSectionEditable(user, "PERSONAL")) {

			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Personal section locked"));
		}

		/*
		 * ===================================== ✅ CHECK 2 : FINAL SUBMIT LOCK
		 * =====================================
		 */
		if (Boolean.TRUE.equals(user.getFinalSubmitted()) && !correctionService.isSectionEditable(user, "PERSONAL")) {

			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Correction window closed"));
		}

		/*
		 * ===================================== ✅ SAVE ONLY AFTER VALIDATION
		 * =====================================
		 */
		personalDetailsService.saveDraft(user, dto);

		return ResponseEntity
				.ok(Map.of("message", "Draft saved successfully", "personalStatus", FormStatus.IN_PROGRESS.name()));
	}

	/*
	 * ========================================================= SUBMIT FORM
	 * =========================================================
	 */
	@PostMapping("/submit")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> submitForm(Authentication authentication, @RequestBody PersonalDetailsDTO dto) {

		String email = authentication.getName();
		User user = userService.findByEmail(email);

		if (user == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		//✅ CORRECTION WINDOW CHECK
		
		if (user.getPersonalStatus() == FormStatus.COMPLETED
				&& !correctionService.isSectionEditable(user, "PERSONAL")) {

			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Correction window closed"));
		}

		personalDetailsService.submit(user, dto);
		System.out.println("DTO City ID: " + dto.getCityId());
		System.out.println("DTO Pincode ID: " + dto.getPincodeId());

		return ResponseEntity
				.ok(Map.of("message", "Form submitted successfully", "personalStatus", FormStatus.COMPLETED.name()));
	}
}
