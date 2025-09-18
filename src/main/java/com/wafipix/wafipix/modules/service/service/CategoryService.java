package com.wafipix.wafipix.modules.service.service;

import com.wafipix.wafipix.modules.service.dto.admin.request.CreateCategoryRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateCategoryRequest;
import com.wafipix.wafipix.modules.service.dto.admin.response.CategoryResponse;
import com.wafipix.wafipix.modules.service.entity.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    
    CategoryResponse createCategory(CreateCategoryRequest request);
    
    List<CategoryResponse> getAllCategories();
    
    CategoryResponse getCategoryById(UUID id);
    
    CategoryResponse updateCategory(UUID id, UpdateCategoryRequest request);
    
    void deleteCategory(UUID id);
    
    Category getCategoryEntityById(UUID id);
}
