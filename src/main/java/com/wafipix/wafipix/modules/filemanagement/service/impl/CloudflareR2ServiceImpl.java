package com.wafipix.wafipix.modules.filemanagement.service.impl;

import com.wafipix.wafipix.modules.filemanagement.service.CloudflareR2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Implementation of CloudflareR2Service
 * Handles actual file operations with Cloudflare R2 storage
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CloudflareR2ServiceImpl implements CloudflareR2Service {

    private final S3Client s3Client;
    private final Tika tika = new Tika();

    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    @Override
    public UploadResult uploadFile(MultipartFile file, String folderPath) {
        try {
            // Generate unique file name
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            String uniqueFileName = generateUniqueFileName(fileExtension);
            
            // Create full file path
            String fullPath = folderPath.isEmpty() ? uniqueFileName : folderPath + "/" + uniqueFileName;
            
            // Detect MIME type
            String mimeType = tika.detect(file.getInputStream());
            
            // Upload to R2
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fullPath)
                    .contentType(mimeType)
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            // Generate public URL
            String publicFileUrl = publicUrl + "/" + fullPath;
            
            log.info("File uploaded successfully: {}", fullPath);
            
            return new UploadResult(
                    uniqueFileName,
                    fullPath,
                    publicFileUrl,
                    mimeType,
                    file.getSize(),
                    fileExtension
            );
            
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public boolean deleteFile(String filePath) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully: {}", filePath);
            return true;
            
        } catch (Exception e) {
            log.error("Error deleting file {}: {}", filePath, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean fileExists(String filePath) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
            
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("Error checking file existence {}: {}", filePath, e.getMessage());
            return false;
        }
    }

    @Override
    public String generatePublicUrl(String filePath) {
        return publicUrl + "/" + filePath;
    }

    /**
     * Generate unique file name with timestamp and UUID
     */
    private String generateUniqueFileName(String fileExtension) {
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid + "." + fileExtension;
    }

    /**
     * Extract file extension from filename
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "bin";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "bin";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}
