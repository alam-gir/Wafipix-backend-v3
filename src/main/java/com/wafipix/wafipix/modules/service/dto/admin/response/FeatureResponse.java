package com.wafipix.wafipix.modules.service.dto.admin.response;

import java.util.UUID;

public record FeatureResponse(
    UUID id,
    String text,
    Boolean highlight
) {}
