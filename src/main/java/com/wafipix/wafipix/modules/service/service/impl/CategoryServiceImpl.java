package com.wafipix.wafipix.modules.service.service.impl;

import com.wafipix.wafipix.common.exception.BusinessException;
import com.wafipix.wafipix.common.exception.ResourceNotFoundException;
import com.wafipix.wafipix.modules.service.dto.admin.request.CreateCategoryRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateCategoryRequest;
import com.wafipix.wafipix.modules.service.dto.admin.response.CategoryResponse;
import com.wafipix.wafipix.modules.service.entity.Category;
import com.wafipix.wafipix.modules.service.mapper.CategoryMapper;
import com.wafipix.wafipix.modules.service.repository.CategoryRepository;
import com.wafipix.wafipix.modules.service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("Creating category with title: {}", request.getTitle());
        
        // Check if category with same title already exists
        if (categoryRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new BusinessException("Category with title '" + request.getTitle() + "' already exists");
        }
        
        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);
        
        log.info("Category created successfully with ID: {}", savedCategory.getId());
        return categoryMapper.toResponse(savedCategory);
    }
    
    @Override
    public List<CategoryResponse> getAllCategories() {
        log.info("Fetching all categories");
        
        List<Category> categories = categoryRepository.findAll();
        log.info("Found {} categories", categories.size());
        
        return categoryMapper.toResponseList(categories);
    }
    
    @Override
    public CategoryResponse getCategoryById(UUID id) {
        log.info("Fetching category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        
        return categoryMapper.toResponse(category);
    }
    
    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, UpdateCategoryRequest request) {
        log.info("Updating category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        
        // Check if another category with same title already exists
        if (categoryRepository.existsByTitleIgnoreCaseAndIdNot(request.getTitle(), id)) {
            throw new BusinessException("Category with title '" + request.getTitle() + "' already exists");
        }
        
        categoryMapper.updateEntity(category, request);
        Category updatedCategory = categoryRepository.save(category);
        
        log.info("Category updated successfully with ID: {}", updatedCategory.getId());
        return categoryMapper.toResponse(updatedCategory);
    }
    
    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        log.info("Deleting category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        
        // Check if category has services
        if (!category.getServices().isEmpty()) {
            throw new BusinessException("Cannot delete category that has services. Please remove all services first.");
        }
        
        categoryRepository.delete(category);
        log.info("Category deleted successfully with ID: {}", id);
    }
    
    @Override
    public Category getCategoryEntityById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }
}
