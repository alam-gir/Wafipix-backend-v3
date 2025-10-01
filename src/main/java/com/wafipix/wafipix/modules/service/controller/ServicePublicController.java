package com.wafipix.wafipix.modules.service.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceFAQResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceFeatureResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceResponse;
import com.wafipix.wafipix.modules.service.dto.response.CategoryPublicResponse;
import com.wafipix.wafipix.modules.service.dto.response.ServicePageDataResponse;
import com.wafipix.wafipix.modules.service.dto.response.ServicePackageResponse;
import com.wafipix.wafipix.modules.service.dto.response.ServicePublicResponse;
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
    public ResponseEntity<ApiResponse<List<ServicePublicResponse>>> getActiveServices() {
        log.info("Public request for active services");
        
        List<ServicePublicResponse> services = serviceService.getPublicActiveServices();
        
        return ResponseEntity.ok(ApiResponse.success(services, "Active services retrieved successfully"));
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ServicePageDataResponse>> getServiceBySlug(@PathVariable String slug) {
        log.info("Public request for service by slug: {}", slug);
        
        ServicePageDataResponse response = serviceService.getPublicServiceBySlug(slug);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Service retrieved successfully"));
    }
    
    @GetMapping("/packages")
    public ResponseEntity<ApiResponse<List<ServicePackageResponse>>> getAllServicePackages() {
        log.info("Public request for all service packages");
        
        List<ServicePackageResponse> packages = serviceService.getAllPublicServicePackages();
        
        return ResponseEntity.ok(ApiResponse.success(packages, "Service packages retrieved successfully"));
    }
    
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryPublicResponse>>> getServiceCategories() {
        log.info("Public request for service categories");
        
        List<CategoryPublicResponse> categories = serviceService.getPublicServiceCategories();
        
        return ResponseEntity.ok(ApiResponse.success(categories, "Service categories retrieved successfully"));
    }
}
