package com.wafipix.wafipix.modules.service.dto.response;

import java.util.UUID;

public record PackageFeatureResponse(
        UUID id,
        String text,
        Boolean highlight
) {}
