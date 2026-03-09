package com.jee.publicapi.serviceimpl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.jee.publicapi.dto.PersonalDetailsDTO;
import com.jee.publicapi.entity.PersonalDetails;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.location.entity.City;
import com.jee.publicapi.location.entity.Country;
import com.jee.publicapi.location.entity.District;
import com.jee.publicapi.location.entity.Pincode;
import com.jee.publicapi.location.entity.State;
import com.jee.publicapi.location.repository.CityRepository;
import com.jee.publicapi.location.repository.CountryRepository;
import com.jee.publicapi.location.repository.DistrictRepository;
import com.jee.publicapi.location.repository.PincodeRepository;
import com.jee.publicapi.location.repository.StateRepository;
import com.jee.publicapi.repository.PersonalDetailsRepository;
import com.jee.publicapi.repository.UserRepository;
import com.jee.publicapi.service.CorrectionWindowService;
import com.jee.publicapi.service.PersonalDetailsService;

import jakarta.transaction.Transactional;

@Service
public class PersonalDetailsServiceImpl implements PersonalDetailsService {

	private final PersonalDetailsRepository personalDetailsRepository;
    private final UserRepository userRepository;
    private final CorrectionWindowService correctionWindowService;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository; 
    private final DistrictRepository districtRepository;
    private final PincodeRepository pincodeRepository;

    // ===== Constructor Injection =====
    public PersonalDetailsServiceImpl(
            PersonalDetailsRepository personalDetailsRepository,
            UserRepository userRepository,
            CorrectionWindowService correctionWindowService,
            CountryRepository countryRepository,
            StateRepository stateRepository,
            CityRepository cityRepository,
            DistrictRepository districtRepository,
            PincodeRepository pincodeRepository) { 
        this.personalDetailsRepository = personalDetailsRepository;
        this.userRepository = userRepository;
        this.correctionWindowService = correctionWindowService;
        this.cityRepository = cityRepository;
        this.districtRepository = districtRepository;
        this.pincodeRepository = pincodeRepository;
        this.stateRepository = stateRepository;
        this.countryRepository = countryRepository;
    }


    /* =====================================================
       GET PERSONAL DETAILS
       ===================================================== */
    @Override
    public PersonalDetails getPersonalDetails(User user) {
        return personalDetailsRepository.findByUser(user).orElse(null);
    }

    /* =====================================================
    SAVE DRAFT
    ===================================================== */
	@Override
	@Transactional
	public PersonalDetailsDTO saveDraft(User user, PersonalDetailsDTO dto) {

		boolean correctionActive = correctionWindowService.isCorrectionActive();

		// ✅ LOCK ONLY WHEN:
		// Completed AND correction NOT active
		if (user.getPersonalStatus() == FormStatus.COMPLETED && !correctionActive) {

			throw new RuntimeException("Personal details already submitted. " + "Correction window closed.");
		}

		PersonalDetails pd = personalDetailsRepository.findByUser(user).orElse(new PersonalDetails());

		copyBasicInfoFromUser(pd, user);
		mapEditableFields(pd, dto);

		pd.setUser(user);
		personalDetailsRepository.save(pd);

		// ✅ DO NOT DOWNGRADE STATUS DURING CORRECTION
		if (user.getPersonalStatus() != FormStatus.COMPLETED) {
			user.setPersonalStatus(FormStatus.IN_PROGRESS);
			userRepository.save(user);
		}

		return mapToDTO(pd);
	}

	/* =====================================================
	   SUBMIT FORM
	   ===================================================== */
	@Transactional
	public PersonalDetailsDTO submit(User user,
	                                 PersonalDetailsDTO dto) {

	    boolean correctionActive =
	            correctionWindowService.isCorrectionActive();

	    if (user.getPersonalStatus() == FormStatus.COMPLETED
	            && !correctionActive) {

	        throw new RuntimeException(
	                "Correction window expired.");
	    }

	    PersonalDetails pd = personalDetailsRepository
	            .findByUser(user)
	            .orElse(new PersonalDetails());

	    pd.setUser(user);

	    mapEditableFields(pd, dto);

	    personalDetailsRepository.save(pd);

	    user.setPersonalStatus(FormStatus.COMPLETED);
	    userRepository.save(user);

	    return mapToDTO(pd);
	}

    /* =====================================================
       COPY BASIC INFO FROM USER (ONCE)
       ===================================================== */
    private void copyBasicInfoFromUser(PersonalDetails pd, User user) {

        // Application number (Integer → String)
        pd.setApplicationNumber(
            user.getApplicationNumber() != null
                ? user.getApplicationNumber().toString()
                : null
        );

        pd.setFirstName(user.getFirstName());
        pd.setMiddleName(user.getMiddleName());
        pd.setLastName(user.getLastName());
        pd.setGender(user.getGender());

        // DOB conversion (day/month/year → LocalDate)
        if (user.getDay() != null && user.getMonth() != null && user.getYear() != null) {
            pd.setDob(LocalDate.of(
                    user.getYear(),
                    user.getMonth(),
                    user.getDay()
            ));
        }

        pd.setAadhaarCard(user.getAadharCard());
        pd.setMobileNumber(user.getMobileNumber());
        pd.setEmail(user.getEmail());
    }


