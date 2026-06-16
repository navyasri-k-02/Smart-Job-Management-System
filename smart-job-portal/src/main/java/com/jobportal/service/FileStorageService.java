package com.jobportal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${file.upload.dir}")
    private String uploadDir;

    /**
     * Stores a resume PDF file.
     * Validates: only PDF, max 2MB.
     * Returns the relative path where it's stored.
     */
    public String storeResume(MultipartFile file) {
        // Validate not empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload.");
        }

        // Validate MIME type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed.");
        }

        // Validate extension
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        if (!originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only .pdf extension is allowed.");
        }

        // Validate size (2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must not exceed 2MB.");
        }

        try {
            // Create upload directory if not exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String newFilename = UUID.randomUUID() + ".pdf";
            Path targetLocation = uploadPath.resolve(newFilename);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String storedPath = uploadDir + "/" + newFilename;
            log.info("Resume stored at: {}", storedPath);
            return storedPath;

        } catch (IOException e) {
            log.error("Failed to store resume: {}", e.getMessage());
            throw new RuntimeException("Failed to store resume. Please try again.", e);
        }
    }

    /**
     * Deletes a file from storage.
     */
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
            log.info("File deleted: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to delete file {}: {}", filePath, e.getMessage());
        }
    }
}
