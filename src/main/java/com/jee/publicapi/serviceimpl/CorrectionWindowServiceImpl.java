package com.jee.publicapi.serviceimpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jee.publicapi.dto.SectionStatusDTO;
import com.jee.publicapi.entity.CorrectionWindow;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.repository.CorrectionWindowRepository;
import com.jee.publicapi.repository.DocumentDetailsRepository;
import com.jee.publicapi.repository.EducationDetailsRepository;
import com.jee.publicapi.repository.PersonalDetailsRepository;
import com.jee.publicapi.service.CorrectionWindowService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CorrectionWindowServiceImpl implements CorrectionWindowService {

	@Autowired
	private CorrectionWindowRepository windowRepo;

	@Autowired
	private PersonalDetailsRepository personalRepo;

	@Autowired
	private EducationDetailsRepository educationRepo;

	@Autowired
	private DocumentDetailsRepository documentRepo;

	/*
	 * ===================================================== ACTIVATE WINDOW (ADMIN)
	 * =====================================================
	 */
	@Override
	public void activateCorrectionWindow(CorrectionWindow window) {

		windowRepo.deactivateAllWindows();

		window.setActive(true);
		windowRepo.save(window);

		// ✅ Section wise unlock
		personalRepo.updateReopenAllowed(Boolean.TRUE.equals(window.getUnlockPersonal()));

		educationRepo.updateReopenAllowed(Boolean.TRUE.equals(window.getUnlockEducation()));

		documentRepo.updateReopenAllowed(Boolean.TRUE.equals(window.getUnlockDocuments()));
	}

	/*
	 * ===================================================== AUTO / MANUAL CLOSE
	 * =====================================================
	 */
	@Override
	public void deactivateAllWindows() {

		windowRepo.deactivateAllWindows();

		personalRepo.updateReopenAllowed(false);
		educationRepo.updateReopenAllowed(false);
		documentRepo.updateReopenAllowed(false);
	}

	/*
	 * ===================================================== ACTIVE WINDOW
	 * =====================================================
	 */
	@Override
	public CorrectionWindow getActiveWindow() {
		return windowRepo.findByActiveTrue();
	}

	/*
	 * ===================================================== GLOBAL FLAG
	 * =====================================================
	 */
	@Override
	public boolean isCorrectionActive() {

	    CorrectionWindow window = windowRepo.findByActiveTrue();

	    if (window == null) return false;

	    LocalDateTime now = LocalDateTime.now();

	    if (now.isBefore(window.getStartDateTime())) return false;

	    if (now.isAfter(window.getEndDateTime())) {
	        deactivateAllWindows();
	        return false;
	    }

	    return true;
	}

	/*
	 * ===================================================== SECTION CHECK (USED BY
	 * CONTROLLERS) =====================================================
	 */
	@Override
	public boolean isSectionEditable(User user, String section) {

	    CorrectionWindow window = windowRepo.findByActiveTrue();

	    if (window == null) return false;

	    LocalDateTime now = LocalDateTime.now();

	    if (now.isBefore(window.getStartDateTime()) ||
	        now.isAfter(window.getEndDateTime())) {
	        return false;
	    }

	    switch (section.toUpperCase()) {

	        case "PERSONAL":
	            return Boolean.TRUE.equals(window.getUnlockPersonal());

	        case "EDUCATION":
	            return Boolean.TRUE.equals(window.getUnlockEducation());

	        case "DOCUMENT":
	            return Boolean.TRUE.equals(window.getUnlockDocuments());

	        default:
	            return false;
	    }
	}
	/*
	 * ===================================================== DASHBOARD STATUS API
	 * =====================================================
	 */
	@Override
	public SectionStatusDTO getCurrentSectionStatus(User user) {

		SectionStatusDTO dto = new SectionStatusDTO();

		dto.setCorrectionActive(isCorrectionActive());

		dto.setPersonalEditable(isSectionEditable(user, "PERSONAL"));

		dto.setEducationEditable(isSectionEditable(user, "EDUCATION"));

		dto.setDocumentEditable(isSectionEditable(user, "DOCUMENT"));

		return dto;
	}
}