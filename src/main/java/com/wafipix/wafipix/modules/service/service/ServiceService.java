package com.wafipix.wafipix.modules.service.service;

import com.wafipix.wafipix.modules.service.dto.admin.request.CreateServiceRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.ServiceSearchRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateServiceFAQsRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateServiceFeaturesRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateServiceRequest;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceFAQResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceFeatureResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceListResponse;
import com.wafipix.wafipix.modules.service.dto.admin.response.ServiceResponse;
import com.wafipix.wafipix.modules.service.dto.response.CategoryPublicResponse;
import com.wafipix.wafipix.modules.service.dto.response.ServicePageDataResponse;
import com.wafipix.wafipix.modules.service.dto.response.ServicePackageResponse;
import com.wafipix.wafipix.modules.service.dto.response.ServicePublicResponse;
import com.wafipix.wafipix.modules.service.entity.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ServiceService {
    
    ServiceResponse createService(CreateServiceRequest request, MultipartFile icon);
    
    List<ServiceResponse> getAllServices();
    
    ServiceListResponse searchServices(ServiceSearchRequest request);
    
    ServiceResponse getServiceById(UUID id);
    
    ServiceResponse getServiceBySlug(String slug);
    
    ServiceResponse updateService(UUID id, UpdateServiceRequest request, MultipartFile icon);
    
    void deleteService(UUID id);
    
    void updateServiceActivityStatus(UUID id, Boolean active);
    
    // Public APIs
    List<ServiceResponse> getActiveServices();
    
    List<ServiceResponse> getActiveServicesByCategory(UUID categoryId);
    
    // Service Features Management
    List<ServiceFeatureResponse> getServiceFeatures(UUID serviceId);
    
    List<ServiceFeatureResponse> updateServiceFeatures(UpdateServiceFeaturesRequest request);
    
    // Service FAQs Management
    List<ServiceFAQResponse> getServiceFAQs(UUID serviceId);
    
    List<ServiceFAQResponse> updateServiceFAQs(UpdateServiceFAQsRequest request);
    
    Service getServiceEntityById(UUID id);
    
    // New Public APIs
    List<ServicePublicResponse> getPublicActiveServices();
    
    ServicePageDataResponse getPublicServiceBySlug(String slug);
    
    List<ServicePackageResponse> getAllPublicServicePackages();
    
    List<CategoryPublicResponse> getPublicServiceCategories();
}
