package com.wafipix.wafipix.modules.service.dto.response;

import java.util.UUID;

public record ServiceFaqsPublicResponse(
        UUID id,
        String question,
        String answer
) {}
