package com.jee.publicapi.basicwindow.serviceimpl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.jee.publicapi.basicwindow.dto.BasicCorrectionDetailsDTO;
import com.jee.publicapi.basicwindow.dto.BasicCorrectionUpdateDTO;
import com.jee.publicapi.basicwindow.service.BasicCorrectionService;
import com.jee.publicapi.basicwindow.service.BasicWindowService;
import com.jee.publicapi.correction.entity.UserCorrectionRequest;
import com.jee.publicapi.correction.enums.CorrectionStatus;
import com.jee.publicapi.correction.repository.UserCorrectionRequestRepository;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BasicCorrectionServiceImpl implements BasicCorrectionService {

    private final UserRepository userRepository;
    private final UserCorrectionRequestRepository requestRepository;
    private final BasicWindowService basicWindowService; // Injected to check window

    public BasicCorrectionServiceImpl(
            UserRepository userRepository,
            UserCorrectionRequestRepository requestRepository,
            BasicWindowService basicWindowService) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.basicWindowService = basicWindowService;
    }

    @Override
    public BasicCorrectionDetailsDTO getCorrectionDetails(Long requestId) {

        UserCorrectionRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Correction request not found"));

        User user = request.getUser();

        BasicCorrectionDetailsDTO dto = new BasicCorrectionDetailsDTO();

        dto.setRequestId(request.getId());
        dto.setFieldName(request.getFieldName());

        dto.setFirstName(user.getFirstName());
        dto.setMiddleName(user.getMiddleName());
        dto.setLastName(user.getLastName());

        dto.setGender(user.getGender());
        dto.setDay(user.getDay());
        dto.setMonth(user.getMonth());
        dto.setYear(user.getYear());
        dto.setAadharCard(user.getAadharCard());
        dto.setUserUpdatedAt(request.getUserUpdatedAt());   // ⭐ ADD THIS LINE
        dto.setCorrectionCompleted(request.isCorrectionCompleted()); // ⭐ ADD THIS
       
        // ✅ Add window info for frontend
        basicWindowService.getWindowByRequestId(requestId).ifPresent(window -> {
            dto.setWindowActive(basicWindowService.isWindowActiveForRequest(requestId));
            dto.setWindowStart(window.getStartDate());
            dto.setWindowEnd(window.getEndDate());
            dto.setDeActivatedByAdmin(window.getDeActivatedByAdmin() != null ? window.getDeActivatedByAdmin() : false);
            dto.setDeActivatedAt(window.getDeActivatedAt());
        });

        return dto;
    }

    @Override
    public void updateBasicDetails(Long requestId, BasicCorrectionUpdateDTO dto) {
        UserCorrectionRequest request = requestRepository.findById(requestId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
        

        if (request.getStatus() == CorrectionStatus.COMPLETED)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Correction already completed");
        
        if (request.getStatus() != CorrectionStatus.APPROVED)
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Correction not approved by admin");

		/*
		 * if (!basicWindowService.isWindowActiveForRequest(requestId)) throw new
		 * ResponseStatusException(HttpStatus.BAD_REQUEST, "Correction window closed");
		 */

        User user = request.getUser();
        switch (request.getFieldName()) {
            case "FIRST_NAME" -> user.setFirstName(dto.getFirstName());
            case "LAST_NAME" -> user.setLastName(dto.getLastName());
            case "DOB" -> {
                user.setDay(dto.getDay());
                user.setMonth(dto.getMonth());
                user.setYear(dto.getYear());
            }
            case "GENDER" -> user.setGender(dto.getGender());
            case "AADHAAR" -> user.setAadharCard(dto.getAadharCard());
        }
        
        userRepository.save(user);
	
	     // ✅ Mark correction completed
	     request.setStatus(CorrectionStatus.COMPLETED);
	     request.setCorrectionCompleted(true);
	     request.setUserUpdatedAt(LocalDateTime.now());
	
	     requestRepository.save(request);
	     System.out.println("Updating request ID: " + requestId);
	     System.out.println("Before update time: " + request.getUserUpdatedAt());
	
	     // ✅ Close correction window
	     basicWindowService.getWindowByRequestId(requestId).ifPresent(window -> {
	         window.setEndDate(LocalDateTime.now());
	         window.setDeActivatedByAdmin(true);
	         window.setDeActivatedAt(LocalDateTime.now());
	         basicWindowService.save(window);
	     });
        
    }
}