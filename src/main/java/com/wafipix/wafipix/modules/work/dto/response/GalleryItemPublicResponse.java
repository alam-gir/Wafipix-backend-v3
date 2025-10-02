package com.wafipix.wafipix.modules.work.dto.response;

import java.util.UUID;

public record GalleryItemPublicResponse(
        UUID id,
        String type, // "image" or "video"
        String url
) {}

