package com.jee.publicapi.service;

import com.jee.publicapi.dto.AdminCandidateDTO;
import com.jee.publicapi.entity.Candidate;
import java.util.List;

public interface CandidateService {

    List<AdminCandidateDTO> getAllCandidates();

    List<Candidate> getCandidatesByStatus(String status);
}