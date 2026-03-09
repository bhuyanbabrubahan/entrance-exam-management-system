package com.jee.publicapi.serviceimpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jee.publicapi.dto.DocumentDetailsDTO;
import com.jee.publicapi.entity.DocumentDetails;
import com.jee.publicapi.entity.User;
import com.jee.publicapi.enums.FormStatus;
import com.jee.publicapi.enums.UploadStatus;
import com.jee.publicapi.repository.DocumentDetailsRepository;
import com.jee.publicapi.service.CorrectionWindowService;
import com.jee.publicapi.service.DocumentDetailsService;
import com.jee.publicapi.storage.FileStorageService;

@Service
public class DocumentDetailsServiceImpl implements DocumentDetailsService {

    private final DocumentDetailsRepository documentRepository;
    private final FileStorageService fileStorageService;
    private final CorrectionWindowService correctionService;

    public DocumentDetailsServiceImpl(DocumentDetailsRepository documentRepository,
                                      FileStorageService fileStorageService,
                                      CorrectionWindowService correctionService) {
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
        this.correctionService = correctionService;
    }

    @Override
    public DocumentDetailsDTO getCurrent(User user) {
        DocumentDetails doc = documentRepository.findByUser(user).orElse(null);

        if (doc == null) {
            doc = new DocumentDetails();
            doc.setUser(user);
            doc.setDocumentStatus(FormStatus.NOT_STARTED);
            doc.setUploadStatus(UploadStatus.PENDING);
            documentRepository.save(doc);
        }

        DocumentDetailsDTO dto = new DocumentDetailsDTO();
        dto.setDocumentStatus(doc.getDocumentStatus());
        dto.setUploadStatus(doc.getUploadStatus());

        // Convert filenames to accessible URLs
        String userFolder = "user_" + user.getApplicationNumber();
        dto.setPhoto(fileStorageService.getFileUrl(userFolder, "photo", doc.getPhoto()));
        dto.setSignature(fileStorageService.getFileUrl(userFolder, "signature", doc.getSignature()));
        dto.setMarksheet(fileStorageService.getFileUrl(userFolder, "marksheet", doc.getMarksheet()));

        dto.setPhotoUploaded(doc.getPhotoUploaded());
        dto.setSignatureUploaded(doc.getSignatureUploaded());
        dto.setMarksheetUploaded(doc.getMarksheetUploaded());

        return dto;
    }

    @Override
    public void saveDraft(User user, MultipartFile photo, MultipartFile signature, MultipartFile marksheet) {
        DocumentDetails doc = documentRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Document record not found"));
        doc.setUser(user);

        String userFolder = "user_" + user.getApplicationNumber();

        System.out.println("===== SAVE DRAFT CONTROLLER START =====");
        System.out.println("User: " + user.getEmail());
        /// 🔒 LOCK CHECK (CORRECT VERSION)
        boolean isCompleted = doc.getDocumentStatus() == FormStatus.COMPLETED;
        boolean editable = correctionService.isSectionEditable(user, "DOCUMENT");

        if (isCompleted && !editable) {
            throw new IllegalStateException("Document is locked by admin");
        }

        try {
            if (photo != null && !photo.isEmpty()) {
            	String storedName = fileStorageService.storeFile(photo, userFolder, "photo");
            	doc.setPhoto(storedName);
                doc.setPhotoUploaded(true);
                

                System.out.println("Photo Received: " + photo.getOriginalFilename());
                System.out.println("Photo Size: " + photo.getSize());
            }

            if (signature != null && !signature.isEmpty()) {
            	String storedName = fileStorageService.storeFile(signature, userFolder, "signature");
            	doc.setSignature(storedName);
                doc.setSignatureUploaded(true);

                System.out.println("Signature Received: " + signature.getOriginalFilename());
                System.out.println("Signature Size: " + signature.getSize());
            }

            if (marksheet != null && !marksheet.isEmpty()) {
            	String storedName = fileStorageService.storeFile(marksheet, userFolder, "marksheet");
            	doc.setMarksheet(storedName);
                doc.setMarksheetUploaded(true);

                System.out.println("Marksheet Received: " + marksheet.getOriginalFilename());
                System.out.println("Marksheet Size: " + marksheet.getSize());
            }
        } catch (Exception e) {
            System.out.println("❌ File storage error: " + e.getMessage());
        }

        // Status logic
        boolean allUploaded = Boolean.TRUE.equals(doc.getPhotoUploaded())
                && Boolean.TRUE.equals(doc.getSignatureUploaded())
                && Boolean.TRUE.equals(doc.getMarksheetUploaded());

        if (allUploaded) {
            doc.setUploadStatus(UploadStatus.COMPLETED);
        } else if (doc.getPhotoUploaded() || doc.getSignatureUploaded() || doc.getMarksheetUploaded()) {
            doc.setUploadStatus(UploadStatus.PARTIAL);
        } else {
            doc.setUploadStatus(UploadStatus.PENDING);
        }

        if (doc.getDocumentStatus() != FormStatus.COMPLETED) {
            doc.setDocumentStatus(FormStatus.IN_PROGRESS);
            user.setDocumentStatus(FormStatus.IN_PROGRESS);
        }
        doc.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(doc);

        System.out.println("===== SAVE DRAFT END =====");
    }

    @Override
    public void submit(User user) {
        DocumentDetails doc = documentRepository.findByUser(user).orElseThrow();

        // 🔒 LOCK CHECK (MUST BE FIRST)
        boolean isCompleted = doc.getDocumentStatus() == FormStatus.COMPLETED;
        boolean editable = correctionService.isSectionEditable(user, "DOCUMENT");

        if (isCompleted && !editable) {
            throw new IllegalStateException("Document already submitted and locked");
        }
        boolean allUploaded = Boolean.TRUE.equals(doc.getPhotoUploaded())
                && Boolean.TRUE.equals(doc.getSignatureUploaded())
                && Boolean.TRUE.equals(doc.getMarksheetUploaded());

        if (!allUploaded) {
            throw new RuntimeException("All files must be uploaded before submission");
        }

        // ✅ NOW LOCK
        
        doc.setDocumentStatus(FormStatus.COMPLETED);
        doc.setUploadStatus(UploadStatus.COMPLETED);
        user.setDocumentStatus(FormStatus.COMPLETED);
        doc.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(doc);
    }


    @Override
    public DocumentDetails getEntityByUser(User user) {
        return documentRepository.findByUser(user).orElse(null);
    }
}
