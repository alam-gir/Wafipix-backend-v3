package com.wafipix.wafipix.modules.filemanagement.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record FileResponse(
        UUID id,
        String fileName,
        String originalFileName,
        String filePath,
        String publicUrl,
        String mimeType,
        Long fileSize,
        String fileExtension,
        String folderPath,
        Boolean isActive,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
