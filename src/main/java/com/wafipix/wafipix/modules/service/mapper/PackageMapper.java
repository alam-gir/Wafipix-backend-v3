package com.wafipix.wafipix.modules.service.mapper;

import com.wafipix.wafipix.modules.service.dto.admin.request.CreatePackageRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdatePackageRequest;
import com.wafipix.wafipix.modules.service.dto.admin.response.PackageResponse;
import com.wafipix.wafipix.modules.service.entity.Package;
import com.wafipix.wafipix.modules.service.entity.Service;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PackageMapper {
    
    public Package toEntity(CreatePackageRequest request, Service service) {
        return Package.builder()
                .service(service)
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .pricing(request.getPricing())
                .features(request.getFeatures())
                .status(request.getStatus())
                .deliveryTime(request.getDeliveryTime())
                .advancePercentage(request.getAdvancePercentage())
                .popular(request.getPopular() != null ? request.getPopular() : false)
                .build();
    }
    
    public PackageResponse toResponse(Package packageEntity) {
        return new PackageResponse(
                packageEntity.getId(),
                packageEntity.getService().getId(),
                packageEntity.getService().getTitle(),
                packageEntity.getTitle(),
                packageEntity.getSubtitle(),
                packageEntity.getPricing(),
                packageEntity.getFeatures(),
                packageEntity.getStatus(),
                packageEntity.getDeliveryTime(),
                packageEntity.getAdvancePercentage(),
                packageEntity.getPopular(),
                packageEntity.getCreatedAt(),
                packageEntity.getUpdatedAt(),
                packageEntity.getCreatedBy(),
                packageEntity.getUpdatedBy()
        );
    }
    
    public List<PackageResponse> toResponseList(List<Package> packages) {
        return packages.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public void updateEntity(Package packageEntity, UpdatePackageRequest request, Service service) {
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            packageEntity.setTitle(request.getTitle());
        }
        
        if (request.getSubtitle() != null) {
            packageEntity.setSubtitle(request.getSubtitle());
        }
        
        if (request.getPricing() != null) {
            packageEntity.setPricing(request.getPricing());
        }
        
        if (request.getFeatures() != null) {
            packageEntity.setFeatures(request.getFeatures());
        }
        
        if (request.getStatus() != null) {
            packageEntity.setStatus(request.getStatus());
        }
        
        if (request.getDeliveryTime() != null) {
            packageEntity.setDeliveryTime(request.getDeliveryTime());
        }
        
        if (request.getAdvancePercentage() != null) {
            packageEntity.setAdvancePercentage(request.getAdvancePercentage());
        }
        
        if (request.getPopular() != null) {
            packageEntity.setPopular(request.getPopular());
        }
        
        if (service != null) {
            packageEntity.setService(service);
        }
    }
}
