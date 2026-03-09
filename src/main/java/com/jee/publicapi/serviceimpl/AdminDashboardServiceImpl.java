package com.jee.publicapi.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jee.publicapi.dto.AdminDashboardStatsDTO;
import com.jee.publicapi.dto.CandidateFullDetailsDTO;
import com.jee.publicapi.dto.DocumentDetailsDTO;
import com.jee.publicapi.dto.EducationDetailsDTO;
import com.jee.publicapi.dto.PersonalDetailsDTO;
import com.jee.publicapi.dto.UserBasicDTO;
import com.jee.publicapi.entity.DocumentDetails;
import com.jee.publicapi.entity.EducationDetails;
import com.jee.publicapi.entity.PersonalDetails;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.repository.CandidateRepository;
import com.jee.publicapi.repository.DocumentDetailsRepository;
import com.jee.publicapi.repository.EducationDetailsRepository;
import com.jee.publicapi.repository.PersonalDetailsRepository;
import com.jee.publicapi.repository.UserRepository;
import com.jee.publicapi.service.AdminDashboardService;
import com.jee.publicapi.storage.FileStorageService;

@Service
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final FileStorageService fileStorageService;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final PersonalDetailsRepository personalRepository;
    private final EducationDetailsRepository educationRepository;
    private final DocumentDetailsRepository documentRepository;

    public AdminDashboardServiceImpl(
            CandidateRepository candidateRepository,
            UserRepository userRepository,
            PersonalDetailsRepository personalRepository,
            EducationDetailsRepository educationRepository,
            DocumentDetailsRepository documentRepository,
            FileStorageService fileStorageService
    ) {
        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
        this.personalRepository = personalRepository;
        this.educationRepository = educationRepository;
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
    }

    // ================= DASHBOARD STATS =================
    @Override
    public AdminDashboardStatsDTO getDashboardStats() {
        AdminDashboardStatsDTO dto = new AdminDashboardStatsDTO();
        dto.setTotalCandidates(userRepository.count());
        dto.setSubmittedApplications(candidateRepository.countByStatus(FormStatus.COMPLETED.name()));
        dto.setPendingReviews(candidateRepository.countByStatus(FormStatus.IN_PROGRESS.name()));
        dto.setUpcomingExams(0);
        return dto;
    }

    // ================= FULL CANDIDATE DETAILS =================
    @Override
    public CandidateFullDetailsDTO getCandidateFullDetails(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Fetch entities
        PersonalDetails personal =
        	    personalRepository.findByUserIdWithLocation(user.getId()).orElse(null);
        EducationDetails education = educationRepository.findByUser(user).orElse(null);
        DocumentDetails documents = documentRepository.findByUser(user).orElse(null);

     // ===== MAP PERSONAL DETAILS =====
        PersonalDetailsDTO personalDTO = null;

        if (personal != null) {
            personalDTO = new PersonalDetailsDTO();
            personalDTO.setApplicationNo(personal.getApplicationNumber());
            personalDTO.setFirstName(personal.getFirstName());
            personalDTO.setMiddleName(personal.getMiddleName());
            personalDTO.setLastName(personal.getLastName());
            personalDTO.setGender(personal.getGender());
            personalDTO.setDob(
                personal.getDob() != null ? personal.getDob().toString() : null
            );
            personalDTO.setAadharCard(personal.getAadhaarCard());
            personalDTO.setMobileNumber(personal.getMobileNumber());
            personalDTO.setEmailAddress(personal.getEmail());
            personalDTO.setFatherName(personal.getFatherName());
            personalDTO.setMotherName(personal.getMotherName());
            personalDTO.setNationality(personal.getNationality());
            personalDTO.setCategory(personal.getCategory());
            personalDTO.setMaritalStatus(personal.getMaritalStatus());
            personalDTO.setDisability(personal.getDisability());
            personalDTO.setPersonalStatus(user.getPersonalStatus());
            personalDTO.setCorrespondenceAddress(personal.getCorrespondenceAddress());
            
         // ===== COUNTRY =====
            if(personal.getCountry()!=null){
                personalDTO.setCountryName(
                    personal.getCountry().getName());
                personalDTO.setCountryId(
                    personal.getCountry().getId());
            }

            // ===== STATE =====
            if(personal.getState()!=null){
                personalDTO.setStateName(
                    personal.getState().getName());
                personalDTO.setStateId(
                    personal.getState().getId());
            }

            // ===== DISTRICT =====
            if(personal.getDistrict()!=null){
                personalDTO.setDistrictName(
                    personal.getDistrict().getName());
                personalDTO.setDistrictId(
                    personal.getDistrict().getId());
            }

            // ===== CITY =====
            if(personal.getCity()!=null){
                personalDTO.setCityName(
                    personal.getCity().getName());
                personalDTO.setCityId(
                    personal.getCity().getId());
            }

            // ===== PINCODE =====
            if(personal.getPincode()!=null){
                personalDTO.setPincode(
                    personal.getPincode().getCode());
                personalDTO.setPincodeId(
                    personal.getPincode().getId());
            }
            
            
            personalDTO.setSameAsCorrespondence(personal.getSameAsCorrespondence());
            personalDTO.setPermanentAddress(personal.getPermanentAddress());
        }

        // ===== MAP EDUCATION DETAILS =====
        EducationDetailsDTO educationDTO = null;

        if (education != null) {
            educationDTO = new EducationDetailsDTO();

            educationDTO.setBoard10(education.getBoard10());
            educationDTO.setSchoolName10(education.getSchoolName10());
            educationDTO.setRollNumber10(education.getRollNumber10());
            educationDTO.setPassingYear10(education.getPassingYear10());
            educationDTO.setMarksType10(education.getMarksType10());
            educationDTO.setPercentage10(education.getPercentage10());

            educationDTO.setBoard12(education.getBoard12());
            educationDTO.setSchoolName12(education.getSchoolName12());
            educationDTO.setRollNumber12(education.getRollNumber12());
            educationDTO.setPassingYear12(education.getPassingYear12());
            educationDTO.setStream12(education.getStream12());
            educationDTO.setMarksType12(education.getMarksType12());
            educationDTO.setPercentage12(education.getPercentage12());
            educationDTO.setPcmPercentage(education.getPcmPercentage());

            educationDTO.setExamStatus(education.getExamStatus());
            educationDTO.setAppearingYear(education.getAppearingYear());

            educationDTO.setEducationStatus(education.getEducationStatus());

            educationDTO.setReopenAllowed(
                    education.getReopenAllowed() != null
                            ? education.getReopenAllowed()
                            : false
            );
        }
        // ===== MAP DOCUMENTS =====
        DocumentDetailsDTO docDTO = null;

        if (documents != null) {
            docDTO = new DocumentDetailsDTO();
            docDTO.setPhoto(documents.getPhoto() != null ?
                    fileStorageService.getFileUrl("user_" + user.getApplicationNumber(), "photo", documents.getPhoto())
                    : null);
            docDTO.setSignature(documents.getSignature() != null ?
                    fileStorageService.getFileUrl("user_" + user.getApplicationNumber(), "signature", documents.getSignature())
                    : null);
            docDTO.setMarksheet(documents.getMarksheet() != null ?
                    fileStorageService.getFileUrl("user_" + user.getApplicationNumber(), "marksheet", documents.getMarksheet())
                    : null);

            docDTO.setUploadStatus(documents.getUploadStatus());
            docDTO.setDocumentStatus(documents.getDocumentStatus());
        }
        
        
		// ===== BUILD DTO =====
		CandidateFullDetailsDTO dto = new CandidateFullDetailsDTO();

		String fullName = user.getFirstName() + " " + (user.getMiddleName() != null ? user.getMiddleName() + " " : "")
				+ user.getLastName();

		UserBasicDTO userDTO = new UserBasicDTO(user.getId(), String.valueOf(user.getApplicationNumber()),
				user.getEmail(), fullName.trim(), user.getRole());

		dto.setUser(userDTO);
		dto.setPersonalDetails(personalDTO);
		dto.setEducationDetails(educationDTO);
		dto.setDocumentDetails(docDTO);

		dto.setPersonalStatus(user.getPersonalStatus() != null ? user.getPersonalStatus().name() : "NOT_STARTED");

		dto.setEducationStatus(user.getEducationStatus() != null ? user.getEducationStatus().name() : "NOT_STARTED");

		dto.setDocumentStatus(user.getDocumentStatus() != null ? user.getDocumentStatus().name() : "NOT_STARTED");

		return dto;

	}
}