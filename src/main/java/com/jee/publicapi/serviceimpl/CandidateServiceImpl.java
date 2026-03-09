package com.jee.publicapi.serviceimpl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jee.publicapi.dto.AdminCandidateDTO;
import com.jee.publicapi.entity.Candidate;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.repository.CandidateRepository;
import com.jee.publicapi.repository.UserRepository;
import com.jee.publicapi.service.CandidateService;

@Service
public class CandidateServiceImpl implements CandidateService {

	private final CandidateRepository candidateRepository;
	private final UserRepository userRepository;

	
	public CandidateServiceImpl(CandidateRepository candidateRepository, UserRepository userRepository) {
		this.candidateRepository = candidateRepository;
		this.userRepository = userRepository;
	}

	/* ==================== GET ALL CANDIDATES AS DTO ==================== */
	@Override
	public List<AdminCandidateDTO> getAllCandidates() {

		return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	/* ==================== GET CANDIDATES BY STATUS ==================== */
	@Override
	public List<Candidate> getCandidatesByStatus(String status) {

		if (status == null || status.isBlank()) {
			return Collections.emptyList();
		}

		FormStatus statusEnum = FormStatus.fromString(status);

		if (statusEnum == FormStatus.UNKNOWN) {
			return Collections.emptyList();
		}

		return candidateRepository.findByStatus(statusEnum.name());
	}

	/* ==================== HELPER: CONVERT USER → DTO ==================== */
	private AdminCandidateDTO convertToDTO(User user) {

		AdminCandidateDTO dto = new AdminCandidateDTO();

		dto.setUserId(user.getId());
		dto.setApplicationNo(user.getApplicationNumber() != null ? user.getApplicationNumber().toString() : null);

		dto.setFirstName(user.getFirstName());
		dto.setMiddleName(user.getMiddleName());
		dto.setLastName(user.getLastName());

		dto.setFullName(user.getFirstName() + " " + (user.getMiddleName() != null ? user.getMiddleName() + " " : "")
				+ user.getLastName());

		dto.setEmail(user.getEmail());
		dto.setMobileNumber(user.getMobileNumber());
		dto.setEnabled(user.isEnabled());

		dto.setPersonalStatus(user.getPersonalStatus() != null ? user.getPersonalStatus().name() : null);

		dto.setEducationStatus(user.getEducationStatus() != null ? user.getEducationStatus().name() : null);

		dto.setDocumentStatus(user.getDocumentStatus() != null ? user.getDocumentStatus().name() : null);

		dto.setPaymentStatus(user.getPaymentStatus() != null ? user.getPaymentStatus().name() : null);

		return dto;
	}
}