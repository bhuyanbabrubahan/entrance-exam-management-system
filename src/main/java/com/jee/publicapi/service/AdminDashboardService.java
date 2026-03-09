package com.jee.publicapi.service;

import com.jee.publicapi.dto.AdminDashboardStatsDTO;
import com.jee.publicapi.dto.CandidateFullDetailsDTO;

public interface AdminDashboardService {
	AdminDashboardStatsDTO getDashboardStats();

	CandidateFullDetailsDTO getCandidateFullDetails(Long id);

}