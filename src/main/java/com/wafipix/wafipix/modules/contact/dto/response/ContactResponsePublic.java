package com.wafipix.wafipix.modules.contact.dto.response;

import java.util.UUID;

public record ContactResponsePublic(
        UUID id,
        String fullName,
        String email,
        String phone,
        String message
) {}
