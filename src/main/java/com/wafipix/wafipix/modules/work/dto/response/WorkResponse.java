package com.wafipix.wafipix.modules.work.dto.response;

import com.wafipix.wafipix.modules.filemanagement.dto.response.FileResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record WorkResponse(
        UUID id,
        String title,
        String slug,
        ServiceResponse service,
        String description,
        FileResponse coverVideo,
        FileResponse coverImage,
        FileResponse profileVideo,
        FileResponse profileImage,
        List<GalleryResponse> galleries,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
