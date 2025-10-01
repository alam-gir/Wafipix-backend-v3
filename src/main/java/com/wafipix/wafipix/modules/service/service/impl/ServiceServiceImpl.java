package com.wafipix.wafipix.modules.service.service.impl;

import com.wafipix.wafipix.common.exception.BusinessException;
import com.wafipix.wafipix.common.exception.ResourceNotFoundException;
import com.wafipix.wafipix.common.util.SlugUtil;
import com.wafipix.wafipix.modules.filemanagement.service.FileService;
import com.wafipix.wafipix.modules.service.dto.admin.request.CreateServiceRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.ServiceFAQRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.ServiceFeatureRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.ServiceSearchRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateServiceFAQsRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateServiceFeaturesRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateServiceRequest;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceFAQResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceFeatureResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceListResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceResponse;
import com.wafipix.wafipix.modules.service.dto.response.CategoryPublicResponse;
import com.wafipix.wafipix.modules.service.dto.response.PackageFeatureResponse;
import com.wafipix.wafipix.modules.service.dto.response.PackagePricingResponse;
import com.wafipix.wafipix.modules.service.dto.response.ServiceFaqsPublicResponse;
import com.wafipix.wafipix.modules.service.dto.response.ServiceFeaturePublicResponse;
import com.wafipix.wafipix.modules.service.dto.response.ServicePageDataResponse;
import com.wafipix.wafipix.modules.service.dto.response.ServicePackageResponse;
import com.wafipix.wafipix.modules.service.dto.response.ServicePublicResponse;
import com.wafipix.wafipix.modules.service.dto.response.SubmenuCategoryResponse;
import com.wafipix.wafipix.modules.service.dto.response.SubmenuItemResponse;
import com.wafipix.wafipix.modules.service.entity.Category;
import com.wafipix.wafipix.modules.service.entity.FAQ;
import com.wafipix.wafipix.modules.service.entity.Service;
import com.wafipix.wafipix.modules.service.entity.ServiceFeature;
import com.wafipix.wafipix.modules.service.mapper.ServiceMapper;
import com.wafipix.wafipix.modules.service.repository.CategoryRepository;
import com.wafipix.wafipix.modules.service.repository.ServiceRepository;
import com.wafipix.wafipix.modules.service.service.ServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class ServiceServiceImpl implements ServiceService {
    
    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceMapper serviceMapper;
    private final FileService fileService;
    
    @Override
    @Transactional
    public ServiceResponse createService(CreateServiceRequest request, MultipartFile icon) {
        log.info("Creating service with title: {}", request.getTitle());
        
        // Check if service with same title already exists
        if (serviceRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new BusinessException("Service with title '" + request.getTitle() + "' already exists");
        }
        
        // Get category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));
        
        // Generate unique slug
        String baseSlug = SlugUtil.generateSlug(request.getTitle());
        String uniqueSlug = SlugUtil.generateUniqueSlug(baseSlug, serviceRepository::existsBySlugIgnoreCase);
        
        // Upload icon
        String iconUrl;
        try {
            var uploadedFile = fileService.uploadFile(icon, "services/icons");
            iconUrl = uploadedFile.getPublicUrl();
            log.info("Icon uploaded successfully: {}", iconUrl);
        } catch (Exception e) {
            log.error("Failed to upload icon: {}", e.getMessage());
            throw new BusinessException("Failed to upload icon: " + e.getMessage());
        }
        
        // Create service entity
        Service service = Service.builder()
                .title(request.getTitle())
                .slug(uniqueSlug)
                .subtitle(request.getSubtitle())
                .description(request.getDescription())
                .icon(iconUrl)
                .category(category)
                .active(true)
                .build();
        
        Service savedService = serviceRepository.save(service);
        
        log.info("Service created successfully with ID: {}", savedService.getId());
        return serviceMapper.toResponse(savedService);
    }
    
    @Override
    public List<ServiceResponse> getAllServices() {
        log.info("Fetching all services");
        
        List<Service> services = serviceRepository.findAll();
        log.info("Found {} services", services.size());
        
        return serviceMapper.toResponseList(services);
    }
    
    @Override
    public ServiceListResponse searchServices(ServiceSearchRequest request) {
        log.info("Searching services with filters: {}", request);
        
        // Create pageable with sorting
        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        // Search with filters
        Page<Service> servicePage = serviceRepository.findServicesWithFilters(
                request.getTitle(),
                request.getSlug(),
                request.getCategoryId(),
                request.getActive(),
                pageable
        );
        
        // Convert to response
        List<ServiceResponse> services = serviceMapper.toResponseList(servicePage.getContent());
        
        // Create pagination info
        com.wafipix.wafipix.common.dto.PaginationInfo pagination = com.wafipix.wafipix.common.dto.PaginationInfo.builder()
                .currentPage(request.getPage())
                .pageSize(request.getSize())
                .totalElements(servicePage.getTotalElements())
                .totalPages(servicePage.getTotalPages())
                .isFirst(servicePage.isFirst())
                .isLast(servicePage.isLast())
                .hasNext(servicePage.hasNext())
                .hasPrevious(servicePage.hasPrevious())
                .build();
        
        log.info("Found {} services out of {} total", services.size(), servicePage.getTotalElements());
        return new ServiceListResponse(services, pagination);
    }
    
    @Override
    public ServiceResponse getServiceById(UUID id) {
        log.info("Fetching service with ID: {}", id);
        
        Service service = serviceRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));
        
        return serviceMapper.toResponse(service);
    }
    
    @Override
    public ServiceResponse getServiceBySlug(String slug) {
        log.info("Fetching service with slug: {}", slug);
        
        Service service = serviceRepository.findBySlugWithCategory(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with slug: " + slug));
        
        return serviceMapper.toResponse(service);
    }
    
    @Override
    @Transactional
    public ServiceResponse updateService(UUID id, UpdateServiceRequest request, MultipartFile icon) {
        log.info("Updating service with ID: {}", id);
        
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));
        
        // Check if another service with same title already exists
        if (serviceRepository.existsByTitleIgnoreCaseAndIdNot(request.getTitle(), id)) {
            throw new BusinessException("Service with title '" + request.getTitle() + "' already exists");
        }
        
        // Handle category update if provided
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));
        }
        
        // Handle icon update if provided
        String iconUrl = null;
        if (icon != null && !icon.isEmpty()) {
            try {
                // Delete old icon if exists
                if (service.getIcon() != null) {
                    fileService.deleteFileByUrl(service.getIcon());
                    log.info("Old icon deleted: {}", service.getIcon());
                }
                
                // Upload new icon
                var uploadedFile = fileService.uploadFile(icon, "services/icons");
                iconUrl = uploadedFile.getPublicUrl();
                log.info("New icon uploaded successfully: {}", iconUrl);
            } catch (Exception e) {
                log.error("Failed to update icon: {}", e.getMessage());
                throw new BusinessException("Failed to update icon: " + e.getMessage());
            }
        }
        
        // Update service
        serviceMapper.updateEntity(service, request, category, iconUrl);
        
        // Regenerate slug if title changed
        if (request.getTitle() != null && !request.getTitle().equals(service.getTitle())) {
            String baseSlug = SlugUtil.generateSlug(request.getTitle());
            String uniqueSlug = SlugUtil.generateUniqueSlug(baseSlug, slug -> 
                serviceRepository.existsBySlugIgnoreCaseAndIdNot(slug, id));
            service.setSlug(uniqueSlug);
        }
        
        Service updatedService = serviceRepository.save(service);
        
        log.info("Service updated successfully with ID: {}", updatedService.getId());
        return serviceMapper.toResponse(updatedService);
    }
    
    @Override
    @Transactional
    public void deleteService(UUID id) {
        log.info("Deleting service with ID: {}", id);
        
        Service service = serviceRepository.findByIdWithCategoryAndPackages(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));
        
        // Check if service has packages
        if (!service.getPackages().isEmpty()) {
            throw new BusinessException("Cannot delete service that has packages. Please remove all packages first.");
        }
        
        // Delete icon from file storage
        if (service.getIcon() != null) {
            try {
                boolean deleted = fileService.deleteFileByUrl(service.getIcon());
                if (deleted) {
                    log.info("Icon deleted from storage: {}", service.getIcon());
                } else {
                    log.warn("Failed to delete icon from storage: {}", service.getIcon());
                }
            } catch (Exception e) {
                log.error("Error deleting icon from storage: {}", e.getMessage());
                // Continue with service deletion even if icon deletion fails
            }
        }
        
        // Delete service
        serviceRepository.delete(service);
        
        log.info("Service deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional
    public void updateServiceActivityStatus(UUID id, Boolean active) {
        log.info("Updating service activity status with ID: {} to: {}", id, active);
        
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));
        
        service.setActive(active);
        serviceRepository.save(service);
        
        log.info("Service activity status updated successfully with ID: {} to: {}", id, active);
    }
    
    @Override
    public List<ServiceResponse> getActiveServices() {
        log.info("Fetching all active services");
        
        List<Service> services = serviceRepository.findActiveServices();
        log.info("Found {} active services", services.size());
        
        return serviceMapper.toResponseList(services);
    }
    
    @Override
    public List<ServiceResponse> getActiveServicesByCategory(UUID categoryId) {
        log.info("Fetching active services for category: {}", categoryId);
        
        List<Service> services = serviceRepository.findActiveServicesByCategory(categoryId);
        log.info("Found {} active services for category: {}", services.size(), categoryId);
        
        return serviceMapper.toResponseList(services);
    }
    
    @Override
    public List<ServiceFeatureResponse> getServiceFeatures(UUID serviceId) {
        log.info("Fetching features for service: {}", serviceId);
        
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + serviceId));
        
        List<ServiceFeature> features = service.getFeatures();
        if (features == null) {
            features = new ArrayList<>();
        }
        
        List<ServiceFeatureResponse> featureResponses = features.stream()
                .map(feature -> new ServiceFeatureResponse(feature.getId(), feature.getText(), feature.getHighlight()))
                .collect(Collectors.toList());
        
        log.info("Found {} features for service: {}", featureResponses.size(), serviceId);
        return featureResponses;
    }
    
    
    @Override
    public List<ServiceFAQResponse> getServiceFAQs(UUID serviceId) {
        log.info("Fetching FAQs for service: {}", serviceId);
        
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + serviceId));
        
        List<FAQ> faqs = service.getFaqs();
        if (faqs == null) {
            faqs = new ArrayList<>();
        }
        
        List<ServiceFAQResponse> faqResponses = faqs.stream()
                .map(faq -> new ServiceFAQResponse(faq.getId(), faq.getQuestion(), faq.getAnswer()))
                .collect(Collectors.toList());
        
        log.info("Found {} FAQs for service: {}", faqResponses.size(), serviceId);
        return faqResponses;
    }
    
    @Override
    @Transactional
    public List<ServiceFeatureResponse> updateServiceFeatures(UpdateServiceFeaturesRequest request) {
        log.info("=== UPDATING SERVICE FEATURES ===");
        log.info("Service ID: {}", request.getServiceId());
        log.info("Request features count: {}", request.getFeatures() != null ? request.getFeatures().size() : 0);
        
        // Step 1: Load the service
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + request.getServiceId()));
        
        // Step 2: EXPLICITLY DELETE all existing features from database
        log.info("Deleting all existing features from database");
        serviceRepository.deleteAllFeaturesByServiceId(request.getServiceId());
        
        // Step 3: Create new features from request
        List<ServiceFeature> newFeatures = new ArrayList<>();
        if (request.getFeatures() != null) {
            for (ServiceFeatureRequest req : request.getFeatures()) {
                ServiceFeature feature = ServiceFeature.builder()
                        .service(service)
                        .text(req.getText())
                        .highlight(req.getHighlight() != null ? req.getHighlight() : false)
                        .build();
                newFeatures.add(feature);
                log.info("Created feature: {}", req.getText());
            }
        }
        
        // Step 4: Set the new features and save
        service.setFeatures(newFeatures);
        serviceRepository.save(service);
        
        // Step 5: Return response
        List<ServiceFeatureResponse> responses = newFeatures.stream()
                .map(f -> new ServiceFeatureResponse(f.getId(), f.getText(), f.getHighlight()))
                .collect(Collectors.toList());
        
        log.info("=== FINAL RESULT: {} features ===", responses.size());
        return responses;
    }
    
    @Override
    @Transactional
    public List<ServiceFAQResponse> updateServiceFAQs(UpdateServiceFAQsRequest request) {
        log.info("=== UPDATING SERVICE FAQs ===");
        log.info("Service ID: {}", request.getServiceId());
        log.info("Request FAQs count: {}", request.getFaqs() != null ? request.getFaqs().size() : 0);
        
        // Step 1: Load the service
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + request.getServiceId()));
        
        // Step 2: EXPLICITLY DELETE all existing FAQs from database
        log.info("Deleting all existing FAQs from database");
        serviceRepository.deleteAllFaqsByServiceId(request.getServiceId());
        
        // Step 3: Create new FAQs from request
        List<FAQ> newFaqs = new ArrayList<>();
        if (request.getFaqs() != null) {
            for (ServiceFAQRequest req : request.getFaqs()) {
                FAQ faq = FAQ.builder()
                        .service(service)
                        .question(req.getQuestion())
                        .answer(req.getAnswer())
                        .build();
                newFaqs.add(faq);
                log.info("Created FAQ: {}", req.getQuestion());
            }
        }
        
        // Step 4: Set the new FAQs and save
        service.setFaqs(newFaqs);
        serviceRepository.save(service);
        
        // Step 5: Return response
        List<ServiceFAQResponse> responses = newFaqs.stream()
                .map(f -> new ServiceFAQResponse(f.getId(), f.getQuestion(), f.getAnswer()))
                .collect(Collectors.toList());
        
        log.info("=== FINAL RESULT: {} FAQs ===", responses.size());
        return responses;
    }
    
    @Override
    public Service getServiceEntityById(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));
    }
    
    // New Public API Implementations
    @Override
    public List<ServicePublicResponse> getPublicActiveServices() {
        log.info("Fetching active services for public display");
        
        List<Service> activeServices = serviceRepository.findActiveServices();
        log.info("Found {} active services", activeServices.size());
        
        return activeServices.stream()
                .map(service -> new ServicePublicResponse(
                        service.getId(),
                        service.getSlug(),
                        service.getTitle(),
                        service.getSubtitle()
                ))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ServicePageDataResponse getPublicServiceBySlug(String slug) {
        log.info("Fetching service by slug for public display: {}", slug);
        
        Service service = serviceRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Active service not found with slug: " + slug));
        
        // Map packages
        List<ServicePackageResponse> packageResponses = service.getPackages().stream()
                .map(pkg -> new ServicePackageResponse(
                        pkg.getId(),
                        pkg.getTitle(),
                        pkg.getSubtitle(),
                        new PackagePricingResponse(
                                pkg.getPricing().getUsd(),
                                pkg.getPricing().getBdt()
                        ),
                        pkg.getFeatures().stream()
                                .map(feature -> new PackageFeatureResponse(
                                        UUID.randomUUID(), // Generate ID for features
                                        feature.getText(),
                                        feature.getHighlight()
                                ))
                                .collect(Collectors.toList()),
                        pkg.getStatus().name().toLowerCase(),
                        pkg.getDeliveryTime(),
                        pkg.getAdvancePercentage() != null ? pkg.getAdvancePercentage() + "% advance" : "Full payment",
                        pkg.getPopular()
                ))
                .collect(Collectors.toList());
        
        // Map features - access lazy collection within transaction
        List<ServiceFeaturePublicResponse> featureResponses = new ArrayList<>();
        if (service.getFeatures() != null) {
            featureResponses = service.getFeatures().stream()
                    .map(feature -> new ServiceFeaturePublicResponse(
                            feature.getId(), // Use actual feature ID
                            feature.getText(),
                            "", // Description not available in entity
                            "" // Icon not available in entity
                    ))
                    .collect(Collectors.toList());
        }
        
        // Map FAQs - access lazy collection within transaction
        List<ServiceFaqsPublicResponse> faqResponses = new ArrayList<>();
        if (service.getFaqs() != null) {
            faqResponses = service.getFaqs().stream()
                    .map(faq -> new ServiceFaqsPublicResponse(
                            faq.getId(), // Use actual FAQ ID
                            faq.getQuestion(),
                            faq.getAnswer()
                    ))
                    .collect(Collectors.toList());
        }
        
        return new ServicePageDataResponse(
                service.getId(),
                service.getSlug(),
                service.getTitle(),
                service.getSubtitle(),
                packageResponses,
                featureResponses,
                faqResponses
        );
    }
    
    @Override
    public List<ServicePackageResponse> getAllPublicServicePackages() {
        log.info("Fetching all active service packages for public display");
        
        List<Service> activeServices = serviceRepository.findActiveServices();
        List<ServicePackageResponse> allPackages = new ArrayList<>();
        
        for (Service service : activeServices) {
            List<ServicePackageResponse> servicePackages = service.getPackages().stream()
                    .map(pkg -> new ServicePackageResponse(
                            pkg.getId(),
                            pkg.getTitle(),
                            pkg.getSubtitle(),
                            new PackagePricingResponse(
                                    pkg.getPricing().getUsd(),
                                    pkg.getPricing().getBdt()
                            ),
                            pkg.getFeatures().stream()
                                    .map(feature -> new PackageFeatureResponse(
                                            UUID.randomUUID(),
                                            feature.getText(),
                                            feature.getHighlight()
                                    ))
                                    .collect(Collectors.toList()),
                            pkg.getStatus().name().toLowerCase(),
                            pkg.getDeliveryTime(),
                            pkg.getAdvancePercentage() != null ? pkg.getAdvancePercentage() + "% advance" : "Full payment",
                            pkg.getPopular()
                    ))
                    .collect(Collectors.toList());
            allPackages.addAll(servicePackages);
        }
        
        log.info("Found {} total packages from {} active services", allPackages.size(), activeServices.size());
        return allPackages;
    }
    
    @Override
    public List<CategoryPublicResponse> getPublicServiceCategories() {
        log.info("Fetching service categories for public display");
        
        List<Category> categories = categoryRepository.findAllOrderedByCreatedAt();
        log.info("Found {} categories", categories.size());
        
        return categories.stream()
                .map(category -> new CategoryPublicResponse(
                        category.getId(),
                        category.getTitle(),
                        category.getSubtitle()
                ))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SubmenuCategoryResponse> getServicesForNavigation() {
        log.info("Fetching services organized by categories for navigation");
        
        List<Category> categories = categoryRepository.findAllOrderedByCreatedAt();
        log.info("Found {} categories for navigation", categories.size());
        
        return categories.stream()
                .map(category -> {
                    // Get active services for this category
                    List<SubmenuItemResponse> serviceItems = category.getServices().stream()
                            .filter(Service::getActive) // Only active services
                            .map(service -> new SubmenuItemResponse(
                                    service.getId(),
                                    service.getTitle(),
                                    service.getSlug()
                            ))
                            .collect(Collectors.toList());
                    
                    return new SubmenuCategoryResponse(
                            category.getId(),
                            category.getTitle(),
                            serviceItems
                    );
                })
                .filter(category -> !category.items().isEmpty()) // Only include categories with active services
                .collect(Collectors.toList());
    }
}
