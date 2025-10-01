package com.wafipix.wafipix.modules.service.dto.response;

import java.util.List;
import java.util.UUID;

public record SubmenuCategoryResponse(
        UUID id,
        String title,
        List<SubmenuItemResponse> items
) {}
