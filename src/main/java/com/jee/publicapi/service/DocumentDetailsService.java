package com.jee.publicapi.service;

import org.springframework.web.multipart.MultipartFile;

import com.jee.publicapi.dto.DocumentDetailsDTO;
import com.jee.publicapi.entity.DocumentDetails;
import com.jee.publicapi.entity.User;

public interface DocumentDetailsService {

    /* ===== FETCH CURRENT ===== */
    DocumentDetailsDTO getCurrent(User user);

    /* ===== SAVE DRAFT WITH FILES ===== */
    void saveDraft(User user, MultipartFile photo, MultipartFile signature, MultipartFile marksheet);

    /* ===== SUBMIT (LOCK) ===== */
    void submit(User user);

    /* ===== INTERNAL USE ===== */
    DocumentDetails getEntityByUser(User user);
}
