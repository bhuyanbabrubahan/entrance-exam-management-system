package com.jee.publicapi.correction.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jee.publicapi.basicwindow.entity.BasicWindow;
import com.jee.publicapi.basicwindow.service.BasicWindowService;
import com.jee.publicapi.correction.dto.CorrectionHistoryDTO;
import com.jee.publicapi.correction.entity.UserCorrectionDocument;
import com.jee.publicapi.correction.entity.UserCorrectionRequest;
import com.jee.publicapi.correction.enums.CorrectionStatus;
import com.jee.publicapi.correction.repository.UserCorrectionDocumentRepository;
import com.jee.publicapi.correction.repository.UserCorrectionRequestRepository;
import com.jee.publicapi.correction.service.UserCorrectionRequestService;
import com.jee.publicapi.entity.PersonalDetails;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.repository.PersonalDetailsRepository;
import com.jee.publicapi.repository.UserRepository;
import com.jee.publicapi.storage.FileStorageService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserCorrectionRequestServiceImpl
        implements UserCorrectionRequestService {

    private final UserRepository userRepository;
    private final UserCorrectionRequestRepository userCorrectionRequestRepository;
    private final UserCorrectionDocumentRepository documentRepo;
    private final PersonalDetailsRepository personalRepo;
    private final FileStorageService fileStorageService;
    private final BasicWindowService basicWindowService;

    
    

    public UserCorrectionRequestServiceImpl(
            UserRepository userRepository,
            UserCorrectionRequestRepository userCorrectionRequestRepository,
            UserCorrectionDocumentRepository documentRepo,
            PersonalDetailsRepository personalRepo,
            FileStorageService fileStorageService,
            BasicWindowService basicWindowService) {

        this.userRepository = userRepository;
        this.userCorrectionRequestRepository = userCorrectionRequestRepository;
        this.documentRepo = documentRepo;
        this.personalRepo = personalRepo;
        this.fileStorageService = fileStorageService;
        this.basicWindowService = basicWindowService;
    }
	/*
	 * ===================================================== SUBMIT CORRECTION
	 * =====================================================
	 */

    
    @Override
    public UserCorrectionRequest submitCorrection(Long userId, String fieldName, String requestedValue, String reason, List<MultipartFile> files) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // 1️⃣ Get old value
        String oldValue = fetchOldValue(user, fieldName);

        // 2️⃣ Check previous history & attempts
        List<UserCorrectionRequest> history = userCorrectionRequestRepository.findFieldHistory(userId, fieldName);
        int attempts = history.size();

        boolean alreadyCompleted = userCorrectionRequestRepository.existsByUserAndFieldNameAndStatus(user, fieldName, CorrectionStatus.COMPLETED);
        if (alreadyCompleted) throw new RuntimeException("Correction already completed");

        if (!history.isEmpty()) {
            UserCorrectionRequest latest = history.get(0);
            switch (latest.getStatus()) {
                case REQUESTED -> throw new RuntimeException("Field already requested and pending approval");
                case APPROVED -> {
                    Optional<BasicWindow> windowOpt = basicWindowService.getWindowByRequestId(latest.getId());
                    if (windowOpt.isPresent()) {
                        BasicWindow window = windowOpt.get();
                        if (Boolean.TRUE.equals(window.getDeActivatedByAdmin())) throw new RuntimeException("Correction window blocked by admin");
                        if (basicWindowService.isWindowActiveForRequest(latest.getId())) throw new RuntimeException("Window already active, go update your field");
                        else throw new RuntimeException("Correction window already submitted");
                    } else throw new RuntimeException("Correction window not found for this request");
                }
                case REJECTED -> { if (attempts >= 3) throw new RuntimeException("Maximum 3 attempts reached"); }
                default -> {}
            }
        }

        // 3️⃣ Create new request
        UserCorrectionRequest request = new UserCorrectionRequest();
        request.setUser(user);
        request.setApplicationNumber(String.valueOf(user.getApplicationNumber()));
        request.setFieldName(fieldName);
        request.setOldValue(oldValue);
        request.setRequestedValue(requestedValue);
        request.setReason(reason);
        request.setAttemptNumber(attempts + 1);
        request.setStatus(CorrectionStatus.REQUESTED);
        request.setRequestedAt(LocalDateTime.now());
        userCorrectionRequestRepository.save(request);

        // 7️⃣ Upload documents if provided
        if (files != null && !files.isEmpty()) {
            String userFolder = "user_" + user.getApplicationNumber();
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) continue;

                try {
                    String storedName = fileStorageService.storeFile(file, userFolder, "correction");
                    String fileUrl = fileStorageService.getFileUrl(userFolder, "correction", storedName, true);

                    UserCorrectionDocument doc = new UserCorrectionDocument();
                    doc.setCorrectionRequest(request);
                    doc.setFileName(storedName);
                    doc.setFilePath(fileUrl);

                    documentRepo.save(doc);
                } catch (Exception e) {
                    throw new RuntimeException("File upload failed: " + e.getMessage());
                }
            }
        }

        return request;
    }
    
    
    
    
    /* =====================================================
       FETCH OLD VALUE
    ===================================================== */

    private String fetchOldValue(
            User user,
            String fieldName) {

        PersonalDetails pd =
                personalRepo.findByUser(user)
                        .orElse(null);

        switch (fieldName.toUpperCase()) {

            case "AADHAAR":
                return user.getAadharCard();

            case "FIRST_NAME":
                return pd != null ?
                        pd.getFirstName() : "";

            case "LAST_NAME":
                return pd != null ?
                        pd.getLastName() : "";

            case "DOB":
                return pd != null && pd.getDob()!=null ?
                        pd.getDob().toString() : "";

            case "GENDER":
                return pd != null ?
                        pd.getGender() : "";

            default:
                return "";
        }
    }

    /* =====================================================
                   USER HISTORY
    ===================================================== */
    @Override
    public List<CorrectionHistoryDTO> getMyRequests(String email) {

        System.out.println("===== FETCH USER CORRECTION HISTORY =====");
        System.out.println("User Email: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("User ID: " + user.getId());

        List<UserCorrectionRequest> requests =
                userCorrectionRequestRepository
                        .findByUserIdWithDocuments(user.getId());

        System.out.println("Total Requests Found: " + requests.size());

        return requests.stream().map(req -> {

            System.out.println("Processing Request ID: " + req.getId());

            CorrectionHistoryDTO dto = new CorrectionHistoryDTO();

            dto.setId(req.getId());
            dto.setFieldName(req.getFieldName());
            dto.setOldValue(req.getOldValue());
            dto.setRequestedValue(req.getRequestedValue());
            dto.setReason(req.getReason());

            dto.setStatus(req.getStatus().name());
            dto.setAdminRemark(req.getAdminRemark());

            dto.setRequestedAt(req.getRequestedAt());
            dto.setReviewedAt(req.getReviewedAt());

            dto.setAttemptNumber(req.getAttemptNumber());
            dto.setCorrectionCompleted(req.isCorrectionCompleted());
            dto.setUserUpdatedAt(req.getUserUpdatedAt());

            /* ================= WINDOW ================= */

            Optional<BasicWindow> windowOpt = basicWindowService.getWindowByRequestId(req.getId());

            if (windowOpt.isPresent()) {
                BasicWindow window = windowOpt.get();

                boolean active = basicWindowService.isWindowActiveForRequest(req.getId());

                // ✅ FIX: also check start date for future windows
                if (window.getStartDate() != null && LocalDateTime.now().isBefore(window.getStartDate())) {
                    active = false; // window not started yet
                }

                dto.setWindowActive(active);
                dto.setWindowStart(window.getStartDate());
                dto.setWindowEnd(window.getEndDate());
                dto.setDeActivatedByAdmin(Boolean.TRUE.equals(window.getDeActivatedByAdmin()));
                dto.setDeActivatedAt(window.getDeActivatedAt());
            } else {
                dto.setWindowActive(false);
            }
            /* ================= DOCUMENTS ================= */

            if (req.getDocuments() != null) {

                List<String> docs = req.getDocuments()
                        .stream()
                        .map(UserCorrectionDocument::getFilePath)
                        .toList();

                dto.setDocumentPaths(docs);
            }

            return dto;

        }).toList();
    }

    

    /* =====================================================
       APPROVE
    ===================================================== */

    @Override
    public void approveRequest(
            Long requestId,
            Long adminId,
            String remark) {

    	UserCorrectionRequest request =
                userCorrectionRequestRepository.findById(requestId)
                        .orElseThrow(() -> new RuntimeException("Request not found"));
    	

        applyCorrection(
        		request,
                request.getUser(),
                request.getFieldName(),
                request.getRequestedValue());

        request.setStatus(CorrectionStatus.APPROVED);
        request.setReviewedByAdminId(adminId);
        request.setAdminRemark(remark);
        request.setReviewedAt(LocalDateTime.now());

        
        
        userCorrectionRequestRepository.save(request);
    }

    
    public void markCorrectionCompleted(Long requestId) {

        UserCorrectionRequest request =
            userCorrectionRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(CorrectionStatus.COMPLETED);
        request.setCorrectionCompleted(true);
        request.setUserUpdatedAt(LocalDateTime.now());

        userCorrectionRequestRepository.save(request);

        /* 🔥 CLOSE WINDOW AFTER COMPLETION */
        Optional<BasicWindow> windowOpt = basicWindowService.getWindowByRequestId(requestId);

        if (windowOpt.isPresent()) {
            BasicWindow window = windowOpt.get();

            window.setEndDate(LocalDateTime.now());   // immediately expire window
            window.setDeActivatedByAdmin(true);
            window.setDeActivatedAt(LocalDateTime.now());

            basicWindowService.save(window);
        }
    }
    
    /* =====================================================
       APPLY REAL UPDATE
    ===================================================== */

    private void applyCorrection(
            UserCorrectionRequest request,
            User user,
            String field,
            String value) {

        PersonalDetails pd =
                personalRepo.findByUser(user)
                        .orElse(null);

        switch (field.toUpperCase()) {

            case "AADHAAR":
                user.setAadharCard(value);
                userRepository.save(user);
                break;

            case "FIRST_NAME":
                if (pd != null) {
                    pd.setFirstName(value);
                    personalRepo.save(pd);
                }
                break;

            case "LAST_NAME":
                if (pd != null) {
                    pd.setLastName(value);
                    personalRepo.save(pd);
                }
                break;

            case "DOB":
                if (pd != null) {
                    try {

                        if (value == null || value.trim().isEmpty()) {
                            throw new RuntimeException("DOB value is empty");
                        }

                        value = value.trim();   // remove spaces

                        java.time.LocalDate parsedDate;

                        // ✅ FIX 1: Handle ddMMyyyy (25051998)
                        if (value.matches("\\d{8}")) {

                            java.time.format.DateTimeFormatter formatter =
                                    java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy");

                            parsedDate = java.time.LocalDate.parse(value, formatter);

                        }
                        // ✅ FIX 2: Handle yyyy-MM-dd (1998-05-25)
                        else if (value.matches("\\d{4}-\\d{2}-\\d{2}")) {

                            parsedDate = java.time.LocalDate.parse(value);

                        }
                        else {
                            throw new RuntimeException("Invalid DOB format: " + value);
                        }

                        pd.setDob(parsedDate);
                        personalRepo.save(pd);

                    } catch (Exception e) {
                        throw new RuntimeException("DOB parsing failed: " + e.getMessage());
                    }
                }
                break;

            case "GENDER":
                if (pd != null) {
                    pd.setGender(value);
                    personalRepo.save(pd);
                }
                break;
        }
    }

    /* =====================================================
       REJECT
    ===================================================== */

    @Override
    public void rejectRequest(
            Long requestId,
            Long adminId,
            String remark) {

        UserCorrectionRequest request =
        		userCorrectionRequestRepository.findById(requestId)
                        .orElseThrow();
        request.setStatus(CorrectionStatus.REJECTED);
        request.setReviewedByAdminId(adminId);
        request.setAdminRemark(remark);
        request.setReviewedAt(LocalDateTime.now());

        userCorrectionRequestRepository.save(request);
    }

    @Override
    public boolean isSectionEditable(User user, String fieldName) {

        boolean alreadyRequested = userCorrectionRequestRepository
                .existsByUserIdAndFieldNameAndStatusNot(
                        user.getId(),
                        fieldName,
                        CorrectionStatus.REJECTED
                );

        if (alreadyRequested) {
            return false;
        }

        return true;
    }
    

    @Override
    public List<UserCorrectionRequest> findByUserIdOrderByRequestedAtDesc(Long userId) {
        return userCorrectionRequestRepository
                .findByUserIdWithDocuments(userId);
    }

    @Override
    public boolean existsByUserIdAndFieldNameAndStatusNot(Long userId, String fieldName, CorrectionStatus status) {
        return userCorrectionRequestRepository
                .existsByUserIdAndFieldNameAndStatusNot(userId, fieldName, status);
    }

	@Override
	public List<UserCorrectionRequest> getUserHistory(Long userId) {
	    return userCorrectionRequestRepository
	            .findByUserIdWithDocuments(userId);
	}
    
	@Override
	public List<UserCorrectionRequest> getFieldHistory(Long userId, String fieldName) {
	    return userCorrectionRequestRepository.findFieldHistory(userId, fieldName);
	}

	
	public void validateWindowBeforeUpdate(Long requestId) {

	    if (!basicWindowService.isWindowActiveForRequest(requestId)) {
	        throw new RuntimeException("Basic details update window is closed");
	    }
	}
	
	
}