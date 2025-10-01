package com.wafipix.wafipix.modules.service.dto.response;

import java.util.UUID;

public record ServicePublicResponse(
        UUID id,
        String slug,
        String title,
        String subtitle
) {}
