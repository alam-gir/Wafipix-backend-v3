package com.wafipix.wafipix.modules.filemanagement.service.impl;

import com.wafipix.wafipix.modules.filemanagement.config.FileStorageConfig;
import com.wafipix.wafipix.modules.filemanagement.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Local file system storage implementation
 * Stores files on the local server filesystem
 */
@Slf4j
public class LocalFileStorageServiceImpl implements FileStorageService {
    
    private final FileStorageConfig.Local config;
    private final Tika tika = new Tika();
    
    public LocalFileStorageServiceImpl(FileStorageConfig config) {
        this.config = config.getStorage().getLocal();
        createBaseDirectory();
    }
    
    @Override
    public UploadResult uploadFile(MultipartFile file, String folderPath) {
        try {
            // Create directory structure
            Path targetDir = Paths.get(config.getBasePath(), folderPath);
            Files.createDirectories(targetDir);
            
            // Generate unique filename
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            Path filePath = targetDir.resolve(fileName);
            
            // Save file to local filesystem
            Files.copy(file.getInputStream(), filePath);
            
            // Generate public URL
            String publicUrl = config.getPublicUrl() + "/" + folderPath + "/" + fileName;
            
            log.info("File uploaded successfully to local storage: {}", filePath);
            
            return new UploadResult(
                fileName,
                folderPath + "/" + fileName,
                publicUrl,
                tika.detect(file.getInputStream()),
                file.getSize(),
                getFileExtension(file.getOriginalFilename())
            );
            
        } catch (IOException e) {
            log.error("Error uploading file to local storage: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file", e);
        }
    }
    
    @Override
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(config.getBasePath(), filePath);
            boolean deleted = Files.deleteIfExists(path);
            
            if (deleted) {
                log.info("File deleted successfully from local storage: {}", filePath);
            } else {
                log.warn("File not found for deletion: {}", filePath);
            }
            
            return deleted;
        } catch (IOException e) {
            log.error("Error deleting file from local storage: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean fileExists(String filePath) {
        Path path = Paths.get(config.getBasePath(), filePath);
        return Files.exists(path);
    }
    
    @Override
    public String generatePublicUrl(String filePath) {
        return config.getPublicUrl() + "/" + filePath;
    }
    
    /**
     * Create base directory if it doesn't exist
     */
    private void createBaseDirectory() {
        try {
            Path basePath = Paths.get(config.getBasePath());
            Files.createDirectories(basePath);
            log.info("Base directory created/verified: {}", basePath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to create base directory: {}", e.getMessage());
            throw new RuntimeException("Failed to create base directory", e);
        }
    }
    
    /**
     * Generate unique file name with timestamp and UUID
     */
    private String generateUniqueFileName(String originalFileName) {
        String fileExtension = getFileExtension(originalFileName);
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid + "." + fileExtension;
    }
    
    /**
     * Extract file extension from filename
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
