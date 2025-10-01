package com.wafipix.wafipix.modules.service.dto.response;

import java.util.UUID;

public record CategoryPublicResponse(
        UUID id,
        String name,
        String subtitle
) {}
