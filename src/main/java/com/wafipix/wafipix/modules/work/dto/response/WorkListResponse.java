package com.wafipix.wafipix.modules.work.dto.response;

import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkListResponse(
        UUID id,
        String title,
        String slug,
        ServiceResponse service,
        String description,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
