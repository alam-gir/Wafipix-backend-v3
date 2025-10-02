package com.wafipix.wafipix.modules.work.dto.response;

import java.util.UUID;

public record WorkListPublicResponse(
        UUID id,
        String title,
        String slug,
        String serviceTitle,
        String coverVideo,
        String coverImage,
        String profileVideo,
        String profileImage
) {}

