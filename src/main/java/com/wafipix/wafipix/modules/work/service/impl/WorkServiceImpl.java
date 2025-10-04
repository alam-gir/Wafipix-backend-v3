package com.wafipix.wafipix.modules.work.service.impl;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.common.exception.ResourceNotFoundException;
import com.wafipix.wafipix.common.util.SlugUtil;
import com.wafipix.wafipix.modules.filemanagement.entity.File;
import com.wafipix.wafipix.modules.filemanagement.service.FileService;
import com.wafipix.wafipix.modules.service.repository.ServiceRepository;
import com.wafipix.wafipix.modules.work.dto.request.CreateWorkRequest;
import com.wafipix.wafipix.modules.work.dto.request.UpdateWorkRequest;
import com.wafipix.wafipix.modules.work.dto.response.WorkListResponse;
import com.wafipix.wafipix.modules.work.dto.response.WorkResponse;
import com.wafipix.wafipix.modules.work.dto.response.WorkListPublicResponse;
import com.wafipix.wafipix.modules.work.dto.response.WorkDetailPublicResponse;
import com.wafipix.wafipix.modules.work.entity.Work;
import com.wafipix.wafipix.modules.work.mapper.WorkMapper;
import com.wafipix.wafipix.modules.work.repository.WorkRepository;
import com.wafipix.wafipix.modules.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkServiceImpl implements WorkService {

    private final WorkRepository workRepository;
    private final ServiceRepository serviceRepository;
    private final FileService fileService;
    private final WorkMapper workMapper;

    @Override
    @Transactional
    public ApiResponse<WorkResponse> createWork(CreateWorkRequest request) {
        try {
            // Validate service exists
            com.wafipix.wafipix.modules.service.entity.Service serviceEntity = serviceRepository.findById(UUID.fromString(request.getServiceId()))
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            // Validate file requirements
            validateFileRequirements(request.getCoverVideo(), request.getCoverImage(), 
                    request.getProfileVideo(), request.getProfileImage());

            // Generate unique slug
            String baseSlug = SlugUtil.generateSlug(request.getTitle());
            String slug = SlugUtil.generateUniqueSlug(baseSlug, workRepository::existsBySlug);

            // Upload files
            File coverVideoFile = uploadFileIfPresent(request.getCoverVideo(), "works/cover-videos");
            File coverImageFile = uploadFileIfPresent(request.getCoverImage(), "works/cover-images");
            File profileVideoFile = uploadFileIfPresent(request.getProfileVideo(), "works/profile-videos");
            File profileImageFile = uploadFileIfPresent(request.getProfileImage(), "works/profile-images");

            // Create work entity
            Work work = Work.builder()
                    .title(request.getTitle())
                    .slug(slug)
                    .service(serviceEntity)
                    .description(request.getDescription())
                    .coverVideo(coverVideoFile)
                    .coverImage(coverImageFile)
                    .profileVideo(profileVideoFile)
                    .profileImage(profileImageFile)
                    .active(true)
                    .build();

            Work savedWork = workRepository.save(work);
            log.info("Work created successfully: {}", savedWork.getSlug());

            return ApiResponse.success(workMapper.toResponse(savedWork));

        } catch (Exception e) {
            log.error("Error creating work: {}", e.getMessage());
            return ApiResponse.error("Failed to create work: " + e.getMessage(), 500);
        }
    }

    @Override
    public ApiResponse<Page<WorkListResponse>> getAllWorks(Pageable pageable) {
        try {
            Page<Work> works = workRepository.findAll(pageable);
            Page<WorkListResponse> response = works.map(workMapper::toListResponse);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("Error fetching works: {}", e.getMessage());
            return ApiResponse.error("Failed to fetch works: " + e.getMessage(), 500);
        }
    }

    @Override
    public ApiResponse<WorkResponse> getWorkById(UUID id) {
        try {
            Work work = workRepository.findByIdWithFiles(id)
                    .orElseThrow(() -> new RuntimeException("Work not found"));
            return ApiResponse.success(workMapper.toResponse(work));
        } catch (Exception e) {
            log.error("Error fetching work by ID {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to fetch work: " + e.getMessage(), 500);
        }
    }

    @Override
    public ApiResponse<WorkResponse> getWorkBySlug(String slug) {
        try {
            Work work = workRepository.findBySlugWithFiles(slug)
                    .orElseThrow(() -> new RuntimeException("Work not found"));
            return ApiResponse.success(workMapper.toResponse(work));
        } catch (Exception e) {
            log.error("Error fetching work by slug {}: {}", slug, e.getMessage());
            return ApiResponse.error("Failed to fetch work: " + e.getMessage(), 500);
        }
    }

    @Override
    @Transactional
    public ApiResponse<WorkResponse> updateWork(UUID id, UpdateWorkRequest request) {
        try {
            Work work = workRepository.findByIdWithFiles(id)
                    .orElseThrow(() -> new RuntimeException("Work not found"));

            // Update basic fields
            if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
                work.setTitle(request.getTitle());
                // Generate new slug if title changed
                String baseSlug = SlugUtil.generateSlug(request.getTitle());
                String newSlug = SlugUtil.generateUniqueSlug(baseSlug, 
                        slug -> workRepository.existsBySlugAndIdNot(slug, id));
                work.setSlug(newSlug);
            }

            if (request.getDescription() != null) {
                work.setDescription(request.getDescription());
            }

            // Handle file updates
            updateFileIfPresent(work::setCoverVideo, request.getCoverVideo(), "works/cover-videos", work.getCoverVideo());
            updateFileIfPresent(work::setCoverImage, request.getCoverImage(), "works/cover-images", work.getCoverImage());
            updateFileIfPresent(work::setProfileVideo, request.getProfileVideo(), "works/profile-videos", work.getProfileVideo());
            updateFileIfPresent(work::setProfileImage, request.getProfileImage(), "works/profile-images", work.getProfileImage());

            Work savedWork = workRepository.save(work);
            log.info("Work updated successfully: {}", savedWork.getSlug());

            return ApiResponse.success(workMapper.toResponse(savedWork));

        } catch (Exception e) {
            log.error("Error updating work {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to update work: " + e.getMessage(), 500);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteWork(UUID id) {
        try {
            Work work = workRepository.findByIdWithFiles(id)
                    .orElseThrow(() -> new RuntimeException("Work not found"));

            // Store file URLs before deletion
            String coverVideoUrl = work.getCoverVideo() != null ? work.getCoverVideo().getPublicUrl() : null;
            String coverImageUrl = work.getCoverImage() != null ? work.getCoverImage().getPublicUrl() : null;
            String profileVideoUrl = work.getProfileVideo() != null ? work.getProfileVideo().getPublicUrl() : null;
            String profileImageUrl = work.getProfileImage() != null ? work.getProfileImage().getPublicUrl() : null;

            // Delete work first
            workRepository.delete(work);
            log.info("Work deleted successfully: {}", work.getSlug());

            // Clean up files after work deletion (outside transaction)
            cleanupFileByUrl(coverVideoUrl);
            cleanupFileByUrl(coverImageUrl);
            cleanupFileByUrl(profileVideoUrl);
            cleanupFileByUrl(profileImageUrl);

            return ApiResponse.success(null);

        } catch (Exception e) {
            log.error("Error deleting work {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to delete work: " + e.getMessage(), 500);
        }
    }

    @Override
    @Transactional
    public ApiResponse<WorkResponse> updateWorkActivityStatus(UUID id, Boolean active) {
        try {
            Work work = workRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Work not found"));

            work.setActive(active);
            Work savedWork = workRepository.save(work);
            log.info("Work activity status updated: {} -> {}", work.getSlug(), active);

            return ApiResponse.success(workMapper.toResponse(savedWork));

        } catch (Exception e) {
            log.error("Error updating work activity status {}: {}", id, e.getMessage());
            return ApiResponse.error("Failed to update work activity status: " + e.getMessage(), 500);
        }
    }

    private void validateFileRequirements(MultipartFile coverVideo, MultipartFile coverImage,
                                        MultipartFile profileVideo, MultipartFile profileImage) {
        boolean hasCover = coverVideo != null && !coverVideo.isEmpty() || 
                          coverImage != null && !coverImage.isEmpty();

        if (!hasCover) {
            throw new RuntimeException("At least one cover media (video or image) is required");
        }
        // Profile media is now optional - no validation needed
    }

    private File uploadFileIfPresent(MultipartFile file, String folderPath) {
        if (file != null && !file.isEmpty()) {
            validateFileType(file);
            return fileService.uploadFile(file, folderPath);
        }
        return null;
    }

    private void updateFileIfPresent(java.util.function.Consumer<File> setter, 
                                   MultipartFile newFile, String folderPath, File currentFile) {
        if (newFile != null && !newFile.isEmpty()) {
            validateFileType(newFile);
            
            // Clean up old file if it exists
            if (currentFile != null && currentFile.getPublicUrl() != null) {
                try {
                    fileService.deleteFileByUrl(currentFile.getPublicUrl());
                    log.info("Old file deleted successfully: {}", currentFile.getPublicUrl());
                } catch (Exception e) {
                    log.error("Error deleting old file {}: {}", currentFile.getPublicUrl(), e.getMessage());
                    // Continue with upload even if old file deletion fails
                }
            }
            
            // Upload new file
            File uploadedFile = fileService.uploadFile(newFile, folderPath);
            setter.accept(uploadedFile);
        }
    }

    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new RuntimeException("File type cannot be determined");
        }
        
        boolean isImage = contentType.startsWith("image/");
        boolean isVideo = contentType.startsWith("video/");
        
        if (!isImage && !isVideo) {
            throw new RuntimeException("File must be an image or video");
        }
    }

    private void cleanupFileByUrl(String fileUrl) {
        if (fileUrl != null && !fileUrl.trim().isEmpty()) {
            try {
                fileService.deleteFileByUrl(fileUrl);
                log.info("File deleted successfully: {}", fileUrl);
            } catch (Exception e) {
                log.error("Error deleting file {}: {}", fileUrl, e.getMessage());
                // Continue with deletion even if file cleanup fails
            }
        }
    }

    // Public API implementations
    @Override
    public Page<WorkListPublicResponse> getAllPublicWorks(Pageable pageable) {
        log.info("Fetching all active works for public display");
        
        Page<Work> works = workRepository.findAllByActive(true, pageable);
        Page<WorkListPublicResponse> response = works.map(workMapper::toPublicListResponse);
        
        log.info("Found {} active works for public display", response.getTotalElements());
        return response;
    }

    @Override
    public Page<WorkListPublicResponse> getAllPublicWorksByServiceId(UUID serviceId, Pageable pageable) {
        log.info("Fetching active works for service ID: {} for public display", serviceId);
        
        Page<Work> works = workRepository.findAllByServiceIdAndActive(serviceId, true, pageable);
        Page<WorkListPublicResponse> response = works.map(workMapper::toPublicListResponse);
        
        log.info("Found {} active works for service ID: {} for public display", response.getTotalElements(), serviceId);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public WorkDetailPublicResponse getPublicWorkBySlug(String slug) {
        log.info("Fetching work by slug for public display: {}", slug);
        
        Work work = workRepository.findBySlugWithFiles(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Active work not found with slug: " + slug));
        
        // Check if work is active
        if (!work.getActive()) {
            throw new ResourceNotFoundException("Work not found with slug: " + slug);
        }
        
        WorkDetailPublicResponse response = workMapper.toPublicDetailResponse(work);
        log.info("Found work for public display: {}", work.getTitle());
        return response;
    }
}
