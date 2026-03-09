package com.jee.publicapi.scheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jee.publicapi.entity.CorrectionWindow;
import com.jee.publicapi.service.CorrectionWindowService;

@Component
public class CorrectionWindowScheduler {

    @Autowired
    private CorrectionWindowService correctionService;

    /* =====================================
       RUN EVERY 1 MINUTE
       ===================================== */
    @Scheduled(fixedRate = 60000)
    public void autoCloseWindow() {

        CorrectionWindow window =
                correctionService.getActiveWindow();

        if (window == null)
            return;

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(window.getEndDateTime())) {

            System.out.println(
                "Correction window expired → Auto closing");

            correctionService.deactivateAllWindows();
        }
    }
}