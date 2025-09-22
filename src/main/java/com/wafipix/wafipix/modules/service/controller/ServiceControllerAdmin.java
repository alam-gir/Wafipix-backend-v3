package com.wafipix.wafipix.modules.service.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.service.dto.admin.request.CreateServiceRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.ServiceSearchRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateServiceFAQsRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateServiceFeaturesRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateServiceRequest;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceFAQResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceFeatureResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceListResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceResponse;
import com.wafipix.wafipix.modules.service.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v3/admin/services")
@RequiredArgsConstructor
@Slf4j
public class ServiceControllerAdmin {
    
    private final ServiceService serviceService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ServiceResponse>> createService(@Valid @ModelAttribute CreateServiceRequest request) {
        log.info("Admin creating service with title: {}", request.getTitle());
        
        ServiceResponse response = serviceService.createService(request, request.getIcon());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Service created successfully"));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getAllServices() {
        log.info("Admin fetching all services");
        
        List<ServiceResponse> services = serviceService.getAllServices();
        
        return ResponseEntity.ok(ApiResponse.success(services, "Services retrieved successfully"));
    }
    
    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ServiceListResponse>> searchServices(@Valid @RequestBody ServiceSearchRequest request) {
        log.info("Admin searching services with filters: {}", request);
        
        ServiceListResponse response = serviceService.searchServices(request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Services retrieved successfully"));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ServiceResponse>> getServiceById(@PathVariable UUID id) {
        log.info("Admin fetching service with ID: {}", id);
        
        ServiceResponse response = serviceService.getServiceById(id);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Service retrieved successfully"));
    }
    
    @GetMapping("/slug/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ServiceResponse>> getServiceBySlug(@PathVariable String slug) {
        log.info("Admin fetching service with slug: {}", slug);
        
        ServiceResponse response = serviceService.getServiceBySlug(slug);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Service retrieved successfully"));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ServiceResponse>> updateService(
            @PathVariable UUID id, 
            @Valid @ModelAttribute UpdateServiceRequest request) {
        log.info("Admin updating service with ID: {}", id);
        
        ServiceResponse response = serviceService.updateService(id, request, request.getIcon());
        
        return ResponseEntity.ok(ApiResponse.success(response, "Service updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable UUID id) {
        log.info("Admin deleting service with ID: {}", id);
        
        serviceService.deleteService(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Service deleted successfully"));
    }
    
    @PutMapping("/{id}/activity-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateServiceActivityStatus(
            @PathVariable UUID id, 
            @RequestParam Boolean active) {
        log.info("Admin updating service activity status with ID: {} to: {}", id, active);
        
        serviceService.updateServiceActivityStatus(id, active);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Service activity status updated successfully"));
    }
    
    // Service Features Management
    @GetMapping("/{serviceId}/features")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ServiceFeatureResponse>>> getServiceFeatures(@PathVariable UUID serviceId) {
        log.info("Admin fetching features for service: {}", serviceId);
        
        List<ServiceFeatureResponse> features = serviceService.getServiceFeatures(serviceId);
        
        return ResponseEntity.ok(ApiResponse.success(features, "Service features retrieved successfully"));
    }
    
    @PutMapping("/features")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ServiceFeatureResponse>>> updateServiceFeatures(
            @Valid @RequestBody UpdateServiceFeaturesRequest request) {
        log.info("Admin updating features for service: {}", request.getServiceId());
        
        List<ServiceFeatureResponse> features = serviceService.updateServiceFeatures(request);
        
        return ResponseEntity.ok(ApiResponse.success(features, "Service features updated successfully"));
    }
    
    // Service FAQs Management
    @GetMapping("/{serviceId}/faqs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ServiceFAQResponse>>> getServiceFAQs(@PathVariable UUID serviceId) {
        log.info("Admin fetching FAQs for service: {}", serviceId);
        
        List<ServiceFAQResponse> faqs = serviceService.getServiceFAQs(serviceId);
        
        return ResponseEntity.ok(ApiResponse.success(faqs, "Service FAQs retrieved successfully"));
    }
    
    @PutMapping("/faqs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ServiceFAQResponse>>> updateServiceFAQs(
            @Valid @RequestBody UpdateServiceFAQsRequest request) {
        log.info("Admin updating FAQs for service: {}", request.getServiceId());
        
        List<ServiceFAQResponse> faqs = serviceService.updateServiceFAQs(request);
        
        return ResponseEntity.ok(ApiResponse.success(faqs, "Service FAQs updated successfully"));
    }
}
