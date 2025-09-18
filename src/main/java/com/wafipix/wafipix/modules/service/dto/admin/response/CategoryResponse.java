package com.wafipix.wafipix.modules.service.dto.admin.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponse(
    UUID id,
    String title,
    String subtitle,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy
) {}
