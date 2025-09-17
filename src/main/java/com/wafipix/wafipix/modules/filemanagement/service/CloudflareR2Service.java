package com.wafipix.wafipix.modules.filemanagement.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service for Cloudflare R2 operations
 * Handles file upload and deletion to/from Cloudflare R2 storage
 */
public interface CloudflareR2Service {

    /**
     * Upload a single file to Cloudflare R2
     * @param file The file to upload
     * @param folderPath The folder path where file should be stored
     * @return UploadResult containing file information
     */
    UploadResult uploadFile(MultipartFile file, String folderPath);

    /**
     * Delete a file from Cloudflare R2 by file path
     * @param filePath The full path of the file in R2 bucket
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteFile(String filePath);

    /**
     * Check if a file exists in R2 bucket
     * @param filePath The full path of the file in R2 bucket
     * @return true if file exists, false otherwise
     */
    boolean fileExists(String filePath);

    /**
     * Generate public URL for a file
     * @param filePath The full path of the file in R2 bucket
     * @return Public URL of the file
     */
    String generatePublicUrl(String filePath);

    /**
     * Upload result data class
     */
    class UploadResult {
        private final String fileName;
        private final String filePath;
        private final String publicUrl;
        private final String mimeType;
        private final long fileSize;
        private final String fileExtension;

        public UploadResult(String fileName, String filePath, String publicUrl, 
                          String mimeType, long fileSize, String fileExtension) {
            this.fileName = fileName;
            this.filePath = filePath;
            this.publicUrl = publicUrl;
            this.mimeType = mimeType;
            this.fileSize = fileSize;
            this.fileExtension = fileExtension;
        }

        // Getters
        public String getFileName() { return fileName; }
        public String getFilePath() { return filePath; }
        public String getPublicUrl() { return publicUrl; }
        public String getMimeType() { return mimeType; }
        public long getFileSize() { return fileSize; }
        public String getFileExtension() { return fileExtension; }
    }
}
