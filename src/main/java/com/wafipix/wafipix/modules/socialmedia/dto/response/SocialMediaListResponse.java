package com.wafipix.wafipix.modules.socialmedia.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record SocialMediaListResponse(
        UUID id,
        String title,
        String url,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
