package com.wafipix.wafipix.modules.service.dto.admin.response;

import java.util.List;

public record CategoryListResponse(
    List<CategoryResponse> categories,
    long totalCount
) {}
