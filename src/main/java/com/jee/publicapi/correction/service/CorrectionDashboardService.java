package com.jee.publicapi.correction.service;

import com.jee.publicapi.correction.dto.CorrectionDashboardDTO;

public interface CorrectionDashboardService {

	CorrectionDashboardDTO getDashboardStatus(Long userId);
}
