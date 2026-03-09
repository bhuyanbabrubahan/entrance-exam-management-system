package com.jee.publicapi.basicwindow.service;

import java.util.Optional;

import com.jee.publicapi.basicwindow.entity.BasicWindow;

public interface BasicWindowService {
	
	 // ADMIN - ACTIVATE WINDOW
    void activeWindowForRequest(Long requestId);

    // ADMIN - DEACTIVATE WINDOW
    void deactivateWindow(Long requestId);

    // USER - CHECK WINDOW
    boolean isWindowActiveForRequest(Long requestId);
    
    
    Optional<BasicWindow> getWindowByRequestId(Long requestId);
    void save(BasicWindow window);
}
