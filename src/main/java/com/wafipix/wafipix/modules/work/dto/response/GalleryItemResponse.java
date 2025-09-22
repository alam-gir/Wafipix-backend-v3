package com.wafipix.wafipix.modules.work.dto.response;

import com.wafipix.wafipix.modules.filemanagement.dto.response.FileResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record GalleryItemResponse(
        UUID id,
        FileResponse file,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
