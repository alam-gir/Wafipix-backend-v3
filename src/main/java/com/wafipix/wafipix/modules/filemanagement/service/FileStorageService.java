package com.wafipix.wafipix.modules.filemanagement.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    UploadResult uploadFile(MultipartFile file, String folderPath);
    boolean deleteFile(String filePath);
    boolean fileExists(String filePath);
    String generatePublicUrl(String filePath);

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

        public String getFileName() { return fileName; }
        public String getFilePath() { return filePath; }
        public String getPublicUrl() { return publicUrl; }
        public String getMimeType() { return mimeType; }
        public long getFileSize() { return fileSize; }
        public String getFileExtension() { return fileExtension; }
    }
}
