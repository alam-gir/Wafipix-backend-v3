package com.wafipix.wafipix.modules.service.dto.response;

import java.util.List;
import java.util.UUID;

public record ServicePackageResponse(
        UUID id,
        String name,
        String subtitle,
        PackagePricingResponse pricing,
        List<PackageFeatureResponse> features,
        String status,
        String deliveryTime,
        String paymentTerms,
        Boolean popular
) {}
