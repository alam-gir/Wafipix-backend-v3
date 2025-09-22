package com.wafipix.wafipix.modules.work.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.work.dto.request.CreateGalleryRequest;
import com.wafipix.wafipix.modules.work.dto.request.CreateWorkRequest;
import com.wafipix.wafipix.modules.work.dto.request.RemoveGalleryFilesRequest;
import com.wafipix.wafipix.modules.work.dto.request.UpdateGalleryRequest;
import com.wafipix.wafipix.modules.work.dto.request.UpdateWorkRequest;
import com.wafipix.wafipix.modules.work.dto.response.GalleryResponse;
import com.wafipix.wafipix.modules.work.dto.response.WorkListResponse;
import com.wafipix.wafipix.modules.work.dto.response.WorkResponse;
import com.wafipix.wafipix.modules.work.service.GalleryService;
import com.wafipix.wafipix.modules.work.service.WorkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v3/admin/works")
@RequiredArgsConstructor
@Slf4j
public class WorkControllerAdmin {

    private final WorkService workService;
    private final GalleryService galleryService;

    // ==================== WORK OPERATIONS ====================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WorkResponse>> createWork(
            @Valid @ModelAttribute CreateWorkRequest request) {
        log.info("Creating work: {}", request.getTitle());
        return ResponseEntity.ok(workService.createWork(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<WorkListResponse>>> getAllWorks(Pageable pageable) {
        log.info("Fetching all works with pagination");
        return ResponseEntity.ok(workService.getAllWorks(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WorkResponse>> getWorkById(@PathVariable UUID id) {
        log.info("Fetching work by ID: {}", id);
        return ResponseEntity.ok(workService.getWorkById(id));
    }

    @GetMapping("/slug/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WorkResponse>> getWorkBySlug(@PathVariable String slug) {
        log.info("Fetching work by slug: {}", slug);
        return ResponseEntity.ok(workService.getWorkBySlug(slug));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WorkResponse>> updateWork(
            @PathVariable UUID id,
            @Valid @ModelAttribute UpdateWorkRequest request) {
        log.info("Updating work: {}", id);
        return ResponseEntity.ok(workService.updateWork(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteWork(@PathVariable UUID id) {
        log.info("Deleting work: {}", id);
        return ResponseEntity.ok(workService.deleteWork(id));
    }

    @PutMapping("/{id}/activity-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WorkResponse>> updateWorkActivityStatus(
            @PathVariable UUID id,
            @RequestParam Boolean active) {
        log.info("Updating work activity status: {} -> {}", id, active);
        return ResponseEntity.ok(workService.updateWorkActivityStatus(id, active));
    }

    // ==================== GALLERY OPERATIONS ====================

    @GetMapping("/{workId}/galleries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<GalleryResponse>>> getWorkGalleries(@PathVariable UUID workId) {
        log.info("Fetching galleries for work: {}", workId);
        return ResponseEntity.ok(galleryService.getWorkGalleries(workId));
    }

    @PostMapping("/{workId}/galleries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GalleryResponse>> createGallery(
            @PathVariable UUID workId,
            @Valid @ModelAttribute CreateGalleryRequest request) {
        log.info("Creating gallery for work: {}", workId);
        return ResponseEntity.ok(galleryService.createGallery(workId, request));
    }

    @PutMapping("/galleries/{galleryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GalleryResponse>> updateGallery(
            @PathVariable UUID galleryId,
            @Valid @RequestBody UpdateGalleryRequest request) {
        log.info("Updating gallery: {}", galleryId);
        return ResponseEntity.ok(galleryService.updateGallery(galleryId, request));
    }

    @DeleteMapping("/galleries/{galleryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteGallery(@PathVariable UUID galleryId) {
        log.info("Deleting gallery: {}", galleryId);
        return ResponseEntity.ok(galleryService.deleteGallery(galleryId));
    }

    @PostMapping("/galleries/{galleryId}/files")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GalleryResponse>> addFilesToGallery(
            @PathVariable UUID galleryId,
            @RequestParam("files") List<MultipartFile> files) {
        log.info("Adding {} files to gallery: {}", files.size(), galleryId);
        return ResponseEntity.ok(galleryService.addFilesToGallery(galleryId, files));
    }

    @DeleteMapping("/galleries/{galleryId}/files")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GalleryResponse>> removeFilesFromGallery(
            @PathVariable UUID galleryId,
            @Valid @RequestBody RemoveGalleryFilesRequest request) {
        log.info("Removing {} gallery items from gallery: {}", request.getGalleryItemIds().size(), galleryId);
        return ResponseEntity.ok(galleryService.removeFilesFromGallery(galleryId, request.getGalleryItemIds()));
    }
}
