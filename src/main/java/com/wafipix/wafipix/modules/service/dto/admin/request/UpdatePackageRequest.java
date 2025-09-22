package com.wafipix.wafipix.modules.service.dto.admin.request;

import com.wafipix.wafipix.modules.service.entity.Feature;
import com.wafipix.wafipix.modules.service.entity.Pricing;
import com.wafipix.wafipix.modules.service.enums.PackageStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UpdatePackageRequest {
    
    private UUID serviceId;
    
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;
    
    @Size(max = 255, message = "Subtitle must not exceed 255 characters")
    private String subtitle;
    
    @Valid
    private Pricing pricing;
    
    private List<Feature> features;
    
    private PackageStatus status;
    
    @Size(max = 100, message = "Delivery time must not exceed 100 characters")
    private String deliveryTime;
    
    private Double advancePercentage;
    
    private Boolean popular;
}
