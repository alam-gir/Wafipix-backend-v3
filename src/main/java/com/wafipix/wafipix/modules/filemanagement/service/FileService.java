package com.wafipix.wafipix.modules.filemanagement.service;

import com.wafipix.wafipix.modules.filemanagement.entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service for file management operations
 * Main service that handles file upload, deletion, and database operations
 */
public interface FileService {

    /**
     * Upload a single file and save metadata to database
     * @param file The file to upload
     * @param folderPath The folder path where file should be stored
     * @param description Optional description for the file
     * @return File entity with all metadata
     */
    File uploadFile(MultipartFile file, String folderPath, String description);

    /**
     * Upload a single file and save metadata to database (without description)
     * @param file The file to upload
     * @param folderPath The folder path where file should be stored
     * @return File entity with all metadata
     */
    File uploadFile(MultipartFile file, String folderPath);

    /**
     * Upload multiple files and save metadata to database
     * @param files List of files to upload
     * @param folderPath The folder path where files should be stored
     * @return List of File entities with all metadata
     */
    List<File> uploadFiles(List<MultipartFile> files, String folderPath);

    /**
     * Delete file by public URL (from both R2 and database)
     * @param publicUrl The public URL of the file
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteFileByUrl(String publicUrl);

    /**
     * Delete file by file path (from both R2 and database)
     * @param filePath The file path in R2 bucket
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteFileByPath(String filePath);

    /**
     * Find file by public URL
     * @param publicUrl The public URL of the file
     * @return File entity if found, null otherwise
     */
    File findByUrl(String publicUrl);

    /**
     * Find file by file path
     * @param filePath The file path in R2 bucket
     * @return File entity if found, null otherwise
     */
    File findByPath(String filePath);

    /**
     * Find files by folder path
     * @param folderPath The folder path
     * @return List of File entities in the folder
     */
    List<File> findByFolderPath(String folderPath);

    /**
     * Check if file exists by public URL
     * @param publicUrl The public URL of the file
     * @return true if file exists, false otherwise
     */
    boolean fileExistsByUrl(String publicUrl);

    /**
     * Check if file exists by file path
     * @param filePath The file path in R2 bucket
     * @return true if file exists, false otherwise
     */
    boolean fileExistsByPath(String filePath);

    /**
     * Get file type category
     * @param publicUrl The public URL of the file
     * @return File type category
     */
    File.FileType getFileType(String publicUrl);

    /**
     * Soft delete file (mark as inactive in database only)
     * @param publicUrl The public URL of the file
     * @return true if soft deletion was successful, false otherwise
     */
    boolean softDeleteFile(String publicUrl);

    /**
     * Restore soft deleted file
     * @param publicUrl The public URL of the file
     * @return true if restoration was successful, false otherwise
     */
    boolean restoreFile(String publicUrl);
}
