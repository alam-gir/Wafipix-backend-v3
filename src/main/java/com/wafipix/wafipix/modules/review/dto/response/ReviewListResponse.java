package com.wafipix.wafipix.modules.review.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewListResponse(
        UUID id,
        String reviewImage,
        String platform,
        String clientName,
        Integer rating,
        String reviewText,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
