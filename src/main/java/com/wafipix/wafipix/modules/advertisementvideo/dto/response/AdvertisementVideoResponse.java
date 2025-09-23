package com.wafipix.wafipix.modules.advertisementvideo.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdvertisementVideoResponse(
        UUID id,
        String url,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy
) {}
