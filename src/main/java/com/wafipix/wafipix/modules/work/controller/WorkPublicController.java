package com.wafipix.wafipix.modules.work.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.work.dto.response.WorkListPublicResponse;
import com.wafipix.wafipix.modules.work.dto.response.WorkDetailPublicResponse;
import com.wafipix.wafipix.modules.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v3/public/works")
@RequiredArgsConstructor
@Slf4j
public class WorkPublicController {

    private final WorkService workService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WorkListPublicResponse>>> getAllWorks(
            @RequestParam(required = false) UUID serviceId,
            @PageableDefault(size = 12, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("Public request for all active works with pagination, serviceId: {}", serviceId);
        
        Page<WorkListPublicResponse> response;
        if (serviceId != null) {
            response = workService.getAllPublicWorksByServiceId(serviceId, pageable);
        } else {
            response = workService.getAllPublicWorks(pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(response, "Active works retrieved successfully"));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<WorkDetailPublicResponse>> getWorkBySlug(
            @PathVariable String slug
    ) {
        log.info("Public request for work by slug: {}", slug);
        WorkDetailPublicResponse response = workService.getPublicWorkBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(response, "Work details retrieved successfully"));
    }
}
