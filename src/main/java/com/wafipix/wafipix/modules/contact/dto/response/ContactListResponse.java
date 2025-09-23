package com.wafipix.wafipix.modules.contact.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContactListResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        String message,
        String status,
        String readBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
