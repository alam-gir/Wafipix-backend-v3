package com.wafipix.wafipix.modules.service.service.impl;

import com.wafipix.wafipix.common.exception.BusinessException;
import com.wafipix.wafipix.common.exception.ResourceNotFoundException;
import com.wafipix.wafipix.modules.service.dto.admin.request.CreatePackageRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdatePackageRequest;
import com.wafipix.wafipix.modules.service.dto.admin.response.PackageResponse;
import com.wafipix.wafipix.modules.service.entity.Package;
import com.wafipix.wafipix.modules.service.entity.Service;
import com.wafipix.wafipix.modules.service.mapper.PackageMapper;
import com.wafipix.wafipix.modules.service.repository.PackageRepository;
import com.wafipix.wafipix.modules.service.repository.ServiceRepository;
import com.wafipix.wafipix.modules.service.service.PackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class PackageServiceImpl implements PackageService {
    
    private final PackageRepository packageRepository;
    private final ServiceRepository serviceRepository;
    private final PackageMapper packageMapper;
    
    @Override
    @Transactional
    public PackageResponse createPackage(CreatePackageRequest request) {
        log.info("Creating package with title: {} for service: {}", request.getTitle(), request.getServiceId());
        
        // Get service
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + request.getServiceId()));
        
        // Check if package with same title already exists for this service
        if (packageRepository.existsByTitleIgnoreCaseAndServiceId(request.getTitle(), request.getServiceId())) {
            throw new BusinessException("Package with title '" + request.getTitle() + "' already exists for this service");
        }
        
        // Create package entity
        Package packageEntity = packageMapper.toEntity(request, service);
        Package savedPackage = packageRepository.save(packageEntity);
        
        log.info("Package created successfully with ID: {}", savedPackage.getId());
        return packageMapper.toResponse(savedPackage);
    }
    
    @Override
    public List<PackageResponse> getPackagesByServiceId(UUID serviceId) {
        log.info("Fetching packages for service: {}", serviceId);
        
        List<Package> packages = packageRepository.findByServiceId(serviceId);
        log.info("Found {} packages for service: {}", packages.size(), serviceId);
        
        return packageMapper.toResponseList(packages);
    }
    
    @Override
    public PackageResponse getPackageById(UUID id) {
        log.info("Fetching package with ID: {}", id);
        
        Package packageEntity = packageRepository.findByIdWithService(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found with ID: " + id));
        
        return packageMapper.toResponse(packageEntity);
    }
    
    @Override
    @Transactional
    public PackageResponse updatePackage(UUID id, UpdatePackageRequest request) {
        log.info("Updating package with ID: {}", id);
        
        Package packageEntity = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found with ID: " + id));
        
        // Check if another package with same title already exists for this service
        UUID serviceId = request.getServiceId() != null ? request.getServiceId() : packageEntity.getService().getId();
        if (packageRepository.existsByTitleIgnoreCaseAndServiceIdAndIdNot(request.getTitle(), serviceId, id)) {
            throw new BusinessException("Package with title '" + request.getTitle() + "' already exists for this service");
        }
        
        // Handle service update if provided
        Service service = null;
        if (request.getServiceId() != null) {
            service = serviceRepository.findById(request.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + request.getServiceId()));
        }
        
        // Update package
        packageMapper.updateEntity(packageEntity, request, service);
        
        Package updatedPackage = packageRepository.save(packageEntity);
        
        log.info("Package updated successfully with ID: {}", updatedPackage.getId());
        return packageMapper.toResponse(updatedPackage);
    }
    
    @Override
    @Transactional
    public void deletePackage(UUID id) {
        log.info("Deleting package with ID: {}", id);
        
        Package packageEntity = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found with ID: " + id));
        
        packageRepository.delete(packageEntity);
        
        log.info("Package deleted successfully with ID: {}", id);
    }
    
    @Override
    public List<PackageResponse> getActivePackagesByServiceId(UUID serviceId) {
        log.info("Fetching active packages for service: {}", serviceId);
        
        List<Package> packages = packageRepository.findActivePackagesByServiceId(serviceId);
        log.info("Found {} active packages for service: {}", packages.size(), serviceId);
        
        return packageMapper.toResponseList(packages);
    }
    
    @Override
    public Package getPackageEntityById(UUID id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found with ID: " + id));
    }
}
