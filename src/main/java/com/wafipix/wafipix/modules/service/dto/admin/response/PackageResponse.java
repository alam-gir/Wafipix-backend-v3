package com.wafipix.wafipix.modules.service.dto.admin.response;

import com.wafipix.wafipix.modules.service.entity.Pricing;
import com.wafipix.wafipix.modules.service.enums.PackageStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PackageResponse(
    UUID id,
    UUID serviceId,
    String serviceTitle,
    String title,
    String subtitle,
    Pricing pricing,
    List<FeatureResponse> features,
    PackageStatus status,
    String deliveryTime,
    Double advancePercentage,
    Boolean popular,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy
) {}
