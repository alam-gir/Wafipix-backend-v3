package com.wafipix.wafipix.modules.service.dto.admin.response;

import com.wafipix.wafipix.common.dto.PaginationInfo;

import java.util.List;

public record ServiceListResponse(
    List<ServiceResponse> services,
    PaginationInfo pagination
) {}
