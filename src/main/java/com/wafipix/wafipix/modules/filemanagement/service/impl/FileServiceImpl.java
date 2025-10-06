package com.wafipix.wafipix.modules.filemanagement.service.impl;

import com.wafipix.wafipix.modules.filemanagement.entity.File;
import com.wafipix.wafipix.modules.filemanagement.repository.FileRepository;
import com.wafipix.wafipix.modules.filemanagement.service.FileStorageService;
import com.wafipix.wafipix.modules.filemanagement.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of FileService
 * Handles file operations including upload, deletion, and database management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public File uploadFile(MultipartFile file, String folderPath, String description) {
        try {
            // Upload file to storage (local or cloud)
            FileStorageService.UploadResult uploadResult = fileStorageService.uploadFile(file, folderPath);
            
            // Create File entity
            File fileEntity = File.builder()
                    .fileName(uploadResult.getFileName())
                    .originalFileName(file.getOriginalFilename())
                    .filePath(uploadResult.getFilePath())
                    .publicUrl(uploadResult.getPublicUrl())
                    .mimeType(uploadResult.getMimeType())
                    .fileSize(uploadResult.getFileSize())
                    .fileExtension(uploadResult.getFileExtension())
                    .folderPath(folderPath)
                    .description(description)
                    .isActive(true)
                    .build();
            
            // Save to database
            File savedFile = fileRepository.save(fileEntity);
            log.info("File uploaded and saved to database: {}", savedFile.getPublicUrl());
            
            return savedFile;
            
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    @Transactional
    public File uploadFile(MultipartFile file, String folderPath) {
        return uploadFile(file, folderPath, null);
    }

    @Override
    @Transactional
    public List<File> uploadFiles(List<MultipartFile> files, String folderPath) {
        List<File> uploadedFiles = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                File uploadedFile = uploadFile(file, folderPath);
                uploadedFiles.add(uploadedFile);
            } catch (Exception e) {
                log.error("Error uploading file {}: {}", file.getOriginalFilename(), e.getMessage());
                // Continue with other files even if one fails
            }
        }
        
        log.info("Uploaded {} files out of {} requested", uploadedFiles.size(), files.size());
        return uploadedFiles;
    }

    @Override
    @Transactional
    public boolean deleteFileByUrl(String publicUrl) {
        try {
            Optional<File> fileOptional = fileRepository.findByPublicUrl(publicUrl);
            if (fileOptional.isEmpty()) {
                log.warn("File not found in database: {}", publicUrl);
                return false;
            }
            
            File file = fileOptional.get();
            
            // Delete from storage (local or cloud)
            boolean deletedFromStorage = fileStorageService.deleteFile(file.getFilePath());
            if (!deletedFromStorage) {
                log.warn("Failed to delete file from storage: {}", file.getFilePath());
            }
            
            // Delete from database
            fileRepository.delete(file);
            
            log.info("File deleted successfully: {}", publicUrl);
            return true;
            
        } catch (Exception e) {
            log.error("Error deleting file by URL {}: {}", publicUrl, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteFileByPath(String filePath) {
        try {
            Optional<File> fileOptional = fileRepository.findByFilePath(filePath);
            if (fileOptional.isEmpty()) {
                log.warn("File not found in database: {}", filePath);
                return false;
            }
            
            File file = fileOptional.get();
            
            // Delete from storage (local or cloud)
            boolean deletedFromStorage = fileStorageService.deleteFile(filePath);
            if (!deletedFromStorage) {
                log.warn("Failed to delete file from storage: {}", filePath);
            }
            
            // Delete from database
            fileRepository.delete(file);
            
            log.info("File deleted successfully: {}", filePath);
            return true;
            
        } catch (Exception e) {
            log.error("Error deleting file by path {}: {}", filePath, e.getMessage());
            return false;
        }
    }

    @Override
    public File findByUrl(String publicUrl) {
        return fileRepository.findByPublicUrl(publicUrl).orElse(null);
    }

    @Override
    public File findByPath(String filePath) {
        return fileRepository.findByFilePath(filePath).orElse(null);
    }

    @Override
    public List<File> findByFolderPath(String folderPath) {
        return fileRepository.findByFolderPathAndIsActiveTrue(folderPath);
    }

    @Override
    public boolean fileExistsByUrl(String publicUrl) {
        return fileRepository.existsByPublicUrl(publicUrl);
    }

    @Override
    public boolean fileExistsByPath(String filePath) {
        return fileRepository.existsByFilePath(filePath);
    }

    @Override
    public File.FileType getFileType(String publicUrl) {
        File file = findByUrl(publicUrl);
        if (file == null) {
            return null;
        }
        return file.getFileType();
    }

    @Override
    @Transactional
    public boolean softDeleteFile(String publicUrl) {
        try {
            Optional<File> fileOptional = fileRepository.findByPublicUrl(publicUrl);
            if (fileOptional.isEmpty()) {
                log.warn("File not found for soft delete: {}", publicUrl);
                return false;
            }
            
            File file = fileOptional.get();
            file.setIsActive(false);
            fileRepository.save(file);
            
            log.info("File soft deleted: {}", publicUrl);
            return true;
            
        } catch (Exception e) {
            log.error("Error soft deleting file {}: {}", publicUrl, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean restoreFile(String publicUrl) {
        try {
            Optional<File> fileOptional = fileRepository.findByPublicUrl(publicUrl);
            if (fileOptional.isEmpty()) {
                log.warn("File not found for restore: {}", publicUrl);
                return false;
            }
            
            File file = fileOptional.get();
            file.setIsActive(true);
            fileRepository.save(file);
            
            log.info("File restored: {}", publicUrl);
            return true;
            
        } catch (Exception e) {
            log.error("Error restoring file {}: {}", publicUrl, e.getMessage());
            return false;
        }
    }
}
