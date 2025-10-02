package com.wafipix.wafipix.modules.work.service;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.work.dto.request.CreateWorkRequest;
import com.wafipix.wafipix.modules.work.dto.request.UpdateWorkRequest;
import com.wafipix.wafipix.modules.work.dto.response.WorkListResponse;
import com.wafipix.wafipix.modules.work.dto.response.WorkResponse;
import com.wafipix.wafipix.modules.work.dto.response.WorkListPublicResponse;
import com.wafipix.wafipix.modules.work.dto.response.WorkDetailPublicResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WorkService {

    ApiResponse<WorkResponse> createWork(CreateWorkRequest request);

    ApiResponse<Page<WorkListResponse>> getAllWorks(Pageable pageable);

    ApiResponse<WorkResponse> getWorkById(UUID id);

    ApiResponse<WorkResponse> getWorkBySlug(String slug);

    ApiResponse<WorkResponse> updateWork(UUID id, UpdateWorkRequest request);

    ApiResponse<Void> deleteWork(UUID id);

    ApiResponse<WorkResponse> updateWorkActivityStatus(UUID id, Boolean active);

    // Public API methods
    Page<WorkListPublicResponse> getAllPublicWorks(Pageable pageable);
    Page<WorkListPublicResponse> getAllPublicWorksByServiceId(UUID serviceId, Pageable pageable);
    WorkDetailPublicResponse getPublicWorkBySlug(String slug);
}
