package com.wafipix.wafipix.modules.socialmedia.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record SocialMediaResponse(
        UUID id,
        String title,
        String url,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy
) {}