    /* =====================================================
       MAP EDITABLE FIELDS FROM DTO
       ===================================================== */
    private void mapEditableFields(
            PersonalDetails pd,
            PersonalDetailsDTO dto) {

        if(dto.getFatherName()!=null)
            pd.setFatherName(dto.getFatherName());

        if(dto.getMotherName()!=null)
            pd.setMotherName(dto.getMotherName());

        if(dto.getNationality()!=null)
            pd.setNationality(dto.getNationality());

        if(dto.getCategory()!=null)
            pd.setCategory(dto.getCategory());

        if(dto.getMaritalStatus()!=null)
            pd.setMaritalStatus(dto.getMaritalStatus());

        if(dto.getDisability()!=null)
            pd.setDisability(dto.getDisability());

        if(dto.getCorrespondenceAddress()!=null)
            pd.setCorrespondenceAddress(
                dto.getCorrespondenceAddress()
            );

        /* ================= LOCATION ================= */
        
        if(dto.getCountryId()!=null){

            Country country =
                countryRepository.findById(dto.getCountryId())
                .orElseThrow(() ->
                    new RuntimeException("Country not found"));

            pd.setCountry(country);
        }

        if(dto.getStateId()!=null){

            State state =
                stateRepository.findById(dto.getStateId())
                .orElseThrow(() ->
                    new RuntimeException("State not found"));

            pd.setState(state);
        }

        if(dto.getDistrictId()!=null){

            District district =
                districtRepository.findById(dto.getDistrictId())
                .orElseThrow(() ->
                    new RuntimeException("District not found"));

            pd.setDistrict(district);
        }

        /* ================= CITY ================= */
        if (dto.getCityId() != null) {

            if (pd.getCity() == null ||
                !pd.getCity().getId().equals(dto.getCityId())) {

                City city = cityRepository.findById(dto.getCityId())
                        .orElseThrow(() -> new RuntimeException("City not found"));

                pd.setCity(city);
                pd.setPincode(null); // reset only if changed
            }
        }
        /* ================= PINCODE ================= */
        if (dto.getPincodeId() != null) {

            if (pd.getCity() == null) {
                throw new RuntimeException("Please select city first");
            }

            Pincode pin = pincodeRepository
                    .findByIdAndCityId(dto.getPincodeId(), pd.getCity().getId())
                    .orElseThrow(() ->
                        new RuntimeException("Invalid pincode for selected city"));

            pd.setPincode(pin);
        }

        /* ================= ADDRESS ================= */

        pd.setSameAsCorrespondence(
            Boolean.TRUE.equals(dto.getSameAsCorrespondence())
        );

        if(Boolean.TRUE.equals(dto.getSameAsCorrespondence())){
            pd.setPermanentAddress(
                pd.getCorrespondenceAddress()
            );
        }else if(dto.getPermanentAddress()!=null){
            pd.setPermanentAddress(dto.getPermanentAddress());
        }
    }

    /* =====================================================
       ENTITY → DTO
       ===================================================== */
    private PersonalDetailsDTO mapToDTO(PersonalDetails pd) {

        PersonalDetailsDTO dto = new PersonalDetailsDTO();

        /* ===== BASIC INFO ===== */
        dto.setApplicationNo(pd.getApplicationNumber());
        dto.setFirstName(pd.getFirstName());
        dto.setMiddleName(pd.getMiddleName());
        dto.setLastName(pd.getLastName());
        dto.setGender(pd.getGender());
        dto.setDob(pd.getDob() != null ? pd.getDob().toString() : null);
        dto.setAadharCard(pd.getAadhaarCard());
        dto.setMobileNumber(pd.getMobileNumber());
        dto.setEmailAddress(pd.getEmail());

        /* ===== PERSONAL INFO ===== */
        dto.setFatherName(pd.getFatherName());
        dto.setMotherName(pd.getMotherName());
        dto.setNationality(pd.getNationality());
        dto.setCategory(pd.getCategory());
        dto.setMaritalStatus(pd.getMaritalStatus());
        dto.setDisability(pd.getDisability());

        /* ===== ADDRESS ===== */
        dto.setCorrespondenceAddress(pd.getCorrespondenceAddress());

        // ✅ COUNTRY
        if (pd.getCountry() != null) {
            dto.setCountryId(pd.getCountry().getId());
            dto.setCountryName(pd.getCountry().getName());
        }

        // ✅ STATE
        if (pd.getState() != null) {
            dto.setStateId(pd.getState().getId());
            dto.setStateName(pd.getState().getName());
        }

        // ✅ DISTRICT
        if (pd.getDistrict() != null) {
            dto.setDistrictId(pd.getDistrict().getId());
            dto.setDistrictName(pd.getDistrict().getName());
        }

        // ✅ CITY
        if (pd.getCity() != null) {
            dto.setCityId(pd.getCity().getId());
            dto.setCityName(pd.getCity().getName());
        }

        // ✅ PINCODE
        if (pd.getPincode() != null) {
            dto.setPincodeId(pd.getPincode().getId());
            dto.setPincode(pd.getPincode().getCode());
        }

        dto.setSameAsCorrespondence(pd.getSameAsCorrespondence());
        dto.setPermanentAddress(pd.getPermanentAddress());

        // ✅ Correction Window Flag
        dto.setReopenAllowed(
            pd.getReopenAllowed() != null && pd.getReopenAllowed()
        );

        return dto;
    }
}
