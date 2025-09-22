package com.wafipix.wafipix.modules.service.dto.admin.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceFeaturesRequest {
    
    @NotNull(message = "Service ID is required")
    private UUID serviceId;
    
    @Valid
    private List<ServiceFeatureRequest> features;
}
