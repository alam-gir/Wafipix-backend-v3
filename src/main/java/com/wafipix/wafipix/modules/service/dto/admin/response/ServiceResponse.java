package com.wafipix.wafipix.modules.service.dto.admin.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceResponse(
    UUID id,
    String title,
    String slug,
    String subtitle,
    String description,
    String icon,
    UUID categoryId,
    String categoryTitle,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy
) {}
