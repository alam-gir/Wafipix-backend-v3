package com.wafipix.wafipix.modules.service.dto.response;

import java.util.UUID;

public record ServiceFeaturePublicResponse(
        UUID id,
        String title,
        String description,
        String icon
) {}
