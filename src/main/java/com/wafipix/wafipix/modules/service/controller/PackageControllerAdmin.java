package com.wafipix.wafipix.modules.service.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.service.dto.admin.request.CreatePackageRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdatePackageRequest;
import com.wafipix.wafipix.modules.service.dto.admin.response.PackageResponse;
import com.wafipix.wafipix.modules.service.service.PackageService;
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
@RequestMapping("/admin/packages")
@RequiredArgsConstructor
@Slf4j
public class PackageControllerAdmin {
    
    private final PackageService packageService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PackageResponse>> createPackage(@Valid @RequestBody CreatePackageRequest request) {
        log.info("Admin creating package with title: {}", request.getTitle());
        
        PackageResponse response = packageService.createPackage(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Package created successfully"));
    }
    
    @GetMapping("/service/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PackageResponse>>> getPackagesByServiceId(@PathVariable UUID serviceId) {
        log.info("Admin fetching packages for service: {}", serviceId);
        
        List<PackageResponse> packages = packageService.getPackagesByServiceId(serviceId);
        
        return ResponseEntity.ok(ApiResponse.success(packages, "Packages retrieved successfully"));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PackageResponse>> getPackageById(@PathVariable UUID id) {
        log.info("Admin fetching package with ID: {}", id);
        
        PackageResponse response = packageService.getPackageById(id);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Package retrieved successfully"));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PackageResponse>> updatePackage(
            @PathVariable UUID id, 
            @Valid @RequestBody UpdatePackageRequest request) {
        log.info("Admin updating package with ID: {}", id);
        
        PackageResponse response = packageService.updatePackage(id, request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Package updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePackage(@PathVariable UUID id) {
        log.info("Admin deleting package with ID: {}", id);
        
        packageService.deletePackage(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Package deleted successfully"));
    }
}
