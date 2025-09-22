package com.wafipix.wafipix.modules.service.mapper;

import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceResponse;
import com.wafipix.wafipix.modules.service.dto.admin.request.CreateServiceRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateServiceRequest;
import com.wafipix.wafipix.modules.service.entity.Category;
import com.wafipix.wafipix.modules.service.entity.Service;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceMapper {

    public ServiceResponse toResponse(Service service) {
        if (service == null) return null;

        return new ServiceResponse(
                service.getId(),
                service.getTitle(),
                service.getSlug(),
                service.getSubtitle(),
                service.getDescription(),
                service.getIcon(),
                service.getCategory() != null ? service.getCategory().getId() : null,
                service.getCategory() != null ? service.getCategory().getTitle() : null,
                service.getActive(),
                service.getCreatedAt(),
                service.getUpdatedAt(),
                service.getCreatedBy(),
                service.getUpdatedBy()
        );
    }

    public List<ServiceResponse> toResponseList(List<Service> services) {
        if (services == null) return List.of();
        return services.stream()
                .map(this::toResponse)
                .toList();
    }

    public Service toEntity(CreateServiceRequest request, Category category, String iconUrl) {
        if (request == null) return null;

        return Service.builder()
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .description(request.getDescription())
                .icon(iconUrl)
                .category(category)
                .active(true)
                .build();
    }

    public void updateEntity(Service service, UpdateServiceRequest request, Category category, String iconUrl) {
        if (service == null || request == null) return;

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            service.setTitle(request.getTitle());
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