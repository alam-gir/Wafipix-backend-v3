package com.wafipix.wafipix.modules.service.dto.response;

import java.util.UUID;

public record SubmenuItemResponse(
        UUID id,
        String title,
        String slug
) {}
