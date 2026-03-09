package com.jee.publicapi.basicwindow.serviceimpl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jee.publicapi.basicwindow.entity.BasicWindow;
import com.jee.publicapi.basicwindow.repository.BasicWindowRepository;
import com.jee.publicapi.basicwindow.service.BasicWindowService;
import com.jee.publicapi.correction.entity.UserCorrectionRequest;
import com.jee.publicapi.correction.enums.CorrectionStatus;
import com.jee.publicapi.correction.repository.UserCorrectionRequestRepository;

@Service
public class BasicWindowServiceImpl implements BasicWindowService {

    private final BasicWindowRepository windowRepository;
    private final UserCorrectionRequestRepository correctionRepository;

    public BasicWindowServiceImpl(
            BasicWindowRepository windowRepository,
            UserCorrectionRequestRepository correctionRepository) {

        this.windowRepository = windowRepository;
        this.correctionRepository = correctionRepository;
    }

    // ===============================
    // OPEN WINDOW
    // ===============================
    @Override
    public void activeWindowForRequest(Long requestId) {
        System.out.println("===== ACTIVATE WINDOW REQUEST =====");
        System.out.println("Request ID: " + requestId);

        // 1️⃣ Fetch the correction request
        UserCorrectionRequest request = correctionRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        System.out.println("Request Status: " + request.getStatus());

        // 2️⃣ Only APPROVED requests can activate a window
        if (request.getStatus() != CorrectionStatus.APPROVED) {
            System.out.println("ERROR: Request not approved");
            throw new RuntimeException("Only APPROVED request can open window");
        }

        // 3️⃣ Check if a window already exists for this request
        BasicWindow existingWindow = windowRepository.findByRequest_Id(requestId).orElse(null);

        if (existingWindow != null) {
            System.out.println("Existing window found");
            System.out.println("Window Active: " + existingWindow.isActive());
            System.out.println("Window Deactivated By Admin: " + existingWindow.getDeActivatedByAdmin());
            System.out.println("Window End: " + existingWindow.getEndDate());

            // 3a. If the window is active and not expired, do nothing
            if (existingWindow.isActive() &&
                !Boolean.TRUE.equals(existingWindow.getDeActivatedByAdmin()) &&
                existingWindow.getEndDate().isAfter(LocalDateTime.now())) {

                System.out.println("Window already active and valid. No action needed.");
                return;
            }

            // 3b. If window expired or deactivated by admin, reactivate
            System.out.println("Reactivating window (expired or deactivated by admin)");

            existingWindow.setStartDate(LocalDateTime.now());
            existingWindow.setEndDate(LocalDateTime.now().plusHours(24));
            existingWindow.setActive(true);
            existingWindow.setDeActivatedByAdmin(false);
            existingWindow.setDeActivatedAt(null);

            windowRepository.save(existingWindow);

            System.out.println("Window restarted successfully");
            System.out.println("New Start: " + existingWindow.getStartDate());
            System.out.println("New End: " + existingWindow.getEndDate());
            return;
        }

        // 4️⃣ If no existing window, create a new one
        System.out.println("No existing window. Creating new window");

        BasicWindow newWindow = new BasicWindow();
        newWindow.setRequest(request);
        newWindow.setStartDate(LocalDateTime.now());
        newWindow.setEndDate(LocalDateTime.now().plusHours(24));
        newWindow.setActive(true);
        newWindow.setDeActivatedByAdmin(false);

        windowRepository.save(newWindow);

        System.out.println("Window created successfully");
        System.out.println("Start Date: " + newWindow.getStartDate());
        System.out.println("End Date: " + newWindow.getEndDate());
    }

    


    // ===============================
    // GET WINDOW
    // ===============================

    @Override
    public Optional<BasicWindow> getWindowByRequestId(Long requestId) {

        System.out.println("Fetching window for request ID: " + requestId);

        Optional<BasicWindow> window =
                windowRepository.findByRequest_Id(requestId);

        if (window.isPresent()) {

            System.out.println("Window FOUND in DB");
            System.out.println("Start: " + window.get().getStartDate());
            System.out.println("End: " + window.get().getEndDate());

        } else {

            System.out.println("Window NOT FOUND in DB for request: " + requestId);
        }

        return window;
    }
    
    
    
    // ===============================
    // DEACTIVATE WINDOW
    // ===============================
    @Override
    public void deactivateWindow(Long requestId) {

        System.out.println("Manual deactivate window request: " + requestId);

        BasicWindow window =
                windowRepository.findByRequest_Id(requestId)
                .orElseThrow(() -> new RuntimeException("Window not found"));

        System.out.println("Window before deactivate: Active=" + window.isActive());

        window.setActive(false);
        window.setDeActivatedAt(LocalDateTime.now());   // 🔹 store deactivated time
        window.setDeActivatedByAdmin(true);            // 🔹 mark as admin deactivated

        windowRepository.save(window);
        System.out.println("Window deactivated successfully by admin at " + window.getDeActivatedAt());
    }

    @Override
    public boolean isWindowActiveForRequest(Long requestId) {

        Optional<BasicWindow> windowOpt = windowRepository.findByRequest_Id(requestId);

        if (windowOpt.isEmpty()) {
            System.out.println("Window not found for request " + requestId);
            return false;
        }

        BasicWindow window = windowOpt.get();
        LocalDateTime now = LocalDateTime.now();

        // 🔴 Admin deactivation check
        if (Boolean.TRUE.equals(window.getDeActivatedByAdmin())) {
            System.out.println("Window manually deactivated by admin for request " + requestId);
            return false;
        }

        // 🔴 Check if window has started
        if (window.getStartDate() != null && now.isBefore(window.getStartDate())) {
            System.out.println("Window has not started yet for request " + requestId);
            return false;
        }

        // 🔴 Auto-expire check
        if (window.getEndDate() != null && now.isAfter(window.getEndDate())) {
            if (window.isActive()) {
                window.setActive(false);
                windowRepository.save(window);
                System.out.println("Window expired automatically for request " + requestId);
            }
            return false;
        }

        // 🔴 Return active only if window is active
        return Boolean.TRUE.equals(window.isActive());
    }

	@Override
	public void save(BasicWindow window) {
		 windowRepository.save(window);
		
	}
}