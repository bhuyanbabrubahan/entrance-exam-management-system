package com.jee.publicapi.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jee.publicapi.dto.DocumentDetailsDTO;
import com.jee.publicapi.entity.DocumentDetails;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.enums.UploadStatus;
import com.jee.publicapi.repository.DocumentDetailsRepository;
import com.jee.publicapi.repository.UserRepository;
import com.jee.publicapi.service.CorrectionWindowService;
import com.jee.publicapi.service.DocumentDetailsService;
import com.jee.publicapi.storage.FileStorageService;
import org.springframework.http.MediaType;
@RestController
@RequestMapping("/api/user/document-details")
public class DocumentDetailsController {

    private final UserRepository userRepository;
    private final DocumentDetailsRepository documentRepository;
    private final DocumentDetailsService documentService;
    private final FileStorageService fileStorageService;
    private final CorrectionWindowService correctionService;

    public DocumentDetailsController(UserRepository userRepository,
                                     DocumentDetailsRepository documentRepository,
                                     DocumentDetailsService documentService,
                                     FileStorageService fileStorageService,
                                     CorrectionWindowService correctionService) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        this.fileStorageService = fileStorageService;
        this.correctionService = correctionService;
    }

    /* =====================================================
       GET CURRENT DOCUMENT DETAILS
       ===================================================== */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrent() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DocumentDetails doc = documentRepository.findByUser(user).orElse(null);

        // ✅ First time open → create record (NOT_STARTED)
        if (doc == null) {
            doc = new DocumentDetails();
            doc.setUser(user);
            doc.setCreatedAt(LocalDateTime.now());
            doc.setDocumentStatus(FormStatus.NOT_STARTED);
            doc.setUploadStatus(UploadStatus.PENDING);
            documentRepository.save(doc);
        }

        DocumentDetailsDTO dto = new DocumentDetailsDTO();
        BeanUtils.copyProperties(doc, dto);

        // 🔹 Convert filenames to HTTP URLs
        String baseUrl = "http://localhost:8080/files/user_" + user.getApplicationNumber() + "/";

        if (doc.getPhoto() != null && !doc.getPhoto().isEmpty()) {
            dto.setPhoto(baseUrl + "photo/" + doc.getPhoto());
        }

        if (doc.getSignature() != null && !doc.getSignature().isEmpty()) {
            dto.setSignature(baseUrl + "signature/" + doc.getSignature());
        }

        if (doc.getMarksheet() != null && !doc.getMarksheet().isEmpty()) {
            dto.setMarksheet(baseUrl + "marksheet/" + doc.getMarksheet());
        }

        Map<String, Object> res = new HashMap<>();
        res.put("status", "SUCCESS");
        res.put("documents", dto);
        res.put("documentStatus", doc.getDocumentStatus());

        System.out.println("===== GET CURRENT DOCUMENTS =====");
        System.out.println("User: " + user.getEmail());
        System.out.println("Photo URL: " + dto.getPhoto());
        System.out.println("Signature URL: " + dto.getSignature());
        System.out.println("Marksheet URL: " + dto.getMarksheet());

        return ResponseEntity.ok(res);
    }

    /* =====================================================
       SAVE DRAFT (MULTIPART)
       ===================================================== */
    @PostMapping("/save")
    public ResponseEntity<?> saveDraft(@RequestParam(value = "photo", required = false) MultipartFile photo,
                                       @RequestParam(value = "signature", required = false) MultipartFile signature,
                                       @RequestParam(value = "marksheet", required = false) MultipartFile marksheet) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("===== SAVE DRAFT CONTROLLER START =====");
        System.out.println("User: " + user.getEmail());

        documentService.saveDraft(user, photo, signature, marksheet);

        System.out.println("===== SAVE DRAFT CONTROLLER END =====");

        // Return updated document DTO after save so frontend can refresh
        DocumentDetailsDTO updatedDto = documentService.getCurrent(user);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Draft saved successfully",
                "documents", updatedDto,
                "documentStatus", updatedDto.getDocumentStatus()
        ));
    }

    
    /* =====================================================
    SUBMIT & LOCK (DOCUMENT) WITH FILE UPLOAD
    ===================================================== */
 @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
 public ResponseEntity<?> submitDocuments(
         @RequestParam(value = "photo", required = false) MultipartFile photo,
         @RequestParam(value = "signature", required = false) MultipartFile signature,
         @RequestParam(value = "marksheet", required = false) MultipartFile marksheet
 ) {

     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
     if (auth == null || !auth.isAuthenticated()) {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
     }

     String email = auth.getName();
     User user = userRepository.findByEmail(email)
             .orElseThrow(() -> new RuntimeException("User not found"));

     /* =====================================================
        🔒 CORRECTION WINDOW CHECK
        ===================================================== */
     boolean editable = correctionService.isSectionEditable(user, "DOCUMENT");
     if (user.getDocumentStatus() == FormStatus.COMPLETED && !editable) {
         return ResponseEntity.status(HttpStatus.FORBIDDEN)
                 .body(Map.of("status", "ERROR", "message", "Correction window closed"));
     }

     DocumentDetails doc = documentRepository.findByUser(user)
             .orElseThrow(() -> new RuntimeException("Document details not found"));

     /* =====================================================
        STORE FILES IF NEW FILES ARE UPLOADED
        ===================================================== */
     String userFolder = "user_" + user.getApplicationNumber();

     try {
         if (photo != null && !photo.isEmpty()) {
             String storedName = fileStorageService.storeFile(photo, userFolder, "photo");
             doc.setPhoto(storedName);
             doc.setPhotoUploaded(true);
         }

         if (signature != null && !signature.isEmpty()) {
             String storedName = fileStorageService.storeFile(signature, userFolder, "signature");
             doc.setSignature(storedName);
             doc.setSignatureUploaded(true);
         }

         if (marksheet != null && !marksheet.isEmpty()) {
             String storedName = fileStorageService.storeFile(marksheet, userFolder, "marksheet");
             doc.setMarksheet(storedName);
             doc.setMarksheetUploaded(true);
         }

     } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                 .body(Map.of("status", "ERROR", "message", "File storage failed: " + e.getMessage()));
     }

     /* =====================================================
        CHECK IF ALL FILES ARE UPLOADED
        ===================================================== */
     boolean allUploaded =
             Boolean.TRUE.equals(doc.getPhotoUploaded()) &&
             Boolean.TRUE.equals(doc.getSignatureUploaded()) &&
             Boolean.TRUE.equals(doc.getMarksheetUploaded());

     if (!allUploaded) {
         return ResponseEntity.badRequest().body(Map.of(
                 "status", "ERROR",
                 "message", "Please upload all required documents before submitting"
         ));
     }

     /* =====================================================
        LOCK DOCUMENT
        ===================================================== */
     doc.setDocumentStatus(FormStatus.COMPLETED);
     doc.setUploadStatus(UploadStatus.COMPLETED);
     doc.setUpdatedAt(LocalDateTime.now());
     user.setDocumentStatus(FormStatus.COMPLETED);

     documentRepository.save(doc);
     userRepository.save(user);

     /* =====================================================
        RETURN UPDATED DOCUMENT DETAILS
        ===================================================== */
     DocumentDetailsDTO updatedDto = new DocumentDetailsDTO();
     updatedDto.setDocumentStatus(doc.getDocumentStatus());
     updatedDto.setUploadStatus(doc.getUploadStatus());

     String baseUrl = "http://localhost:8080/files/" + userFolder + "/";
     updatedDto.setPhoto(doc.getPhoto() != null ? baseUrl + "photo/" + doc.getPhoto() : null);
     updatedDto.setSignature(doc.getSignature() != null ? baseUrl + "signature/" + doc.getSignature() : null);
     updatedDto.setMarksheet(doc.getMarksheet() != null ? baseUrl + "marksheet/" + doc.getMarksheet() : null);

     updatedDto.setPhotoUploaded(doc.getPhotoUploaded());
     updatedDto.setSignatureUploaded(doc.getSignatureUploaded());
     updatedDto.setMarksheetUploaded(doc.getMarksheetUploaded());

     return ResponseEntity.ok(Map.of(
             "status", "SUCCESS",
             "message", "Documents submitted successfully",
             "documents", updatedDto,
             "documentStatus", doc.getDocumentStatus()
     ));
 }
    
}
