package com.wafipix.wafipix.modules.service.dto.response;

import java.util.List;
import java.util.UUID;

public record ServicePageDataResponse(
        UUID id,
        String slug,
        String title,
        String subtitle,
        List<ServicePackageResponse> packages,
        List<ServiceFeaturePublicResponse> features,
        List<ServiceFaqsPublicResponse> faqs
) {}
