package com.jee.publicapi.service;

import com.jee.publicapi.dto.SectionStatusDTO;
import com.jee.publicapi.entity.CorrectionWindow;
import com.jee.publicapi.entity.User;

public interface CorrectionWindowService {

	 /* ADMIN CONTROL */
    void activateCorrectionWindow(CorrectionWindow window);

    void deactivateAllWindows();

    CorrectionWindow getActiveWindow();

    boolean isCorrectionActive();

    /* USER SIDE CHECK */
    boolean isSectionEditable(User user, String section);

    /* DASHBOARD STATUS */
    SectionStatusDTO getCurrentSectionStatus(User user);
}
