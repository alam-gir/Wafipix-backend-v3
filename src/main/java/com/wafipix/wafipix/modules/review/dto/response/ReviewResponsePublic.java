package com.wafipix.wafipix.modules.review.dto.response;

import java.util.UUID;

public record ReviewResponsePublic(
        UUID id,
        String reviewImage,
        String platform,
        String clientName,
        Integer rating,
        String reviewText
) {}
