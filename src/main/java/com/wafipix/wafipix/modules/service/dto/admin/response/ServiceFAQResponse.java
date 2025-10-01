package com.wafipix.wafipix.modules.service.dto.admin.response;

import java.util.UUID;

public record ServiceFAQResponse(
    UUID id,
    String question,
    String answer
) {}
