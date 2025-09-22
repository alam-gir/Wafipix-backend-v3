package com.wafipix.wafipix.modules.work.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GalleryResponse(
        UUID id,
        Boolean isMobileGrid,
        List<GalleryItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
