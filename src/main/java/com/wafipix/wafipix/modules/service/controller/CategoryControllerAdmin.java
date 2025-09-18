package com.wafipix.wafipix.modules.service.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.service.dto.admin.request.CreateCategoryRequest;
import com.wafipix.wafipix.modules.service.dto.admin.request.UpdateCategoryRequest;
import com.wafipix.wafipix.modules.service.dto.admin.response.CategoryResponse;
import com.wafipix.wafipix.modules.service.service.CategoryService;
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
@RequestMapping("/v3/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryControllerAdmin {
    
    private final CategoryService categoryService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        log.info("Admin creating category with title: {}", request.getTitle());
        
        CategoryResponse response = categoryService.createCategory(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Category created successfully"));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        log.info("Admin fetching all categories");
        
        List<CategoryResponse> categories = categoryService.getAllCategories();
        
        return ResponseEntity.ok(ApiResponse.success(categories, "Categories retrieved successfully"));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable UUID id) {
        log.info("Admin fetching category with ID: {}", id);
        
        CategoryResponse response = categoryService.getCategoryById(id);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Category retrieved successfully"));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id, 
            @Valid @RequestBody UpdateCategoryRequest request) {
        log.info("Admin updating category with ID: {}", id);
        
        CategoryResponse response = categoryService.updateCategory(id, request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Category updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        log.info("Admin deleting category with ID: {}", id);
        
        categoryService.deleteCategory(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted successfully"));
    }
}
