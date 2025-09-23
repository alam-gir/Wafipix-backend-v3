package com.wafipix.wafipix.modules.client.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientListResponse(
        UUID id,
        String title,
        String logo,
        String description,
        String companyUrl,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
