package com.wafipix.wafipix.modules.service.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceFAQResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceFeatureResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceResponse;
import com.wafipix.wafipix.modules.service.service.ServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v3/public/services")
@RequiredArgsConstructor
@Slf4j
public class ServicePublicController {
    
    private final ServiceService serviceService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getActiveServices() {
        log.info("Public request for active services");
        
        List<ServiceResponse> services = serviceService.getActiveServices();
        
        return ResponseEntity.ok(ApiResponse.success(services, "Active services retrieved successfully"));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getActiveServicesByCategory(@PathVariable UUID categoryId) {
        log.info("Public request for active services by category: {}", categoryId);
        
        List<ServiceResponse> services = serviceService.getActiveServicesByCategory(categoryId);
        
        return ResponseEntity.ok(ApiResponse.success(services, "Active services retrieved successfully"));
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ServiceResponse>> getServiceBySlug(@PathVariable String slug) {
        log.info("Public request for service by slug: {}", slug);
        
        ServiceResponse response = serviceService.getServiceBySlug(slug);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Service retrieved successfully"));
    }
    
    @GetMapping("/{serviceId}/features")
    public ResponseEntity<ApiResponse<List<ServiceFeatureResponse>>> getServiceFeatures(@PathVariable UUID serviceId) {
        log.info("Public request for features of service: {}", serviceId);
        
        List<ServiceFeatureResponse> features = serviceService.getServiceFeatures(serviceId);
        
        return ResponseEntity.ok(ApiResponse.success(features, "Service features retrieved successfully"));
    }
    
    @GetMapping("/{serviceId}/faqs")
    public ResponseEntity<ApiResponse<List<ServiceFAQResponse>>> getServiceFAQs(@PathVariable UUID serviceId) {
        log.info("Public request for FAQs of service: {}", serviceId);
        
        List<ServiceFAQResponse> faqs = serviceService.getServiceFAQs(serviceId);
        
        return ResponseEntity.ok(ApiResponse.success(faqs, "Service FAQs retrieved successfully"));
    }
}
