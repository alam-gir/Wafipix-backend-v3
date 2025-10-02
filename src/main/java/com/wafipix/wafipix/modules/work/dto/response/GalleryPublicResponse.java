package com.wafipix.wafipix.modules.work.dto.response;

import java.util.List;
import java.util.UUID;

public record GalleryPublicResponse(
        UUID id,
        Boolean isMobileGrid,
        List<GalleryItemPublicResponse> items
) {}

