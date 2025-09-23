package com.wafipix.wafipix.modules.contact.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContactReplyResponse(
        UUID id,
        String message,
        String repliedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
