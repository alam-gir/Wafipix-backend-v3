package com.wafipix.wafipix.modules.service.mapper;

import com.wafipix.wafipix.modules.service.dto.admin.request.CreateCategoryRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateCategoryRequest;
import com.wafipix.wafipix.modules.service.dto.admin.response.CategoryResponse;
import com.wafipix.wafipix.modules.service.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    
    public Category toEntity(CreateCategoryRequest request) {
        return Category.builder()
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .build();
    }
    
    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getTitle(),
                category.getSubtitle(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getCreatedBy(),
                category.getUpdatedBy()
        );
    }
    
    public List<CategoryResponse> toResponseList(List<Category> categories) {
        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public void updateEntity(Category category, UpdateCategoryRequest request) {
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            category.setTitle(request.getTitle());
        }
        
        if (request.getSubtitle() != null) {
            category.setSubtitle(request.getSubtitle());
        }
    }
}
