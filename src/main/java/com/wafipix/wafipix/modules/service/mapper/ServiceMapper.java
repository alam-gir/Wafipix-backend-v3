package com.wafipix.wafipix.modules.service.mapper;

import com.wafipix.wafipix.common.util.SlugUtil;
import com.wafipix.wafipix.modules.service.dto.admin.request.CreateServiceRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateServiceRequest;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceResponse;
import com.wafipix.wafipix.modules.service.entity.Category;
import com.wafipix.wafipix.modules.service.entity.Service;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiceMapper {
    
    public Service toEntity(CreateServiceRequest request, Category category, String iconUrl) {
        return Service.builder()
                .title(request.getTitle())
                .slug(SlugUtil.generateSlug(request.getTitle()))
                .subtitle(request.getSubtitle())
                .description(request.getDescription())
                .icon(iconUrl)
                .category(category)
                .active(true)
                .build();
    }
    
    public ServiceResponse toResponse(Service service) {
        return new ServiceResponse(
                service.getId(),
                service.getTitle(),
                service.getSlug(),
                service.getSubtitle(),
                service.getDescription(),
                service.getIcon(),
                service.getCategory().getId(),
                service.getCategory().getTitle(),
                service.getActive(),
                service.getCreatedAt(),
                service.getUpdatedAt(),
                service.getCreatedBy(),
                service.getUpdatedBy()
        );
    }
    
    public List<ServiceResponse> toResponseList(List<Service> services) {
        return services.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public void updateEntity(Service service, UpdateServiceRequest request, Category category, String iconUrl) {
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            service.setTitle(request.getTitle());
            // Regenerate slug if title changed
            service.setSlug(SlugUtil.generateSlug(request.getTitle()));
        }
        
        if (request.getSubtitle() != null) {
            service.setSubtitle(request.getSubtitle());
        }
        
        if (request.getDescription() != null) {
            service.setDescription(request.getDescription());
        }
        
        if (iconUrl != null) {
            service.setIcon(iconUrl);
        }
        
        if (category != null) {
            service.setCategory(category);
        }
    }
}
