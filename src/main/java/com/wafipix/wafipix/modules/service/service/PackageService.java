package com.wafipix.wafipix.modules.service.service;

import com.wafipix.wafipix.modules.service.dto.admin.request.CreatePackageRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdatePackageRequest;
import com.wafipix.wafipix.modules.service.dto.admin.response.PackageResponse;
import com.wafipix.wafipix.modules.service.entity.Package;

import java.util.List;
import java.util.UUID;

public interface PackageService {
    
    PackageResponse createPackage(CreatePackageRequest request);
    
    List<PackageResponse> getPackagesByServiceId(UUID serviceId);
    
    PackageResponse getPackageById(UUID id);
    
    PackageResponse updatePackage(UUID id, UpdatePackageRequest request);
    
    void deletePackage(UUID id);
    
    // Public APIs
    List<PackageResponse> getActivePackagesByServiceId(UUID serviceId);
    
    Package getPackageEntityById(UUID id);
}
