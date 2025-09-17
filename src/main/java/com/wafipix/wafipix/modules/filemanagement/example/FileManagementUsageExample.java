package com.wafipix.wafipix.modules.filemanagement.example;

import com.wafipix.wafipix.modules.filemanagement.entity.File;
import com.wafipix.wafipix.modules.filemanagement.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Example class showing how to use the File Management System
 * This demonstrates the main methods available for file operations
 */
@Component
@RequiredArgsConstructor
public class FileManagementUsageExample {

    private final FileService fileService;

    /**
     * Example: Upload a service icon
     */
    public File uploadServiceIcon(MultipartFile iconFile) {
        // Upload to website-content/icons folder
        return fileService.uploadFile(iconFile, "website-content/icons");
    }

    /**
     * Example: Upload client delivery files
     */
    public List<File> uploadClientDeliveryFiles(List<MultipartFile> files, String clientId, String projectId) {
        // Upload to client-deliveries/{clientId}/{projectId} folder
        String folderPath = "client-deliveries/" + clientId + "/" + projectId;
        return fileService.uploadFiles(files, folderPath);
    }

    /**
     * Example: Upload website content images
     */
    public File uploadWebsiteImage(MultipartFile imageFile, String category) {
        // Upload to website-content/{category} folder
        return fileService.uploadFile(imageFile, "website-content/" + category);
    }

    /**
     * Example: Delete a file when service is removed
     */
    public boolean removeServiceIcon(String iconUrl) {
        return fileService.deleteFileByUrl(iconUrl);
    }

    /**
     * Example: Check file type before processing
     */
    public boolean isImageFile(String fileUrl) {
        File.FileType fileType = fileService.getFileType(fileUrl);
        return fileType == File.FileType.IMAGE;
    }

    /**
     * Example: Get all files in a specific folder
     */
    public List<File> getClientProjectFiles(String clientId, String projectId) {
        String folderPath = "client-deliveries/" + clientId + "/" + projectId;
        return fileService.findByFolderPath(folderPath);
    }

    /**
     * Example: Soft delete files (mark as inactive)
     */
    public boolean archiveClientFiles(String clientId) {
        List<File> files = fileService.findByFolderPath("client-deliveries/" + clientId);
        boolean allDeleted = true;
        
        for (File file : files) {
            if (!fileService.softDeleteFile(file.getPublicUrl())) {
                allDeleted = false;
            }
        }
        
        return allDeleted;
    }

    /**
     * Example: Restore archived files
     */
    public boolean restoreClientFiles(String clientId) {
        List<File> files = fileService.findByFolderPath("client-deliveries/" + clientId);
        boolean allRestored = true;
        
        for (File file : files) {
            if (!fileService.restoreFile(file.getPublicUrl())) {
                allRestored = false;
            }
        }
        
        return allRestored;
    }
}
