package com.wafipix.wafipix.modules.service.service.impl;

import com.wafipix.wafipix.common.exception.BusinessException;
import com.wafipix.wafipix.common.exception.ResourceNotFoundException;
import com.wafipix.wafipix.common.util.SlugUtil;
import com.wafipix.wafipix.modules.filemanagement.service.FileService;
import com.wafipix.wafipix.modules.service.dto.admin.request.CreateServiceRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.ServiceSearchRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateServiceFAQsRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateServiceFeaturesRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateServiceRequest;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceFAQResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceFeatureResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceListResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceResponse;
import com.wafipix.wafipix.modules.service.entity.Category;
import com.wafipix.wafipix.modules.service.entity.FAQ;
import com.wafipix.wafipix.modules.service.entity.Feature;
import com.wafipix.wafipix.modules.service.entity.Service;
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
import java.util.List;
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
        
        List<Feature> features = service.getFeatures();
        if (features == null) {
            features = new ArrayList<>();
        }
        
        List<ServiceFeatureResponse> featureResponses = features.stream()
                .map(feature -> new ServiceFeatureResponse(feature.getText(), feature.getHighlight()))
                .collect(Collectors.toList());
        
        log.info("Found {} features for service: {}", featureResponses.size(), serviceId);
        return featureResponses;
    }
    
    @Override
    @Transactional
    public List<ServiceFeatureResponse> updateServiceFeatures(UpdateServiceFeaturesRequest request) {
        log.info("Updating features for service: {}", request.getServiceId());
        
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + request.getServiceId()));
        
        // Convert request features to entity features
        List<Feature> features = new ArrayList<>();
        if (request.getFeatures() != null) {
            features = request.getFeatures().stream()
                    .map(req -> Feature.builder()
                            .text(req.getText())
                            .highlight(req.getHighlight() != null ? req.getHighlight() : false)
                            .build())
                    .collect(Collectors.toList());
        }
        
        // Update service features
        service.setFeatures(features);
        serviceRepository.save(service);
        
        // Convert to response
        List<ServiceFeatureResponse> featureResponses = features.stream()
                .map(feature -> new ServiceFeatureResponse(feature.getText(), feature.getHighlight()))
                .collect(Collectors.toList());
        
        log.info("Updated {} features for service: {}", featureResponses.size(), request.getServiceId());
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
                .map(faq -> new ServiceFAQResponse(faq.getQuestion(), faq.getAnswer()))
                .collect(Collectors.toList());
        
        log.info("Found {} FAQs for service: {}", faqResponses.size(), serviceId);
        return faqResponses;
    }
    
    @Override
    @Transactional
    public List<ServiceFAQResponse> updateServiceFAQs(UpdateServiceFAQsRequest request) {
        log.info("Updating FAQs for service: {}", request.getServiceId());
        
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + request.getServiceId()));
        
        // Convert request FAQs to entity FAQs
        List<FAQ> faqs = new ArrayList<>();
        if (request.getFaqs() != null) {
            faqs = request.getFaqs().stream()
                    .map(req -> FAQ.builder()
                            .question(req.getQuestion())
                            .answer(req.getAnswer())
                            .build())
                    .collect(Collectors.toList());
        }
        
        // Update service FAQs
        service.setFaqs(faqs);
        serviceRepository.save(service);
        
        // Convert to response
        List<ServiceFAQResponse> faqResponses = faqs.stream()
                .map(faq -> new ServiceFAQResponse(faq.getQuestion(), faq.getAnswer()))
                .collect(Collectors.toList());
        
        log.info("Updated {} FAQs for service: {}", faqResponses.size(), request.getServiceId());
        return faqResponses;
    }
    
    @Override
    public Service getServiceEntityById(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));
    }
}
