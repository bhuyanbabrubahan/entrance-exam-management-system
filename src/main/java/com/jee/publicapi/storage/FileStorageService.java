package com.jee.publicapi.storage;

import java.io.IOException;
import java.nio.file.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jee.publicapi.config.DocumentStorageProperties;

@Service
public class FileStorageService {

    private final Path documentBasePath;
    private final Path correctionBasePath;
    private final String userFolderPattern;

    public FileStorageService(DocumentStorageProperties properties) {

        this.documentBasePath = Paths.get(properties.getDocumentBasePath())
                .toAbsolutePath()
                .normalize();

        this.correctionBasePath = Paths.get(properties.getCorrectionBasePath())
                .toAbsolutePath()
                .normalize();

        this.userFolderPattern = properties.getUserFolderPattern();
    }

    // ================================
    // NEW METHOD (Pattern-based folder)
    // ================================
    public String storeFile(
            MultipartFile file,
            Long applicationNo,
            String typeFolder,
            boolean isCorrection
    ) throws IOException {

        String userFolder = buildUserFolder(applicationNo);
        return saveFile(file, userFolder, typeFolder, isCorrection);
    }

    // ==========================================
    // OLD METHOD (DO NOT REMOVE - BACKWARD SAFE)
    // ==========================================
    public String storeFile(
            MultipartFile file,
            String userFolder,
            String typeFolder
    ) throws IOException {

        return saveFile(file, userFolder, typeFolder, false);
    }

    // ================================
    // COMMON INTERNAL SAVE LOGIC
    // ================================
    private String saveFile(
            MultipartFile file,
            String userFolder,
            String typeFolder,
            boolean isCorrection
    ) throws IOException {

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());

        String safeName = originalName
                .trim()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-zA-Z0-9._-]", "");

        String fileName = System.currentTimeMillis() + "_" + safeName;

        Path basePath = isCorrection ? correctionBasePath : documentBasePath;

        Path targetDir = basePath
                .resolve(userFolder)
                .resolve(typeFolder);

        Files.createDirectories(targetDir);

        Path targetFile = targetDir.resolve(fileName);

        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("========== FILE STORAGE DEBUG ==========");
        System.out.println("Base Path     : " + basePath);
        System.out.println("User Folder   : " + userFolder);
        System.out.println("Type Folder   : " + typeFolder);
        System.out.println("Final Dir     : " + targetDir.toAbsolutePath());
        System.out.println("Final File    : " + targetFile.toAbsolutePath());
        System.out.println("========================================");

        return fileName;
    }

    // ================================
    // BUILD FOLDER FROM PATTERN
    // ================================
    private String buildUserFolder(Long applicationNo) {
        return userFolderPattern.replace("{applicationNo}", String.valueOf(applicationNo));
    }

    
 // ==========================================
 // OLD METHOD (NORMAL DOCUMENTS)
 // ==========================================
 public String getFileUrl(
         String userFolder,
         String typeFolder,
         String fileName) {

     return getFileUrl(userFolder, typeFolder, fileName, false);
 }
    
    
 // ==========================================
 // NEW METHOD (CORRECTION SUPPORT)
 // ==========================================
	 public String getFileUrl(
	         String userFolder,
	         String typeFolder,
	         String fileName,
	         boolean isCorrection) {
	
	     if (fileName == null || fileName.isEmpty()) return null;
	
	     String baseUrl = isCorrection ? "/correction-files/" : "/files/";
	
	     return ServletUriComponentsBuilder.fromCurrentContextPath()
	             .path(baseUrl)
	             .path(userFolder + "/")
	             .path(typeFolder + "/")
	             .path(fileName)
	             .toUriString();
	 }
}