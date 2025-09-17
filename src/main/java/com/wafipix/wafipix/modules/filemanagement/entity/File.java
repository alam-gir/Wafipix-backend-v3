package com.wafipix.wafipix.modules.filemanagement.entity;

import com.wafipix.wafipix.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * File entity to store metadata of uploaded files
 * Stores all necessary information about files uploaded to Cloudflare R2
 */
@Entity
@Table(name = "files")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File extends Auditable {

    @Column(nullable = false, unique = true)
    private String fileName;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String filePath; // Full path in R2 bucket

    @Column(nullable = false)
    private String publicUrl; // Public accessible URL

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private Long fileSize; // Size in bytes

    @Column(nullable = false)
    private String fileExtension;

    @Column(nullable = false)
    private String folderPath; // Folder structure where file is stored

    @Builder.Default
    private Boolean isActive = true;

    @Column(length = 500)
    private String description; // Optional description

    /**
     * Get file type category based on MIME type
     */
    public FileType getFileType() {
        if (mimeType.startsWith("image/")) {
            return FileType.IMAGE;
        } else if (mimeType.startsWith("video/")) {
            return FileType.VIDEO;
        } else if (mimeType.startsWith("audio/")) {
            return FileType.AUDIO;
        } else if (mimeType.equals("application/pdf")) {
            return FileType.PDF;
        } else if (mimeType.startsWith("text/")) {
            return FileType.TEXT;
        } else if (mimeType.contains("zip") || mimeType.contains("rar") || mimeType.contains("7z")) {
            return FileType.ARCHIVE;
        } else if (mimeType.contains("word") || mimeType.contains("document")) {
            return FileType.DOCUMENT;
        } else if (mimeType.contains("sheet") || mimeType.contains("excel")) {
            return FileType.SPREADSHEET;
        } else if (mimeType.contains("presentation") || mimeType.contains("powerpoint")) {
            return FileType.PRESENTATION;
        } else {
            return FileType.OTHER;
        }
    }

    /**
     * File type enum for categorization
     */
    public enum FileType {
        IMAGE,
        VIDEO,
        AUDIO,
        PDF,
        TEXT,
        ARCHIVE,
        DOCUMENT,
        SPREADSHEET,
        PRESENTATION,
        OTHER
    }
}
