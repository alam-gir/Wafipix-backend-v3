package com.wafipix.wafipix.modules.service.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.PackageResponse;
import com.wafipix.wafipix.modules.service.service.PackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v3/public/packages")
@RequiredArgsConstructor
@Slf4j
public class PackagePublicController {
    
    private final PackageService packageService;
    
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<ApiResponse<List<PackageResponse>>> getActivePackagesByServiceId(@PathVariable UUID serviceId) {
        log.info("Public request for active packages by service: {}", serviceId);
        
        List<PackageResponse> packages = packageService.getActivePackagesByServiceId(serviceId);
        
        return ResponseEntity.ok(ApiResponse.success(packages, "Active packages retrieved successfully"));
    }
}
