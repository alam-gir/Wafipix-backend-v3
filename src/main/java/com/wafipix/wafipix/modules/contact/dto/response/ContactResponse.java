package com.wafipix.wafipix.modules.contact.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ContactResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        String message,
        String status,
        String readBy,
        List<ContactReplyResponse> replies,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy,
        String updatedBy
) {}
