package com.jee.publicapi.correction.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/correction-files")
public class CorrectionFileController {

    @Value("${file.upload.correction-base-path}")
    private String correctionBasePath;

    @GetMapping("/{userFolder}/{typeFolder}/{fileName:.+}")
    public ResponseEntity<Resource> viewFile(
            @PathVariable String userFolder,
            @PathVariable String typeFolder,
            @PathVariable String fileName) throws IOException {

        Path filePath = Paths.get(correctionBasePath)
                .resolve(userFolder)
                .resolve(typeFolder)
                .resolve(fileName)
                .normalize();
        
        // 🔥 ADD THESE DEBUG LINES
        System.out.println("=========== FILE DEBUG ===========");
        System.out.println("Base Path: " + correctionBasePath);
        System.out.println("User Folder: " + userFolder);
        System.out.println("Type Folder: " + typeFolder);
        System.out.println("File Name: " + fileName);
        System.out.println("Constructed Path: " + filePath.toAbsolutePath());
        System.out.println("File Exists? : " + Files.exists(filePath));
        System.out.println("==================================");
        System.out.println("Constructed Path: " + filePath.toFile().getAbsolutePath());
        System.out.println("Exists: " + filePath.toFile().exists());
        System.out.println("Readable: " + filePath.toFile().canRead());

        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("File not found");
        }

        String contentType = Files.probeContentType(filePath);

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}