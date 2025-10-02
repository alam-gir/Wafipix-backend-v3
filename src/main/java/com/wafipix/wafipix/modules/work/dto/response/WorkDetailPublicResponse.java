package com.wafipix.wafipix.modules.work.dto.response;

import java.util.List;
import java.util.UUID;

public record WorkDetailPublicResponse(
        UUID id,
        String title,
        String slug,
        String description,
        String coverVideo,
        String coverImage,
        String profileVideo,
        String profileImage,
        List<GalleryPublicResponse> galleries
) {}

